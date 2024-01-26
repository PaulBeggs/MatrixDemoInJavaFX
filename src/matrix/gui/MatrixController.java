package matrix.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import matrix.fileManaging.FilePath;
import matrix.fileManaging.MatrixFileHandler;
import matrix.fileManaging.MatrixType;
import matrix.model.Matrix;
import matrix.model.MatrixCell;
import matrix.model.MatrixSingleton;
import matrix.util.*;
import matrix.view.MatrixView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static matrix.util.TextFieldListeners.objectOnlyListener;

public class MatrixController implements DataManipulation {
    @FXML
    BorderPane borderPane;
    @FXML
    Button generateButton, saveButton, operationButton, identityButton, transposeButton, operationSummaryButton, loadButton, clearSummaryButton;
    @FXML
    TextField sizeColsField, sizeRowsField, targetRow, sourceRow, multiplier;
    @FXML
    ChoiceBox<Scenes> scenes;
    @FXML
    ChoiceBox<String> operations;
    @FXML
    GridPane matrixGrid;
    private MatrixView matrixView;
    private int step = 1;
    private MatrixCell[][] matrixCells;
    private final MatrixInputHandler MIH = new MatrixInputHandler();
    private List<String> operationsSummary = new ArrayList<>();
    @FXML
    private void initialize() {
        update();
        matrixView = new MatrixView(matrixGrid, matrixCells);
        setupTextFieldListeners();
        setupOperationsDropdown();
        setupScenesDropdown();
        Matrix matrix = MatrixSingleton.getInstance();
        matrixView.updateViews(true, matrix);
        setToolTips();
        setInitTextFieldData();
    }

    private void setupTextFieldListeners() {
        // static import of MatrixUtil and ObjectType to cut down on clutter.
        objectOnlyListener(sourceRow, ObjectType.INTEGER);
        objectOnlyListener(targetRow, ObjectType.INTEGER);
        objectOnlyListener(sizeRowsField, ObjectType.INTEGER);
        objectOnlyListener(sizeColsField, ObjectType.INTEGER);
        TextFieldListeners.addEnterListener(multiplier);
    }

