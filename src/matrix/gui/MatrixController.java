package matrix.gui;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
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
import java.util.Timer;
import java.util.TimerTask;

public class MatrixController implements DataManipulation {
    @FXML
    BorderPane borderPane;
    @FXML
    Button generateButton, saveButton, operationButton, clearButton, fastFactsButton;
    @FXML
    TextField sizeColsField, sizeRowsField, targetRow, sourceRow, multiplier;
    @FXML
    TextArea directions = new TextArea();
    @FXML
    ChoiceBox<Scenes> scenes;
    @FXML
    ChoiceBox<String> operations;
    @FXML
    GridPane matrixGrid = new GridPane();
    private MatrixView matrixView;
    private MatrixInputHandler MIH = new MatrixInputHandler();
    private ElementaryMatrixOperations EMO;
    private Matrix matrix;
    private List<List<TextField>> matrixTextFields;

    @FXML
    private void initialize() {
        setupAutoSave();
        initializeMatrixView();
        setupTextFieldListeners();
        setupOperationsDropdown();
        setupScenesDropdown();
        setupDirectionText();
        matrixView.updateViews(FilePath.MATRIX_PATH.getPath(), MatrixType.REGULAR, true);
        update();
    }

    private void initializeMatrixView() {
        matrixTextFields = new ArrayList<>();
        matrixView = new MatrixView(matrix, matrixGrid, matrixTextFields);
    }

    private void setupTextFieldListeners() {
        integerOnlyListener(sourceRow, "sourceRow");
        integerOnlyListener(targetRow, "targetRow");
        integerOnlyListener(sizeRowsField, "sizeRows");
        integerOnlyListener(sizeColsField, "sizeCols");
    }

    private void setupOperationsDropdown() {
        operations.getItems().addAll("Swap Rows", "Multiply Rows", "Add Rows");
        operations.setValue("Swap Rows");
    }

    private void setupScenesDropdown() {
        scenes.getItems().setAll(Scenes.values());
        scenes.setValue(Scenes.MATRIX);
        scenes.setOnAction(event -> {
            Scenes selectedScene = scenes.getValue();
            try {
                saveToFile();
                selectedScene.switchScene(event);
            } catch (IOException e) {
                e.printStackTrace(); // Changed to printStackTrace for better error visibility
            }
        });
    }

    private void setupDirectionText() {
        directions.setWrapText(true);
        directions.setEditable(false);
    }

    @FXML
    public void handleGenerateButton() {
        if (MIH.isPositiveIntValid(sizeColsField) && MIH.isPositiveIntValid(sizeRowsField)) {
            int numRows = Integer.parseInt(sizeRowsField.getText());
            int numCols = Integer.parseInt(sizeColsField.getText());

            List<List<String>> matrixData = new ArrayList<>();
            for (int row = 0; row < numRows; row++) {
                List<String> rowData = new ArrayList<>();
                for (int col = 0; col < numCols; col++) {
                    // For a regular matrix, initialize with random/default values
                    // For an identity matrix, you'd check if row == col for 1.0, else 0.0
                    double cellValue = Math.floor(Math.random() * 100); // or another initialization logic
                    rowData.add(String.valueOf(cellValue));
                }
                matrixData.add(rowData);
            }

            updateMatrixGrid(false, matrixData); // false indicates it's not an identity matrix
        } else {
            // Handle invalid input
            System.out.println("Invalid input for matrix dimensions.");
        }
    }


    @FXML
    public void handleClearButton() {
        int numRows = matrix.getRows();
        int numCols = matrix.getCols();

        List<List<String>> identityMatrixData = new ArrayList<>();
        for (int row = 0; row < numRows; row++) {
            List<String> rowData = new ArrayList<>();
            for (int col = 0; col < numCols; col++) {
                String cellValue = (row == col) ? "1.0" : "0.0"; // Identity matrix logic
                rowData.add(cellValue);
            }
            identityMatrixData.add(rowData);
        }

        updateMatrixGrid(true, identityMatrixData); // true indicates it's an identity matrix

        matrix.setToIdentity(); // Resets the matrix to an identity matrix
        MatrixFileHandler.setMatrix(FilePath.MATRIX_PATH.getPath(), matrix);
        System.out.println("After Setting (in handleClearButton): \n" + MatrixFileHandler.getMatrix(FilePath.MATRIX_PATH.getPath()));
    }


