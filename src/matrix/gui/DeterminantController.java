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
    private MatrixView matrixView;
    private MatrixCell[][] matrixCells;
    private BigDecimal determinant;
    private String nonSquareMatrixString = "Matrix is not square; it does not have a defined determinant.";
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
        matrixView = new MatrixView(matrixGrid, matrixCells);
        setupDirectionText();
        setupScenesDropdown();
        Matrix matrix = MatrixSingleton.getInstance();
        matrixView.updateViews(true, matrix);
    }

    private void setupDirectionText() {
        directions.setText(initialDirections);
        directions.setWrapText(true);
        directions.setEditable(false);
    }

    @Override
    public void setupScenesDropdown() {
        scenes.getItems().setAll(Scenes.values());
        scenes.setValue(Scenes.DETERMINANT);
        scenes.setTooltip(new Tooltip("Pick the scene"));
        scenes.setOnAction(event -> {
            Scenes selectedScene = scenes.getValue();
            try {
                selectedScene.switchScene(event);
            } catch (IOException e) {
                e.getMessage();
            }
        });
    }

    @FXML
    public void handleDeterminantFunctionality() {
        try {
            Matrix matrix = MatrixSingleton.getInstance();
            MatrixDeterminantOperations determinantOperations = new MatrixDeterminantOperations(matrix);
            this.determinant = determinantOperations.calculateDeterminant();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            temporarilyUpdateDirections(nonSquareMatrixString);
            showErrorPopup("Matrix is not square. I cannot transform it into a triangular matrix, nor can I find the determinant.");
            return;
        }

        if (determinant.compareTo(BigDecimal.ZERO) == 0) {
            determinantValue.setText("0.0");
            save();
            return;
        }
        updateToTriangular();
        if (checkBox.isSelected()) {
            showDeterminantAnimation();
        }
    }


    @Override
    @FXML
    public void handleSaveButton() {
        if (determinant == null) {
            showErrorPopup("Cannot save a matrix with an undefined determinant using this option.");
            return;
        }
        if (determinant.compareTo(BigDecimal.ZERO) == 0) {
            determinantValue.setText("0.0");
            determinant = BigDecimal.ZERO;
        }
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
        saveController.setDeterminantValue(determinant);

        Scene saveScene = new Scene(root);
        MatrixApp.setupGlobalEscapeHandler(saveScene);
        MatrixApp.applyTheme(saveScene);
        saveStage.setScene(saveScene);
        saveStage.showAndWait();
    }

    private void showDeterminantAnimation() {
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

        Scene animationScene = new Scene(root);
        MatrixApp.setupGlobalEscapeHandler(animationScene);
        MatrixApp.applyTheme(animationScene);
        animationStage.setScene(animationScene);
        animationStage.show();
    }


    private void showErrorPopup(String prompt) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(prompt);

        alert.showAndWait();
    }

    @Override
    public void update() {
        List<List<String>> matrixData = MatrixFileHandler.loadMatrixDataFromFile(FilePath.MATRIX_PATH.getPath());

        if (!matrixData.isEmpty()) {
            populateMatrixUI(matrixData);
        } else {
            MatrixFileHandler.populateMatrixIfEmpty();
        }
    }


    private void populateMatrixUI(List<List<String>> matrixData) {
        int numRows = matrixData.size();
        int numCols = matrixData.getFirst().size();

        // Initialize your Matrix model with the loaded data
        Matrix matrix = MatrixSingleton.getInstance();

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                try {
                    double cellValue = Double.parseDouble(matrixData.get(row).get(col));
                    matrix.setValue(row, col, cellValue);
                } catch (NumberFormatException e) {
                    MatrixFileHandler.populateMatrixIfEmpty();
                }
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

        System.out.println("Matrix is updating?");
        if (MatrixSingleton.getInstance() != null) {
            System.out.println("Matrix is not null (determinantController) \n" + MatrixSingleton.getInstance());

            // Set the scale of the determinant to 5 decimal places, rounding as necessary
            BigDecimal scaledDeterminant = determinant.setScale(5, RoundingMode.HALF_UP);
            System.out.println("This is the scaled determinant: \n" + scaledDeterminant);
            determinant = scaledDeterminant;
            if (determinant.compareTo(BigDecimal.ZERO) == 0) {
                determinantValue.setText("0.0");
            }
            determinantValue.setText(String.valueOf(determinant));
            System.out.println("Matrix is being saved as a triangular matrix, hopefully.");
            save();
            matrixView.updateViews(true, MatrixSingleton.getInstance());
        } else {
            System.out.println("Matrix is null (determinantController)");
            System.out.println("Handling missing matrix...");
            MatrixFileHandler.populateMatrixIfEmpty();
            temporarilyUpdateDirections("Matrix not found. Generate a matrix first.");
        }
    }

    private void temporarilyUpdateDirections(String newDirections) {
        directions.setText(newDirections);

        if (Objects.equals(newDirections, nonSquareMatrixString)) {
            directions.setText(nonSquareMatrixString);
            return;
        }
        // Schedule resetting the directions text area after 6 seconds
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> {
            Platform.runLater(() -> directions.setText(initialDirections));
            executorService.shutdown(); // Important to shut down the executor to prevent resource leaks
        }, 7, TimeUnit.SECONDS);
    }

    private void save() {
        List<List<String>> matrixData = MatrixFileHandler.loadMatrixDataFromMatrix(MatrixSingleton.getInstance());

        MatrixFileHandler.saveMatrixDataToFile(
                FilePath.DETERMINANT_PATH.getPath(), determinant,
                matrixData, MatrixType.DETERMINANT);
        MatrixFileHandler.saveMatrixDataToFile(FilePath.TRIANGULAR_PATH.getPath(),
                BigDecimal.ZERO, matrixData, MatrixType.TRIANGULAR);
    }
}