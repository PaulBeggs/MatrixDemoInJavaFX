package matrix.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import matrix.fileManaging.FilePath;
import matrix.fileManaging.MatrixType;
import matrix.model.Matrix;
import matrix.fileManaging.MatrixFileHandler;
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
    Button saveButton;
    @FXML
    TextField determinantValue;
    @FXML
    GridPane matrixGrid = new GridPane();
    @FXML
    ChoiceBox<Scenes> scenes;
    @FXML
    TextArea directions;
    @FXML
    CheckBox checkBox;
    private SaveController saveController;
    private MatrixDeterminantOperations determinantOperations;
    private MatrixView matrixView;
    private List<List<TextField>> matrixTextFields;
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
        setupAutoSave();
        setBooleans();
        initializeMatrixView();
        initializeMatrixAndOperations();
        setupDirectionText();
        setupUIListeners();
        matrixView.updateViews(FilePath.MATRIX_PATH.getPath(), MatrixType.REGULAR, true);
    }

    private void initializeMatrixAndOperations() {
        matrix = MatrixFileHandler.getMatrix(FilePath.MATRIX_PATH.getPath());
        determinantOperations = new MatrixDeterminantOperations(matrix, matrixView);
    }
    private void setBooleans() {
        isEditable = true;
        tickedBox = true;
        checkBox.setSelected(true);
    }

    private void initializeMatrixView() {
        matrixTextFields = new ArrayList<>();
        matrixView = new MatrixView(matrix, matrixGrid, matrixTextFields);
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
            if (newValue) {
                System.out.println("Checkbox is checked");
                tickedBox = true;
            } else {
                System.out.println("Checkbox is unchecked");
                tickedBox = false;
            }
        });
    }
    @FXML
    public void handleDeterminantFunctionality() {
        if (checkBox.isSelected()) {
            tickedBox = true;
            update();
            showDeterminantAnimation();
            isEditable = false;
        }
        determinantValue.setText(String.valueOf(determinant));
        processMatrixForDeterminant();
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
        matrix = MatrixFileHandler.getMatrix(FilePath.TRIANGULAR_PATH.getPath());
        matrixView.updateViews(FilePath.TRIANGULAR_PATH.getPath(), MatrixType.TRIANGULAR, false);
    }

    private void handleMissingMatrix() {
        temporarilyUpdateDirections("Matrix not found. Generate a matrix first.");
    }

    @Override
    @FXML
    public void handleSaveButton() {
        Stage saveStage = new Stage();
        saveStage.setTitle("Save Matrix and Its Determinant");
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

//        this.saveController = loader.getController();
//        saveController.setMatrixTextFields(matrixTextFields);
//        saveController.setDeterminantValue(determinant);
//        saveController.setStage(saveStage);
//        saveController.setSavableController(this, SaveOperationType.MATRIX_AND_DETERMINANT);


        Scene saveScene = new Scene(root);
        saveStage.setScene(saveScene);
        saveStage.showAndWait();
    }

    private void showDeterminantAnimation() {
        if (!determinantIsZero) {

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
            determinantPopUpController.setMatrixTextFields(matrixTextFields);
            determinantPopUpController.setMatrixView(matrixView);
            determinantPopUpController.setStage(animationStage);

            Scene animationScene = new Scene(root);
            animationStage.setScene(animationScene);
            animationStage.show();

        } else {
            showErrorPopup();
        }
    }

    private void showErrorPopup() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("The matrix is not invertible. Therefore, the determinant is either zero or undefined.");

        alert.showAndWait();
    }

    @Override
    public void update() {
        if (matrix != null) {
            this.determinant = determinantOperations.calculateDeterminant();
            BigDecimal threshold = new BigDecimal("1.0E-5");

            // Set the scale of the determinant to 5 decimal places, rounding as necessary
            BigDecimal scaledDeterminant = determinant.setScale(5, RoundingMode.HALF_UP);

            // Perform the comparison
            if (scaledDeterminant.compareTo(threshold) < 0) {
                this.determinant = BigDecimal.ZERO;
            } else {
                this.determinant = scaledDeterminant;
            }

            List<List<String>> matrixData = matrixView.parseMatrixData(matrix);
            MatrixFileHandler.saveMatrixDataToFile(
                    FilePath.DETERMINANT_PATH.getPath(), determinant,
                            matrixData, MatrixType.DETERMINANT);
            processMatrixForDeterminant();
        } else {
            handleMissingMatrix();
        }
    }

    @Override
    public void setInitMatrixData(String filePath) {
        try {
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

                int numCols = 0;

                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split("\\s+");
                    numCols = Math.max(numCols, values.length);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
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

    public void uploadFromFile() {
        matrix = MatrixFileHandler.getMatrix(FilePath.MATRIX_PATH.getPath());
        if (matrix == null) {
            System.out.println("(MatrixController) regMatrix is null from the start.");
            System.out.println("Populating with 0.0...");

            MatrixFileHandler.populateFileIfEmpty(FilePath.MATRIX_PATH.getPath());
            matrix = MatrixFileHandler.getMatrix(FilePath.MATRIX_PATH.getPath());

            if (matrix == null) {
                System.out.println("(MatrixController) Error: Unable to load initial matrix.");
            }
        } else if (isEditable) {
            matrixView.setMatrixTextFields(matrixTextFields);
            matrixView.updateMatrixFromUI();
            matrix = MatrixFileHandler.getMatrix(FilePath.MATRIX_PATH.getPath());
        }
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
        long autoSaveInterval = 100; // The auto-save interval in milliseconds

        // Schedule the auto-save task to run periodically
        autoSaveExecutor.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                // Ensure that file operations that affect the UI are run on the JavaFX Application Thread
                uploadFromFile();
                saveToFile();
            });
        }, autoSaveInterval, autoSaveInterval, TimeUnit.MILLISECONDS);
    }

    public void stopAutoSave() {
        autoSaveExecutor.shutdown();
        try {
            if (!autoSaveExecutor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                autoSaveExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            autoSaveExecutor.shutdownNow();
        }
    }
}