    @Override
    @FXML
    public void handleSaveButton() {
        Stage saveStage = new Stage();
        saveStage.setTitle("Save Matrix");
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
        saveController.setMatrixTextFields(matrixTextFields);
        saveController.setStage(saveStage);

        Scene saveScene = new Scene(root);
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
            List<List<String>> matrixData = MatrixFileHandler.loadMatrixFromFile(filePath);

            if (matrixData != null) {
                // Convert the loaded data into a Matrix object
                updateMatrixGrid(false, matrixData); // Use the modified updateMatrixGrid
                saveToFile();
            }
        }
    }

    @FXML
    private void performOperation() {
        matrix = MatrixFileHandler.getMatrix(FilePath.MATRIX_PATH.getPath());
        EMO = new ElementaryMatrixOperations(matrix);
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
                    }
                }
            }
            saveToFile();
        } else {
            System.out.println("At least one row is invalid. Fix it to proceed.");
        }
    }


    // To cut down on duplicated code and add some abstraction to the project:
    private enum MatrixOperation {
        SWAP,
        MULTIPLY,
        ADD
    }

    private void updateMatrixUIFromOperations(int rowIndex, int numCols, MatrixOperation operation, int sourceRowIndex, double rowMultiplier) {
        try {
            for (int col = 0; col < numCols; col++) {
                double targetValue = Double.parseDouble(matrixTextFields.get(rowIndex).get(col).getText());
                double newValue;

                switch (operation) {
                    case SWAP -> {
                        newValue = Double.parseDouble(matrixTextFields.get(sourceRowIndex).get(col).getText());
                        matrixTextFields.get(sourceRowIndex).get(col).setText(String.valueOf(targetValue));
                    }
                    case MULTIPLY -> newValue = targetValue * rowMultiplier;
                    case ADD -> {
                        double sourceValue = Double.parseDouble(matrixTextFields.get(sourceRowIndex).get(col).getText());
                        newValue = targetValue + rowMultiplier * sourceValue;
                    }
                    default -> throw new IllegalArgumentException("Unsupported operation");
                }

                matrixView.getMatrixTextFields();
                matrixTextFields.get(rowIndex).get(col).setText(String.valueOf((newValue)));

            }

//            matrixView.updateViews(FilePath.MATRIX_PATH.getPath(), MatrixType.REGULAR, true);
            saveToFile();
        } catch (NumberFormatException e) {
            System.out.println("Error converting text to double: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private void updateSwappedMatrixUI(int targetRowIndex, int sourceRowIndex) {
        double rowMultiplier = 0;
        updateMatrixUIFromOperations(targetRowIndex, matrix.getCols(), MatrixOperation.SWAP, sourceRowIndex, rowMultiplier);
        MatrixFileHandler.setMatrix(FilePath.MATRIX_PATH.getPath(), matrix);
    }

    private void updateMultipliedMatrixUI(int targetRowIndex, double rowMultiplier) {
        int sourceRowIndex = 0;
        updateMatrixUIFromOperations(targetRowIndex, matrix.getCols(), MatrixOperation.MULTIPLY, sourceRowIndex, rowMultiplier);
        MatrixFileHandler.setMatrix(FilePath.MATRIX_PATH.getPath(), matrix);
    }

    private void updateAddedMatrixUI(int targetRowIndex, int sourceRowIndex, double rowMultiplier) {
        updateMatrixUIFromOperations(targetRowIndex, matrix.getCols(), MatrixOperation.ADD, sourceRowIndex, rowMultiplier);
        MatrixFileHandler.setMatrix(FilePath.MATRIX_PATH.getPath(), matrix);
    }

    @Override
    public void update() {
        setInitMatrixData(FilePath.MATRIX_PATH.getPath());
    }

    @Override
    public void setInitMatrixData(String filePath) {
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


    public void uploadFromFile() {
        matrix = MatrixFileHandler.getMatrix(FilePath.MATRIX_PATH.getPath());
        if (matrix == null) {
            System.out.println("(DeterminantController) regMatrix is null from the start.");
            System.out.println("Populating with 0.0...");

            MatrixFileHandler.populateFileIfEmpty(FilePath.MATRIX_PATH.getPath());
            matrix = MatrixFileHandler.getMatrix(FilePath.MATRIX_PATH.getPath());

            if (matrix == null) {
                System.out.println("(DeterminantController) Error: Unable to load initial matrix.");
            }
        } else {
            matrixView.setMatrixTextFields(matrixTextFields);
            matrixView.updateMatrixFromUI();
            matrix = MatrixFileHandler.getMatrix(FilePath.MATRIX_PATH.getPath());
        }
    }

    @Override
    public void saveToFile() {
        List<List<String>> matrixData = matrixView.parseMatrixData(matrix);
        if (matrixData != null) {

            MatrixFileHandler.saveMatrixDataToFile(FilePath.MATRIX_PATH.getPath(),
                    BigDecimal.valueOf(0), matrixData, MatrixType.REGULAR);
            MatrixFileHandler.setMatrix(FilePath.MATRIX_PATH.getPath(), matrix);

        } else {
            System.out.println("(MatrixController) Error: regMatrix data is null.");
        }
    }

    private void updateMatrixGrid(Boolean identityCheck, List<List<String>> matrixData) {
        matrixGrid.getChildren().clear();
        int numRows = matrixData.size();
        int numCols = matrixData.get(0).size(); // Assumes each row has the same number of columns

        this.matrix = new Matrix(numRows, numCols);
        this.matrixTextFields = new ArrayList<>();
        matrixView.setMatrixTextFields(matrixTextFields);

        for (int row = 0; row < numRows; row++) {
            List<TextField> rowList = new ArrayList<>();
            for (int col = 0; col < numCols; col++) {

                final int currentRow = row; // Final variable for use in lambda
                final int currentCol = col; // Final variable for use in lambda

                TextField cell = new TextField();
                cell.setMinHeight(50);
                cell.setMinWidth(50);
                cell.setAlignment(Pos.CENTER);
                cell.setEditable(true);

                double cellValue;
                if (identityCheck && row == col) {
                    cellValue = 1.0; // Diagonal element for identity matrix
                } else if (identityCheck) {
                    cellValue = 0.0; // Off-diagonal element for identity matrix
                } else {
                    cellValue = Math.floor(Math.random() * 100); // Random value for regular matrix
                }

                cell.setText(String.valueOf(cellValue));
                matrix.setValue(row, col, cellValue);

                cell.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue.matches("\\d*(\\.\\d*)?")) {
                        cell.setText(newValue.replaceAll("[^\\d.]", ""));
                    } else {
                        try {
                            // Parse the new value to a double
                            double newCellValue = Double.parseDouble(newValue);
                            // Update the Matrix object at the specific row and column
                            matrix.setValue(currentRow, currentCol, newCellValue);
                        } catch (NumberFormatException e) {
                            cell.setText(oldValue);
                        }
                    }
                });


                matrixGrid.add(cell, col, row);
                rowList.add(cell);
            }
            matrixTextFields.add(rowList);
        }
        saveToFile();
        MatrixFileHandler.setMatrix(FilePath.MATRIX_PATH.getPath(), matrix);
        int guiRows = Integer.parseInt(matrixView.getSizeRowsField().getText());
        int guiCols = Integer.parseInt(matrixView.getSizeColsField().getText());
        int matrixRows = matrix.getRows();
        int matrixCols = matrix.getCols();

        System.out.println("GUI Dimensions: Rows = " + guiRows + ", Cols = " + guiCols);
        System.out.println("Matrix Dimensions: Rows = " + matrixRows + ", Cols = " + matrixCols);

        if (guiRows != matrixRows || guiCols != matrixCols) {
            System.out.println("Dimension Mismatch Detected!");
        } else {
            System.out.println("After Setting (within 'updateMatrixGrid()': \n" + MatrixFileHandler.getMatrix(FilePath.MATRIX_PATH.getPath()));
        }
    }

    @Override
    public void setupAutoSave() {
        Timer autoSaveTimer = new Timer();
        long AUTO_SAVE_INTERVAL = 100;
        autoSaveTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                uploadFromFile();
                saveToFile();
            }
        }, AUTO_SAVE_INTERVAL, AUTO_SAVE_INTERVAL);
    }

    private void integerOnlyListener(TextField textField, String fieldName) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    // for "fast facts" pop-up

    @FXML
    public void handleFastFactsButton() {

    }

    public TriangularizationResult triangularizeMatrix(Matrix matrix) {
        if (matrix == null || matrix.getRows() != matrix.getCols()) {
            // Non-square or null matrix cannot be triangularized
            return new TriangularizationResult(false, null);
        }

        // Perform the triangularization process
        Matrix triangularMatrix = performTriangularization(matrix);

        // Return the result
        return new TriangularizationResult(true, triangularMatrix);
    }

    private Matrix performTriangularization(Matrix matrix) {
        // Implement the logic similar to makeTriangular()
        // Create and return the triangular matrix
        return matrix;
    }

    class TriangularizationResult {
        private final boolean canBeTriangularized;
        private final Matrix triangularMatrix;

        public TriangularizationResult(boolean canBeTriangularized, Matrix triangularMatrix) {
            this.canBeTriangularized = canBeTriangularized;
            this.triangularMatrix = triangularMatrix;
        }

        // Getters and possibly setters
    }
}