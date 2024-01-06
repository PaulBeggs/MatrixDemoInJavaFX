package matrix.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import matrix.fileManaging.FilePath;
import matrix.fileManaging.MatrixType;
import matrix.model.Matrix;
import matrix.fileManaging.MatrixFileHandler;
import matrix.model.MatrixCell;
import matrix.model.MatrixSingleton;
import matrix.model.MatrixView;
import matrix.operators.MatrixDeterminantOperations;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DeterminantController implements DataManipulation {
    @FXML
    BorderPane borderPane;
    @FXML
    Button saveButton;
    @FXML
    TextField determinantValue;
    @FXML
    GridPane matrixGrid;
    @FXML
    ChoiceBox<Scenes> scenes;
    @FXML
    TextArea directions;
    @FXML
    CheckBox checkBox;
    private MatrixDeterminantOperations determinantOperations;
    private MatrixView matrixView;
    private MatrixCell[][] matrixCells;
    private Matrix matrix;
    private BigDecimal determinant;
    private boolean tickedBox;
    private boolean determinantIsZero = false;
    private boolean isEditable;
    private final ScheduledExecutorService autoSaveExecutor = Executors.newSingleThreadScheduledExecutor();
    private final String initialDirections =
            """
                    Additive:\s
                    det(B) = det(A)\s
                    \s
                    Interchangeability:\s
                    det(B) = -det(A);\s
                    \s
                    Scalar Multiplication:\s
                    det(B) = k * det(A)""";

    @FXML
    private void initialize() {
        update();
        setupAutoSave();
        setBooleans();
        initializeMatrixView();
        setupDirectionText();
        setupUIListeners();
        Matrix matrix = MatrixSingleton.getInstance();
//        matrixView.updateViews(matrix, true);
    }

    private void setBooleans() {
        isEditable = true;
        tickedBox = true;
        checkBox.setSelected(true);
    }

    private void initializeMatrixView() {
        matrixCells = new MatrixCell[matrix.getRows()][matrix.getCols()];
        matrixView = new MatrixView(matrixGrid, matrixCells, borderPane);
    }

    private void setupDirectionText() {
        directions.setText(initialDirections);
        directions.setWrapText(true);
        directions.setEditable(false);
    }

    private void setupUIListeners() {
        scenes.getItems().setAll(Scenes.values());
        scenes.setValue(Scenes.DETERMINANT);

        scenes.setOnAction(event -> {
            Scenes selectedScene = scenes.getValue();
            try {
                stopAutoSave();
                selectedScene.switchScene(event);
            } catch (IOException e) {
                e.getMessage();
            }
        });

        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            tickedBox = newValue;
        });
    }

    @FXML
    public void handleDeterminantFunctionality() {
        stopAutoSave();
        updateToTriangular();
        if (checkBox.isSelected()) {
            tickedBox = true;

            showDeterminantAnimation();
            isEditable = false;
        }
        determinantValue.setText(String.valueOf(determinant));
        System.out.println("This is the determinant value inside of 'handleDeterminantFunctionality' \n" + determinant);
    }

    private void processMatrixForDeterminant() {
        if (Objects.equals(determinant, BigDecimal.valueOf(.12319620031999)) ||
                Objects.equals(determinant, BigDecimal.ZERO)) {
            handleZeroDeterminant();
        } else {
            handleNonZeroDeterminant();
        }
    }

    private void handleZeroDeterminant() {
        if (!tickedBox) {
            temporarilyUpdateDirections("Determinant is either 0, or not defined as it may be a non-square matrix.");
        }
        determinantValue.setText("0.0");
        determinantIsZero = true;
    }

    private void handleNonZeroDeterminant() {
        stopAutoSave();
        matrix = MatrixFileHandler.loadMatrixFromFile(FilePath.TRIANGULAR_PATH.getPath());
        System.out.println("This is the matrix that is being acquired from Triangular_Path: \n" + matrix);
//        matrixView.updateViews(matrix, false);
    }

    private void handleMissingMatrix() {
        temporarilyUpdateDirections("Matrix not found. Generate a matrix first.");
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
        saveController.setDeterminantValue(determinant);
        saveController.setStage(saveStage);

        Scene saveScene = new Scene(root);
        MatrixApp.setupGlobalEscapeHandler(saveScene);
        MatrixApp.applyTheme(saveScene);
        saveStage.setScene(saveScene);
        saveStage.showAndWait();
    }

    private void showDeterminantAnimation() {
        if (!determinantIsZero) {
            System.out.println("This is the matrix that is being sent to be calculated for the determinant in show determinant operation: \n" + matrix);
            Stage animationStage = new Stage();
            animationStage.setTitle("Determinant Animation");
            animationStage.initModality(Modality.WINDOW_MODAL);
            animationStage.initOwner(MatrixApp.getPrimaryStage());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/determinantAnimation.fxml"));
            Parent root;
            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            DeterminantPopUpController determinantPopUpController = loader.getController();
            determinantPopUpController.setMatrixCells(matrixCells);
            determinantPopUpController.setMatrixGrid(matrixGrid);
            determinantPopUpController.setStage(animationStage);

            Scene animationScene = new Scene(root);
            MatrixApp.setupGlobalEscapeHandler(animationScene);
            MatrixApp.applyTheme(animationScene);
            animationStage.setScene(animationScene);
            animationStage.show();

        } else {
            showErrorPopup();
        }
    }

    private void makeTriangular(Matrix matrix) {
        List<List<String>> matrixData = MatrixFileHandler.loadMatrixDataFromMatrix(matrix);
        System.out.println("This is the (hopefully triangularized) matrixData inside DeterminantController: \n" + matrixData);
        MatrixFileHandler.saveMatrixDataToFile(FilePath.TRIANGULAR_PATH.getPath(), BigDecimal.valueOf(0), matrixData, MatrixType.TRIANGULAR);
//        matrixView.updateViews(matrix, false);
    }

    private void showErrorPopup() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("The matrix is not invertible. Therefore, the determinant is either zero or undefined.");

        alert.showAndWait();
    }

    //    @Override
