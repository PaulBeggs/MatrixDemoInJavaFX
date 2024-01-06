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
import matrix.model.Matrix;
import matrix.fileManaging.MatrixFileHandler;
import matrix.model.MatrixCell;
import matrix.model.MatrixSingleton;
import matrix.model.MatrixView;
import javafx.fxml.FXML;

import matrix.operators.ElementaryMatrixOperations;
import matrix.operators.MatrixInputHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MatrixController implements DataManipulation {
    @FXML
    BorderPane borderPane;
    @FXML
    Button generateButton, saveButton, operationButton, identityButton, fastFactsButton;
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
    private MatrixCell[][] matrixCells;
    private final MatrixInputHandler MIH = new MatrixInputHandler();

    private final String initialDirections =
            """
                    Click 'generate' to produce a Matrix. The cells are editable; use 'tab' to go through each cell and add an entry.""";

    private final ScheduledExecutorService autoSaveExecutor = Executors.newSingleThreadScheduledExecutor();

    @FXML
    private void initialize() {
        update();
        matrixView = new MatrixView(matrixGrid, matrixCells, borderPane);
        setupAutoSave();
        setupTextFieldListeners();
        setupOperationsDropdown();
        setupScenesDropdown();
        setupDirectionText();
        matrixView.updateViews(true);
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

    private void setupScenesDropdown() {
        scenes.getItems().setAll(Scenes.values());
        scenes.setValue(Scenes.MATRIX);
        scenes.setTooltip(new Tooltip("Pick the scene"));
        scenes.setOnAction(event -> {
            Scenes selectedScene = scenes.getValue();
            try {
                updateMatrixFromMatrixCells(matrixCells); // Update the shared instance
                MatrixSingleton.saveMatrix(); // Scenes dropdown
                stopAutoSave();
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
        List<List<String>> newMatrixData = getNewMatrixData();
        assert newMatrixData != null;
        updateMatrixGrid(false, newMatrixData); // false indicates it's not an identity matrix
    }

    @FXML
    public void handleIdentityButton() {
        List<List<String>> identityMatrixData = getIdentityMatrixData();
        assert identityMatrixData != null;
        updateMatrixGrid(true, identityMatrixData); // true indicates it's an identity matrix
    }

    private List<List<String>> getNewMatrixData() {
        if (MIH.isPositiveIntValid(sizeColsField) && MIH.isPositiveIntValid(sizeRowsField)) {
            int numRows = Integer.parseInt(sizeRowsField.getText());
            int numCols = Integer.parseInt(sizeColsField.getText());

            List<List<String>> matrixData = new ArrayList<>();
            for (int row = 0; row < numRows; row++) {
                List<String> rowData = new ArrayList<>();
                for (int col = 0; col < numCols; col++) {
                    // For a regular matrix, initialize with random/default values
                    double cellValue = Math.floor(Math.random() * 100); // or another initialization logic
                    rowData.add(String.valueOf(cellValue));
                }
                matrixData.add(rowData);
            }
            return matrixData;
        } else {
            System.out.println("Invalid input for matrix dimensions.");
            temporarilyUpdateDirections("Invalid input for matrix dimensions.");
            return null;
        }
    }

    private void updateMatrixGrid(Boolean identityCheck, List<List<String>> matrixData) {
        int numRows = matrixData.size();
        int numCols = matrixData.getFirst().size();

        Matrix newMatrix = new Matrix(numRows, numCols);
        System.out.println("pre-updated matrix after instance is retrieved: \n" + newMatrix);
        MatrixSingleton.resizeInstance(numRows, numCols);

        matrixGrid.getChildren().clear();
        this.matrixCells = new MatrixCell[numRows][numCols];

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                double cellValue = identityCheck && row == col ? 1.0 :
                        (identityCheck ? 0.0 : Double.parseDouble(matrixData.get(row).get(col)));

                matrixCells[row][col] = new MatrixCell(row, col, String.valueOf(cellValue), true);
                matrixGrid.add(matrixCells[row][col].getTextField(), col, row);
                newMatrix.setValue(row, col, cellValue);
            }
        }
        System.out.println("this is the matrix after updating: \n" + newMatrix);
        System.out.println("These are the matrix cells: \n" + matrixCellsToString(matrixCells));
        MatrixSingleton.setInstance(newMatrix);
        MatrixSingleton.saveMatrix(); // Save Matrix to File.
        matrixView.updateViews(true);
    }


    private List<List<String>> getIdentityMatrixData() {
        if (MIH.isPositiveIntValid(sizeColsField) && MIH.isPositiveIntValid(sizeRowsField)) {
            int numRows = Integer.parseInt(sizeRowsField.getText());
            int numCols = Integer.parseInt(sizeColsField.getText());

            List<List<String>> identityMatrixData = new ArrayList<>();
            for (int row = 0; row < numRows; row++) {
                List<String> rowData = new ArrayList<>();
                for (int col = 0; col < numCols; col++) {
                    // For an identity matrix, you'd check if row == col for 1.0, else 0.0
                    double cellValue = Double.parseDouble((row == col) ? "1.0" : "0.0"); // Identity matrix logic
                    rowData.add(String.valueOf(cellValue));
                }
                identityMatrixData.add(rowData);
            }
            return identityMatrixData;
        } else {
            temporarilyUpdateDirections("Invalid input for identity matrix dimensions");
            return null;
        }
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

        SaveController saveController = loader.getController();
        saveController.setMatrixCells(matrixCells);
        saveController.setStage(saveStage);

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
            updateMatrixGrid(false, matrixData); // Use the modified updateMatrixGrid
            updateMatrixFromMatrixCells(matrixCells); // Update the shared instance
        }
    }

    @FXML
    private void performOperation() {
        Matrix matrix = MatrixSingleton.getInstance();
        ElementaryMatrixOperations EMO = new ElementaryMatrixOperations(matrix);
        String selectedOption = operations.getValue();

        if (MIH.isRowValid(targetRow, matrix.getRows()) && MIH.isRowValid(sourceRow, matrix.getRows())) {
            int targetRowIndex = Integer.parseInt(targetRow.getText()) - 1;
            int sourceRowIndex = Integer.parseInt(sourceRow.getText()) - 1;


            switch (selectedOption) {
                case "Swap Rows" -> {
                    EMO.swapRows(targetRowIndex, sourceRowIndex);
                    updateSwappedMatrixUI(targetRowIndex, sourceRowIndex);

                }
                case "Multiply Rows", "Add Rows" -> {
                    if (MIH.isDoubleValid(multiplier)) {
                        double rowMultiplier = Double.parseDouble(multiplier.getText());

                        if (selectedOption.equals("Multiply Rows")) {
                            EMO.multiplyRow(targetRowIndex, rowMultiplier);
                            updateMultipliedMatrixUI(targetRowIndex, rowMultiplier);

                        } else {
                            EMO.addRows(targetRowIndex, sourceRowIndex, rowMultiplier);
                            updateAddedMatrixUI(targetRowIndex, sourceRowIndex, rowMultiplier);
                        }

                    } else {
                        System.out.println("Invalid Multiplier. Please enter a valid double.");
                        temporarilyUpdateDirections("Invalid Multiplier. Please enter a valid double.");
                    }
                }
            }
            updateMatrixFromMatrixCells(matrixCells); // Update the shared instance
            MatrixSingleton.saveMatrix(); // Save the shared instance to file
        } else {
            System.out.println("At least one row is invalid. Fix it to proceed.");
            temporarilyUpdateDirections("At least one row is invalid. Fix it to proceed.");
        }
    }


    // To cut down on duplicated code and add some abstraction to the project:
    private enum MatrixOperation {
        SWAP,
        MULTIPLY,
        ADD
    }
    private enum ObjectType {
        INTEGER,
        DOUBLE
    }

    private void updateMatrixUIFromOperations(int rowIndex, int numCols, MatrixOperation operation, int sourceRowIndex, double rowMultiplier) {
        try {
            for (int col = 0; col < numCols; col++) {
                double targetValue = Double.parseDouble(matrixCells[rowIndex][col].getTextField().getText());
                double newValue;

                switch (operation) {
                    case SWAP -> {
                        newValue = Double.parseDouble(matrixCells[sourceRowIndex][col].getTextField().getText());
                        matrixCells[sourceRowIndex][col].getTextField().setText(String.valueOf(targetValue));
                    }
                    case MULTIPLY -> newValue = targetValue * rowMultiplier;
                    case ADD -> {
                        double sourceValue = Double.parseDouble(matrixCells[sourceRowIndex][col].getTextField().getText());
                        newValue = targetValue + rowMultiplier * sourceValue;
                    }
                    default -> throw new IllegalArgumentException("Unsupported operation");
                }

                matrixCells[rowIndex][col].getTextField().setText(String.valueOf(newValue));
            }
            updateMatrixFromMatrixCells(matrixCells); // Update the shared instance
            MatrixSingleton.saveMatrix(); // Save the shared instance to file
        } catch (NumberFormatException e) {
            System.out.println("Error converting text to double: " + e.getMessage());
            temporarilyUpdateDirections("Error converting text to double.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An unexpected error occurred: " + e.getMessage());
            temporarilyUpdateDirections("An unexpected error occurred.");
        }
    }

    private void updateSwappedMatrixUI(int targetRowIndex, int sourceRowIndex) {
        Matrix matrix = MatrixSingleton.getInstance();
        updateMatrixUIFromOperations(targetRowIndex, matrix.getCols(), MatrixOperation.SWAP, sourceRowIndex, 0);
        updateMatrixFromMatrixCells(matrixCells); // Update the shared instance
        MatrixSingleton.saveMatrix(); // Save the shared instance to file
    }

    private void updateMultipliedMatrixUI(int targetRowIndex, double rowMultiplier) {
        Matrix matrix = MatrixSingleton.getInstance();
        updateMatrixUIFromOperations(targetRowIndex, matrix.getCols(), MatrixOperation.MULTIPLY, 0, rowMultiplier);
        updateMatrixFromMatrixCells(matrixCells); // Update the shared instance
        MatrixSingleton.saveMatrix(); // Save the shared instance to file
    }

    private void updateAddedMatrixUI(int targetRowIndex, int sourceRowIndex, double rowMultiplier) {
        Matrix matrix = MatrixSingleton.getInstance();
        updateMatrixUIFromOperations(targetRowIndex, matrix.getCols(), MatrixOperation.ADD, sourceRowIndex, rowMultiplier);
        updateMatrixFromMatrixCells(matrixCells); // Update the shared instance
        MatrixSingleton.saveMatrix(); // Save the shared instance to file
    }

    @Override
    public void update() {
        List<List<String>> matrixData = MatrixFileHandler.loadMatrixDataFromFile(FilePath.MATRIX_PATH.getPath());

        if (!matrixData.isEmpty()) {
            populateMatrixUI(matrixData);
        } else {
            // Handle the case where no data was loaded (e.g., create a default matrix)
        }
    }

    private void populateMatrixUI(List<List<String>> matrixData) {
        int numRows = matrixData.size();
        int numCols = matrixData.getFirst().size();

        // Initialize your Matrix model with the loaded data
        Matrix matrix = MatrixSingleton.getInstance();

        // Ensure the shared matrix has the correct dimensions
        MatrixSingleton.resizeInstance(numRows, numCols);

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                double cellValue = Double.parseDouble(matrixData.get(row).get(col));
                matrix.setValue(row, col, cellValue);
            }
        }
        System.out.println("This is the matrix that is initially created: \n" + matrix);

        // Create MatrixCells with the initialized Matrix model
        matrixCells = new MatrixCell[numRows][numCols];
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                String cellValue = matrixData.get(row).get(col);
                matrixCells[row][col] = new MatrixCell(row, col, cellValue, true);
            }
        }
        System.out.println("These are the matrix cells: \n" + matrixCellsToString(matrixCells));
    }

    @Override
    public void saveToFile() {
//        Matrix matrix = MatrixSingleton.getInstance();
//        List<List<String>> matrixData = MatrixFileHandler.loadMatrixDataFromMatrixCells(matrixCells);
//        MatrixFileHandler.saveMatrixDataToFile(FilePath.MATRIX_PATH.getPath(),
//                BigDecimal.valueOf(0), matrixData, MatrixType.REGULAR);
//
//        // Optionally, update the Matrix object to reflect the changes
//        updateMatrixFromMatrixCells(matrix, matrixCells);
    }

    private void updateMatrixFromMatrixCells(MatrixCell[][] matrixCells) {
        Matrix matrix = MatrixSingleton.getInstance();
        for (int row = 0; row < matrixCells.length; row++) {
            for (int col = 0; col < matrixCells[row].length; col++) {
                String cellValue = matrixCells[row][col].getTextField().getText();
                try {
                    double numericValue = Double.parseDouble(cellValue);
                    matrix.setValue(row, col, numericValue);
                } catch (NumberFormatException e) {
                    throw new RuntimeException(e);
                }
            }
        }
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

    @Override
    public void setupAutoSave() {
//        long autoSaveInterval = 100; // The auto-save interval in milliseconds
//
//        // Schedule the auto-save task to run periodically
//        autoSaveExecutor.scheduleAtFixedRate(() -> // Ensure that file operations that affect the UI are run on the JavaFX Application Thread
//                Platform.runLater(this::saveToFile), autoSaveInterval, autoSaveInterval, TimeUnit.MILLISECONDS);
    }

    public void stopAutoSave() {
        autoSaveExecutor.shutdown();
        try {
            if (!autoSaveExecutor.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                System.out.println("autoSaver has been shut down.");
                autoSaveExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            autoSaveExecutor.shutdownNow();
        }
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
            matrixView.setSizeColsField(sizeColsField);
            matrixView.setSizeRowsField(sizeRowsField);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // for "fast facts" pop-up

    @FXML
    public void handleFastFactsButton() {

    }

    public String matrixCellsToString(MatrixCell[][] matrixCells) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < matrixCells.length; i++) {
            for (int j = 0; j < matrixCells[i].length; j++) {
                String cellValue = matrixCells[i][j].getTextField().getText();
                sb.append(String.format("(%d,%d): %s ", i, j, cellValue));
            }
            sb.append("\n"); // New line at the end of each row
        }
        return sb.toString();
    }
}