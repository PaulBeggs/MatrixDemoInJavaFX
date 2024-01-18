package matrix.gui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import matrix.fileManaging.FilePath;
import matrix.fileManaging.MatrixType;
import matrix.model.*;
import matrix.fileManaging.MatrixFileHandler;
import javafx.fxml.FXML;

import matrix.operators.MatrixInputHandler;
import matrix.operators.MatrixUtil;
import matrix.view.MatrixView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class MatrixController implements DataManipulation {
    @FXML
    BorderPane borderPane;
    @FXML
    Button generateButton, saveButton, operationButton, identityButton, transposeButton;
    @FXML
    TextField sizeColsField, sizeRowsField, targetRow, sourceRow, multiplier;
    @FXML
    TextArea directions;
    @FXML
    ChoiceBox<Scenes> scenes;
    @FXML
    ChoiceBox<String> operations;
    @FXML
    GridPane matrixGrid;
    private MatrixView matrixView;
    private int steps = 1;
    private MatrixCell[][] matrixCells;
    private final MatrixInputHandler MIH = new MatrixInputHandler();

    private final String initialDirections =
            """
                    Click 'generate' to produce a Matrix. The cells are editable; use 'tab' to go through each cell and add an entry.""";

    @FXML
    private void initialize() {
        update();
        matrixView = new MatrixView(matrixGrid, matrixCells);
        setupTextFieldListeners();
        setupOperationsDropdown();
        setupScenesDropdown();
        setupDirectionText();
        System.out.println("(initialize) Accessing the singleton inside of the matrix cells (matrixController): ");
        System.out.println("Accessed " + steps++ + " in order.");
        Matrix matrix = MatrixSingleton.getInstance();
        matrixView.updateViews(true, matrix);
        setInitTextFieldData();
    }

    private void setupTextFieldListeners() {
        objectOnlyListener(sourceRow, ObjectType.INTEGER);
        objectOnlyListener(targetRow, ObjectType.INTEGER);
        objectOnlyListener(sizeRowsField, ObjectType.INTEGER);
        objectOnlyListener(sizeColsField, ObjectType.INTEGER);
        objectOnlyListener(multiplier, ObjectType.DOUBLE);
    }

    private void setupOperationsDropdown() {
        operations.getItems().addAll("Swap Rows", "Multiply Rows", "Add Rows");
        operations.setValue("Swap Rows");
    }

    @Override
    public void setupScenesDropdown() {
        scenes.getItems().setAll(Scenes.values());
        scenes.setValue(Scenes.MATRIX);
        scenes.setTooltip(new Tooltip("Pick the scene"));
        scenes.setOnAction(event -> {
            Scenes selectedScene = scenes.getValue();
            try {
                selectedScene.switchScene(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void setupDirectionText() {
        directions.setText(initialDirections);
        directions.setWrapText(true);
        directions.setEditable(false);
    }

    @FXML
    public void handleGenerateButton() {
        updateMatrixGrid(false, getNewMatrixData()); // false indicates it's not an identity matrix
        saveIdentityCopy(getIdentityMatrixData());
    }

    @FXML
    public void handleIdentityButton() {
        updateMatrixGrid(true, getIdentityMatrixData()); // true indicates it's an identity matrix
    }

    private void updateMatrixGrid(Boolean identityCheck, List<List<String>> matrixData) {
        Matrix newMatrix = new Matrix(matrixData.size(), matrixData.getFirst().size());
        MatrixSingleton.resizeInstance(matrixData.size(), matrixData.getFirst().size());
        matrixGrid.getChildren().clear();

        setupMatrixCells(newMatrix, matrixData, identityCheck);

        System.out.println("this is the matrix after updating: \n" + newMatrix);
        System.out.println("These are the matrix cells: \n" + MatrixUtil.matrixCellsToString(matrixCells));
        MatrixSingleton.setInstance(newMatrix);
        MatrixSingleton.saveMatrix();
        matrixView.updateViews(true, newMatrix);
    }

    private List<List<String>> getNewMatrixData() {
        if (MIH.isPositiveIntValid(sizeColsField) && MIH.isPositiveIntValid(sizeRowsField)) {
            int numRows = Integer.parseInt(sizeRowsField.getText());
            int numCols = Integer.parseInt(sizeColsField.getText());
            return generateMatrixData(numRows, numCols, (row, col) -> {
                int cellValue = (int) Math.floor(Math.random() * 10);
                return String.valueOf(cellValue);
            });
        } else {
            System.out.println("Invalid input for matrix dimensions.");
            temporarilyUpdateDirections("Invalid input for matrix dimensions.");
            sizeColsField.setText("1");
            sizeRowsField.setText("1");
            return Collections.emptyList(); // Return an empty list if inputs are invalid
        }
    }

    private List<List<String>> getIdentityMatrixData() {
        if (MIH.isPositiveIntValid(sizeColsField) && MIH.isPositiveIntValid(sizeRowsField)) {
            int numRows = Integer.parseInt(sizeRowsField.getText());
            int numCols = Integer.parseInt(sizeColsField.getText());
            return generateMatrixData(numRows, numCols, (row, col) -> (Objects.equals(row, col)) ? "1.0" : "0.0");
        } else {
            temporarilyUpdateDirections("Invalid input for identity matrix dimensions");
            sizeColsField.setText("1");
            sizeRowsField.setText("1");
            return Collections.emptyList(); // Return an empty list if inputs are invalid
        }
    }

    private List<List<String>> generateMatrixData(int numRows, int numCols, BiFunction<Integer, Integer, String> cellValueFunction) {
        List<List<String>> matrixData = new ArrayList<>();
        for (int row = 0; row < numRows; row++) {
            List<String> rowData = new ArrayList<>();
            for (int col = 0; col < numCols; col++) {
                rowData.add(cellValueFunction.apply(row, col));
            }
            matrixData.add(rowData);
        }
        return matrixData;
    }

    @Override
    public void update() {
        List<List<String>> matrixData = MatrixFileHandler.loadMatrixDataFromFile(FilePath.MATRIX_PATH.getPath());

        if (!matrixData.isEmpty()) {
            populateMatrixUI(matrixData);
        } else {
            System.out.println("Populating matrix because it is empty...");
            MatrixFileHandler.populateMatrixIfEmpty();
        }
    }

    private void populateMatrixUI(List<List<String>> matrixData) {
        System.out.println("(populate) Accessing the singleton inside of the matrix cells (matrixController): \n");
        System.out.println("Accessed " + steps++ + " in order.");
        Matrix matrix = MatrixSingleton.getInstance();
        MatrixSingleton.resizeInstance(matrixData.size(), matrixData.get(0).size());
        setupMatrixCells(matrix, matrixData, false);
        System.out.println("This is the matrix that is initially created: \n" + matrix);
        System.out.println("These are the matrix cells: \n" + MatrixUtil.matrixCellsToString(matrixCells));
    }


    private void setupMatrixCells(Matrix matrix, List<List<String>> matrixData, boolean identityCheck) {
        int numRows = matrixData.size();
        int numCols = matrixData.getFirst().size();
        matrixCells = new MatrixCell[numRows][numCols];

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                String cellValue = formatCellValue(matrixData.get(row).get(col), identityCheck, row == col);
                matrixCells[row][col] = new MatrixCell(row, col, cellValue, true);
                matrix.setValue(row, col, cellValue);
            }
        }
    }

    private String formatCellValue(String value, boolean identityCheck, boolean isDiagonal) {
        if (identityCheck) {
            return isDiagonal ? "1" : "0";
        }
        return value;
    }

    private void updateMatrixFromMatrixCells(MatrixCell[][] matrixCells) {
        System.out.println("Accessing the singleton inside of the matrix cells (matrixController): ");
        System.out.println("Accessed " + steps++ + " in order. \n");
        Matrix matrix = MatrixSingleton.getInstance();
        for (int row = 0; row < matrixCells.length; row++) {
            for (int col = 0; col < matrixCells[row].length; col++) {
                String cellValue = matrixCells[row][col].getTextField().getText();
                try {
                    matrix.setValue(row, col, cellValue);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format in cell [" + row + "][" + col + "]");
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void saveIdentityCopy(List<List<String>> identityMatrixData) {
        int numRows = identityMatrixData.size();
        int numCols = identityMatrixData.getFirst().size();

        Matrix iMatrix = new Matrix(numRows, numCols);
//        System.out.println("pre-updated matrix after instance is retrieved: \n" + iMatrix);

        MatrixCell[][] iMatrixCells = new MatrixCell[numRows][numCols];

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                String cellValue = row == col ? "1" : "0";

                iMatrixCells[row][col] = new MatrixCell(row, col, cellValue, true);
                iMatrix.setValue(row, col, cellValue);
            }
        }
        MatrixFileHandler.saveMatrixDataToFile(FilePath.IDENTITY_PATH.getPath(), 0, identityMatrixData, MatrixType.IDENTITY);
    }

    @Override
    @FXML
    public void handleSaveButton() {
        Stage saveStage = new Stage();
        saveStage.setTitle("Save Menu");

        saveStage.initModality(Modality.WINDOW_MODAL);
        saveStage.initOwner(MatrixApp.getPrimaryStage());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/SaveScene.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Scene saveScene = new Scene(root);
        MatrixApp.setupGlobalEscapeHandler(saveScene);
        MatrixApp.applyTheme(saveScene);
        saveStage.setScene(saveScene);
        saveStage.showAndWait();
    }

    @FXML
    public void handleLoadButton() {
        FileChooser fileChooser = new FileChooser();

        File initialDir = new File(System.getProperty("user.dir"), "SavedMatrices/matrices");
        if (!initialDir.exists()) {
            initialDir = new File(System.getProperty("user.dir"));
        }
        fileChooser.setInitialDirectory(initialDir);

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            String filePath = file.getAbsolutePath();

            // Load the matrix data from the selected file
            List<List<String>> matrixData = MatrixFileHandler.loadMatrixDataFromFile(filePath);

            // Convert the loaded data into a Matrix object
            updateMatrixGrid(false, matrixData);
            updateMatrixFromMatrixCells(matrixCells); // Update the shared instance
        }
    }
    
    private enum ObjectType {
        INTEGER,
        DOUBLE
    }

    @FXML
    private void performOperation() {
        Matrix matrix = MatrixSingleton.getInstance();
        String selectedOption = operations.getValue();

        if (MIH.isRowValid(targetRow, matrix.getRows()) || MIH.isRowValid(sourceRow, matrix.getRows())) {
            System.out.println("At least one row is invalid. Fix it to proceed.");
            temporarilyUpdateDirections("At least one row is invalid. Fix it to proceed.");
            return;
        }

        int targetRowIndex = Integer.parseInt(targetRow.getText()) - 1;
        int sourceRowIndex = Integer.parseInt(sourceRow.getText()) - 1;

        switch (selectedOption) {
            case "Swap Rows":
                matrix.swapRows(targetRowIndex, sourceRowIndex);
                break;
            case "Multiply Rows":
                handleRowOperation(targetRowIndex, multiplier, matrix::multiplyRow);
                break;
            case "Add Rows":
                handleRowOperation(targetRowIndex, multiplier, (index, value) -> matrix.addRows(targetRowIndex, sourceRowIndex, value));
                break;
            default:
                throw new IllegalStateException("How did you fuck this up?");
        }
        matrixView.updateViews(true, matrix);
    }

    private void handleRowOperation(int targetRowIndex, TextField multiplierField, BiConsumer<Integer, Double> operationFunction) {
        if (!MIH.isDoubleValid(multiplierField)) {
            System.out.println("Invalid Multiplier. Please enter a valid double.");
            temporarilyUpdateDirections("Invalid Multiplier. Please enter a valid double.");
            return;
        }
        double value = 0;
        try {
            value = Double.parseDouble(multiplierField.getText());
            // Use 'value' as needed
        } catch (NumberFormatException e) {
            // Handle the case where the text is not a valid number
            System.out.println("Invalid number format");
        }

        operationFunction.accept(targetRowIndex, value);
    }

    @FXML
    public void handleTransposeButton() {

    }

    // Method to update the directions text area
    private void temporarilyUpdateDirections(String newDirections) {
        directions.setText(newDirections);

        // Schedule resetting the directions text area after 10 seconds
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> {
            Platform.runLater(() -> directions.setText(initialDirections));
            executorService.shutdown(); // Important to shut down the executor to prevent resource leaks
        }, 6, TimeUnit.SECONDS);
    }

    private void objectOnlyListener(TextField textField, ObjectType objectType) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            String regex = switch (objectType) {
                case INTEGER -> "-?\\d*"; // Allows negative integers
                case DOUBLE -> "-?\\d*(\\.\\d*)?"; // Allows negative doubles and decimals
            };

            if (!newValue.matches(regex)) {
                // The newValue is not a valid format, so don't change the text field.
                // This is to prevent invalid inputs like "--" or "3.-" or multiple dots.
                if (newValue.isEmpty()) {
                    textField.setText(""); // If the new value is empty, allow it to clear the field
                } else {
                    textField.setText(oldValue); // Otherwise, revert to the old value
                }
            }
        });
    }

    public void setInitTextFieldData() {
        String filePath = FilePath.MATRIX_PATH.getPath();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            sizeRowsField.setText(String.valueOf(0));
            sizeColsField.setText(String.valueOf(0));

            String firstLine = br.readLine();
            if (firstLine != null) {
                String[] values = firstLine.split("\\s+");
                sizeColsField.setText(String.valueOf(values.length));
            }

            int i = 1;
            while (br.readLine() != null) {
                i++;
            }
            sizeRowsField.setText(String.valueOf(i));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}