//    public void update() {
//        List<List<String>> matrixData = MatrixFileHandler.loadMatrixFromFile(FilePath.MATRIX_PATH.getPath());
//
//        if (matrixData != null && !matrixData.isEmpty()) {
//            populateMatrixUI(matrixData);
//        } else {
//            // Handle the case where no data was loaded (e.g., create a default matrix)
//        }
//    }
//
//    private void populateMatrixUI(List<List<String>> matrixData) {
//        int numRows = matrixData.size();
//        int numCols = matrixData.get(0).size(); // Assuming all rows have the same number of columns
//
//        // Initialize your Matrix model with the loaded data
//        matrix = new Matrix(numRows, numCols);
//        for (int row = 0; row < numRows; row++) {
//            for (int col = 0; col < numCols; col++) {
//                double cellValue = Double.parseDouble(matrixData.get(row).get(col));
//                matrix.setValue(row, col, cellValue);
//            }
//        }
//
//        // Create MatrixCells with the initialized Matrix model
//        matrixCells = new MatrixCell[numRows][numCols];
//        for (int row = 0; row < numRows; row++) {
//            for (int col = 0; col < numCols; col++) {
//                String cellValue = matrixData.get(row).get(col);
//                matrixCells[row][col] = new MatrixCell(row, col, cellValue, true, matrix);
//                // Add the cell's TextField to your grid
//            }
//        }
//    }
    @Override
    public void update() {
        List<List<String>> matrixData = MatrixFileHandler.loadMatrixDataFromFile(FilePath.MATRIX_PATH.getPath());

        if (matrixData != null && !matrixData.isEmpty()) {
            populateMatrixUI(matrixData);
        } else {
            // Handle the case where no data was loaded (e.g., create a default matrix)
        }
    }


    private void populateMatrixUI(List<List<String>> matrixData) {
        int numRows = matrixData.size();
        int numCols = matrixData.get(0).size(); // Assuming all rows have the same number of columns

        // Initialize your Matrix model with the loaded data
        matrix = new Matrix(numRows, numCols);
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                double cellValue = Double.parseDouble(matrixData.get(row).get(col));
                matrix.setValue(row, col, cellValue);
            }
        }

        // Create MatrixCells with the initialized Matrix model
        matrixCells = new MatrixCell[numRows][numCols];
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                String cellValue = matrixData.get(row).get(col);
                matrixCells[row][col] = new MatrixCell(row, col, cellValue, true);
            }
        }
    }

    private void updateToTriangular() {
        if (matrix != null) {
            System.out.println("Matrix is not null (determinantController) \n" + matrix);
            determinantOperations = new MatrixDeterminantOperations(matrix);
            this.determinant = determinantOperations.calculateDeterminant();

            // Set the scale of the determinant to 5 decimal places, rounding as necessary
            BigDecimal scaledDeterminant = determinant.setScale(5, RoundingMode.HALF_UP);
            System.out.println("This is the scaled determinant: \n" + scaledDeterminant);

            this.determinant = scaledDeterminant;

            List<List<String>> matrixData = MatrixFileHandler.loadMatrixDataFromMatrix(matrix);
            MatrixFileHandler.saveMatrixDataToFile(
                    FilePath.DETERMINANT_PATH.getPath(), determinant,
                    matrixData, MatrixType.DETERMINANT);
            MatrixFileHandler.saveMatrixDataToFile(FilePath.TRIANGULAR_PATH.getPath(),
                    BigDecimal.ZERO, matrixData, MatrixType.TRIANGULAR);
            makeTriangular(matrix);
            processMatrixForDeterminant();
        } else {
            System.out.println("Matrix is null (determinantController) ");
            System.out.println("Handling missing matrix...");
            handleMissingMatrix();
        }
    }

    @Override
    public void saveToFile() {
        List<List<String>> matrixData = MatrixFileHandler.loadMatrixDataFromMatrix(matrix);
        MatrixFileHandler.saveMatrixDataToFile(FilePath.MATRIX_PATH.getPath(),
                BigDecimal.valueOf(0), matrixData, MatrixType.REGULAR);
    }

    private void temporarilyUpdateDirections(String newDirections) {
        directions.setText(newDirections);

        // Schedule resetting the directions text area after 6 seconds
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> {
            Platform.runLater(() -> directions.setText(initialDirections));
            executorService.shutdown(); // Important to shut down the executor to prevent resource leaks
        }, 7, TimeUnit.SECONDS);
    }

    @Override
    public void setupAutoSave() {
        long autoSaveInterval = 500; // The auto-save interval in milliseconds

        // Schedule the auto-save task to run periodically
        autoSaveExecutor.scheduleAtFixedRate(() -> {
            // Ensure that file operations that affect the UI are run on the JavaFX Application Thread
            Platform.runLater(this::saveToFile);
        }, autoSaveInterval, autoSaveInterval, TimeUnit.MILLISECONDS);
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
}


