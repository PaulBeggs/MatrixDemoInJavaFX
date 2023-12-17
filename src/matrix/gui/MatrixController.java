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

import matrix.model.TriangularizationView;
import matrix.operators.ElementaryMatrixOperations;
import matrix.operators.MatrixDeterminantOperations;
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

public class MatrixController implements DataManipulation{
    @FXML
    BorderPane borderPane;
    @FXML
    Button generateButton, saveButton, operationButton, clearButton;
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
    private TriangularizationView tView;
    private MatrixInputHandler MIP = new MatrixInputHandler();
    private ElementaryMatrixOperations EMO;
    private MatrixDeterminantOperations MDO;
    private Matrix matrix;
    private Matrix tMatrix;
    private List<List<TextField>> matrixTextFields;

    @FXML
    private void initialize() {
        initializeMatrixView();
        setupAutoSaveAndLoad();
        initializeTriangularizationView();
        saveTMatrixToFile();
        setupChoiceBoxes();
        setupTextArea();
        setupListeners();
        update();
    }

    private void initializeMatrixView() {
        matrixTextFields = new ArrayList<>();
        matrixView = new MatrixView(matrix, matrixGrid, matrixTextFields);
        matrixView.updateViews(FilePath.MATRIX_PATH.getPath(), MatrixType.REGULAR, true);
    }