    private void setupOperationsDropdown() {
        operations.getItems().addAll("Swap Rows", "Multiply Row", "Add Rows");
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
                ErrorsAndSyntax.showErrorPopup("Unable to load the scene: " + scenes.getValue());
                throw new IllegalArgumentException(e);
            }
        });
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
            ErrorsAndSyntax.showErrorPopup("Invalid input for matrix dimensions.");
            sizeColsField.setText("1");
            sizeRowsField.setText("1");
            return Collections.emptyList(); // Return an empty list if inputs are invalid
        }
    }

    private List<List<String>> getIdentityMatrixData() {
        if (MIH.isPositiveIntValid(sizeColsField) && MIH.isPositiveIntValid(sizeRowsField)) {
            int numRows = Integer.parseInt(sizeRowsField.getText());
            int numCols = Integer.parseInt(sizeColsField.getText());
            return generateMatrixData(numRows, numCols, (row, col) -> (Objects.equals(row, col)) ? "1" : "0");
        } else {
            ErrorsAndSyntax.showErrorPopup("Invalid input for identity matrix dimensions");
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
        Matrix matrix = MatrixSingleton.getInstance();
        MatrixSingleton.resizeInstance(matrixData.size(), matrixData.getFirst().size());
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
        Matrix matrix = MatrixSingleton.getInstance();
        for (int row = 0; row < matrixCells.length; row++) {
            for (int col = 0; col < matrixCells[row].length; col++) {
                String cellValue = matrixCells[row][col].getTextField().getText();
                try {
                    matrix.setValue(row, col, cellValue);
                } catch (NumberFormatException e) {
                    ErrorsAndSyntax.showErrorPopup("Invalid number format in cell [" + row + "][" + col + "]");
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void saveIdentityCopy(List<List<String>> identityMatrixData) {
        int numRows = identityMatrixData.size();
        int numCols = identityMatrixData.getFirst().size(); // Use get(0) instead of getFirst()

        Matrix iMatrix = new Matrix(numRows, numCols);

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                String cellValue = row == col ? "1" : "0";
                iMatrix.setValue(row, col, cellValue);
            }
        }

        MatrixFileHandler.saveMatrixDataToFile(FilePath.IDENTITY_PATH.getPath(), "0", identityMatrixData, MatrixType.IDENTITY);
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
            ErrorsAndSyntax.showErrorPopup("Unable to load the Save Scene.");
            throw new IllegalArgumentException(e);
        }

        Scene saveScene = new Scene(root);
        saveScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                saveStage.close();
            }
        });

        SaveController saveController = loader.getController();
        saveController.setStage(saveStage);

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

    @FXML
    private void performOperation() {
        Matrix matrix = MatrixSingleton.getInstance();
        String selectedOption = operations.getValue();

        if (MIH.isRowValid(targetRow, matrix.getRows()) || MIH.isRowValid(sourceRow, matrix.getRows())) {
            System.out.println("At least one row is invalid. Fix it to proceed.");
            ErrorsAndSyntax.showErrorPopup("At least one row is invalid. Fix it to proceed.");
            return;
        }

        int targetRowIndex = Integer.parseInt(targetRow.getText()) - 1;
        int sourceRowIndex = Integer.parseInt(sourceRow.getText()) - 1;

        switch (selectedOption) {
            case "Swap Rows":
                operationsSummary.add("#" + step++ + ", Swapped rows: " + (targetRowIndex + 1) + ", " + (sourceRowIndex + 1) + "\n");
                matrix.swapRows(targetRowIndex, sourceRowIndex);
                break;
            case "Multiply Row":
                operationsSummary.add("#" + step++ + ", Multiplied row: " + (targetRowIndex + 1) + " by " + multiplier + "\n");
                handleRowOperation(targetRowIndex, multiplier, matrix::multiplyRow);
                break;
            case "Add Rows":
                operationsSummary.add("#" + step++ + ", Multiplied row: " + (sourceRowIndex + 1) + " by " + multiplier + ", and added it to" + (targetRowIndex + 1) + "\n");
                handleRowOperation(targetRowIndex, multiplier, (index, value) -> matrix.addRows(sourceRowIndex, targetRowIndex, value));
                break;
            default:
                throw new IllegalStateException("How did you fuck this up?");
        }
        MatrixSingleton.saveMatrix();
        matrixView.updateViews(true, matrix);
    }

    private void handleRowOperation(int targetRowIndex, TextField multiplierField, BiConsumer<Integer, Double> operationFunction) {
        if (!MIH.isDoubleValid(multiplierField)) {
            System.out.println("Invalid Multiplier. Please enter a valid double.");
            ErrorsAndSyntax.showErrorPopup("Invalid Multiplier. Please enter a valid double.");
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
        Matrix matrix = MatrixSingleton.getInstance();
        matrix.transpose();
        MatrixSingleton.saveMatrix();
        matrixView.updateViews(true, matrix);
        String rows = String.valueOf(matrix.getRows());
        String cols = String.valueOf(matrix.getCols());
        sizeRowsField.setText(rows);
        sizeColsField.setText(cols);
    }

    @FXML
    public void handleOperationSummary() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Matrix Operations Summary");
        alert.setHeaderText("Operations Performed on This Matrix Instance");

        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setText(String.join("\n", operationsSummary));

        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }

    @FXML
    public void handleResetOperationSummary() {
        operationsSummary = new ArrayList<>();
        step = 1;
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

    @FXML
    public void handleShowInformation() {
        try {
            // Load layout from FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/mainInformation.fxml"));
            Parent infoLayout = loader.load();

            // Create a Scene with the layout
            Scene scene = new Scene(infoLayout);

            // Add key event handler for ESCAPE key to close the window
            scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    ((Stage) scene.getWindow()).close();
                }
            });

            // Create a new Stage (window) and set the scene
            Stage infoStage = new Stage();
            infoStage.setTitle("Information");
            infoStage.setScene(scene);

            // Show the stage
            infoStage.show();
        } catch (Exception e) {
            ErrorsAndSyntax.showErrorPopup("Unable to show the information.");
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void setToolTips() {
        generateButton.setTooltip(new Tooltip("Generate a random matrix"));
        saveButton.setTooltip(new Tooltip("Save the matrix"));
        loadButton.setTooltip(new Tooltip("Load a matrix"));
        operationButton.setTooltip(new Tooltip("Operate on matrix"));
        identityButton.setTooltip(new Tooltip("Set matrix to 1s and 0s"));
        transposeButton.setTooltip(new Tooltip("Transpose the matrix"));
        operationSummaryButton.setTooltip(new Tooltip("Shows operations"));
        clearSummaryButton.setTooltip(new Tooltip("Clears operations"));
        operations.setTooltip(new Tooltip("Elementary row operations"));
        sizeRowsField.setTooltip(new Tooltip("Specify number of rows"));
        sizeColsField.setTooltip(new Tooltip("Specify number of columns"));
        targetRow.setTooltip(new Tooltip("Specify target row"));
        sourceRow.setTooltip(new Tooltip("Specify source row"));
        multiplier.setTooltip(new Tooltip("Change multiplier"));
    }
}