    @Override
    public void setupAutoSaveAndLoad() {
        Timer autoSaveTimer = new Timer();
        long AUTO_SAVE_INTERVAL = 100;
        autoSaveTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                saveToFile();
            }
        }, AUTO_SAVE_INTERVAL, AUTO_SAVE_INTERVAL);
    }

    public void initializeTriangularizationView() {
        MDO = new MatrixDeterminantOperations(matrix);
        tView = new TriangularizationView(tMatrix, matrixGrid, matrixTextFields);
    }

    private void setupChoiceBoxes() {
        operations.getItems().addAll("Swap Rows", "Multiply Rows", "Add Rows");
        operations.setValue("Swap Rows");
        scenes.getItems().setAll(Scenes.values());
        scenes.setValue(Scenes.MATRIX);
    }

    private void setupTextArea() {
        directions.setWrapText(true);
        directions.setEditable(false);
    }

    private void setupListeners() {
        MIP.integerOnlyListener(sourceRow, "sourceRow");
        MIP.integerOnlyListener(targetRow, "targetRow");
        MIP.integerOnlyListener(sizeRowsField, "sizeRows");
        MIP.integerOnlyListener(sizeColsField, "sizeCols");

        scenes.setOnAction(event -> {
            Scenes selectedScene = scenes.getValue();
            try {
                saveToFile();
                selectedScene.switchScene(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    public void handleGenerateButton() {
        if (MIP.isPositiveIntValid(sizeColsField) && MIP.isPositiveIntValid(sizeRowsField)) {
            updateMatrixGrid(false);
        } else {
            sizeColsField.clear();
            sizeRowsField.clear();
            System.out.println("Invalid Row Indices.");
        }
    }

    @FXML
    public void handleClearButton() {
        matrix.setToIdentity();
        MatrixFileHandler.setMatrix(FilePath.MATRIX_PATH.getPath(), matrix);
        System.out.println("After Setting (in handleClearButton': \n" + MatrixFileHandler.getMatrix(FilePath.MATRIX_PATH.getPath()));
        updateMatrixGrid(true);
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
            matrixView.updateViews(filePath, MatrixType.REGULAR, true);
            saveToFile();
        }
    }


    @FXML
    private void performOperation() {
        matrix = MatrixFileHandler.getMatrix(FilePath.MATRIX_PATH.getPath());
        EMO = new ElementaryMatrixOperations(matrix);
        String selectedOption = operations.getValue();

        if (MIP.isRowValid(targetRow, matrix.getRows()) && MIP.isRowValid(sourceRow, matrix.getRows())) {
            int targetRowIndex = Integer.parseInt(targetRow.getText()) - 1;
            int sourceRowIndex = Integer.parseInt(sourceRow.getText()) - 1;


            switch (selectedOption) {
                case "Swap Rows" -> {
                    EMO.swapRows(targetRowIndex, sourceRowIndex);
                    updateSwappedMatrixUI(targetRowIndex, sourceRowIndex);

                }
                case "Multiply Rows", "Add Rows" -> {
                    if (MIP.isDoubleValid(multiplier)) {
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

    @Override
    public void saveToFile() {
        matrix = MatrixFileHandler.getMatrix(FilePath.MATRIX_PATH.getPath());

        // If the matrix is null, populate the file and try to get the matrix again
        if (matrix == null) {
            MatrixFileHandler.populateFileIfEmpty(FilePath.MATRIX_PATH.getPath());
            matrix = MatrixFileHandler.getMatrix(FilePath.MATRIX_PATH.getPath());


            // If still null after trying to populate, log an error and return
            if (matrix == null) {
                System.out.println("(MatrixController) Error: Unable to load or create the regMatrix.");
                return;
            }
        }
        matrixView.setMatrixTextFields(matrixTextFields);
        matrixView.updateMatrixFromUI();

        List<List<String>> matrixData = matrixView.parseMatrixData(matrix);
        if (matrixData != null) {
            MatrixFileHandler.saveMatrixDataToFile(FilePath.MATRIX_PATH.getPath(),
                    BigDecimal.valueOf(0), matrixData, MatrixType.REGULAR);
            MatrixFileHandler.setMatrix(FilePath.MATRIX_PATH.getPath(), matrix);
        } else {
            System.out.println("(MatrixController) Error: regMatrix data is null.");
        }
    }

    public void saveTMatrixToFile() {
        tMatrix = MatrixFileHandler.getMatrix(FilePath.TRIANGULAR_PATH.getPath());

        if (tMatrix == null) {
            MatrixFileHandler.populateFileIfEmpty(FilePath.TRIANGULAR_PATH.getPath());
            tMatrix = MatrixFileHandler.getMatrix(FilePath.TRIANGULAR_PATH.getPath());

            if (matrix == null) {
                System.out.println("(MatrixController) Error: Unable to load or create the triMatrix.");
                return;
            }

            tView.setMatrixTextFields(matrixTextFields);
            tView.updateMatrixFromUI();

            List<List<String>> matrixData = tView.parseMatrixData(tMatrix);
            if (matrixData != null) {
                MatrixFileHandler.saveMatrixDataToFile(FilePath.TRIANGULAR_PATH.getPath(),
                        BigDecimal.valueOf(0), matrixData, MatrixType.TRIANGULAR);
                MatrixFileHandler.setMatrix(FilePath.TRIANGULAR_PATH.getPath(), tMatrix);
            } else {
                System.out.println("(MatrixController) Error: triMatrix data is null.");
            }
        }
    }


    private void updateMatrixGrid(Boolean identityCheck) {
        matrixGrid.getChildren().clear();

        int numCols = Integer.parseInt(matrixView.getSizeColsField().getText());
        int numRows = Integer.parseInt(matrixView.getSizeRowsField().getText());
        System.out.println("Are these accurate? \n" + numRows);
        System.out.println(numCols);
        this.matrix = new Matrix(numRows, numCols);

        this.matrixTextFields = new ArrayList<>();
        matrixView.setMatrixTextFields(matrixTextFields);

        for (int row = 0; row < numRows; row++) {
            List<TextField> rowList = new ArrayList<>();
            for (int col = 0; col < numCols; col++) {
                TextField cell = new TextField();
                MIP.addNumericValidation(cell);
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
                    cellValue = Math.floor(Math.random() * 101) - 50; // Random value for regular matrix
                }

                cell.setText(String.valueOf(cellValue));
                matrix.setValue(row, col, cellValue);

                matrixGrid.add(cell, col, row);
                rowList.add(cell);
            }
            matrixTextFields.add(rowList);
        }
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
}