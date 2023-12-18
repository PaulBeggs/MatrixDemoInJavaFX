package matrix.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import java.util.*;

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
    private int numRows, numCols;
    private MatrixView matrixView;
    private List<List<TextField>> matrixTextFields;
    private Matrix matrix;
    private BigDecimal determinant;
    private boolean tickedBox;
    private boolean determinantIsZero = false;
    private long AUTO_SAVE_INTERVAL;
    private boolean isEditable;
    private boolean isStart;
    private String initialDirections =
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
        initializeMatrixAndOperations();
        initializeMatrixView();
        setupDirectionText();
        setupUIListeners();
        matrixView.updateViews(FilePath.MATRIX_PATH.getPath(), MatrixType.REGULAR, true);
        update();
    }

    private void initializeMatrixAndOperations() {
        matrix = MatrixFileHandler.getMatrix(FilePath.MATRIX_PATH.getPath());
        determinantOperations = new MatrixDeterminantOperations(matrix);
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
            showDeterminantAnimation();
            isEditable = false;
        }
        determinantValue.setText(String.valueOf(determinant));
        processMatrixForDeterminant();
    }

    private void processMatrixForDeterminant() {
            if (Objects.equals(determinant, BigDecimal.valueOf(.12319620031999))) {
                handleZeroDeterminant();
            } else {
                handleNonZeroDeterminant();
            }
    }

    private void handleZeroDeterminant() {
        determinantValue.setText(null);
        determinantIsZero = true;
    }

    private void handleNonZeroDeterminant() {
//        if (tickedBox) {
//            matrix = MatrixFileHandler.getMatrix(FilePath.TRIANGULAR_PATH.getPath());
//            stop();
//        }
    }

    private void handleMissingMatrix() {
        System.out.println("Matrix not found. Generate a matrix first.");
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
            AUTO_SAVE_INTERVAL = 1000000000;
//
//            Stage animationStage = new Stage();
//            animationStage.setTitle("Determinant Animation");
//            animationStage.initModality(Modality.WINDOW_MODAL);
//            animationStage.initOwner(MatrixApp.getPrimaryStage());
//
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/determinantAnimation.fxml"));
//            Parent root;
//            try {
//                root = loader.load();
//            } catch (IOException e) {
//                e.printStackTrace();
//                return;
//            }
//
//            DeterminantPopUpController determinantPopUpController = loader.getController();
//            determinantPopUpController.setMatrixTextFields(matrixTextFields);
//            determinantPopUpController.setMatrixView(matrixView);
//            determinantPopUpController.setStage(animationStage);
//
//            Scene animationScene = new Scene(root);
//            animationStage.setScene(animationScene);
//            animationStage.show();

        } else {
            showErrorPopup("Determinant is either zero or undefined. Therefore, the matrix is not invertible.");
        }
    }

    private void showErrorPopup(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(errorMessage);

        alert.showAndWait();
    }

    @Override
    public void update() {
        if (matrix != null) {
            this.determinant = determinantOperations.calculateDeterminant();
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
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            numRows = 0;
            numCols = 0;

            while (br.readLine() != null) {
                numRows++;
            }

            br.close();
            br = new BufferedReader(new FileReader(filePath));

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\\s+");
                numCols = Math.max(numCols, values.length);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveToFile() {
        matrix = MatrixFileHandler.getMatrix(FilePath.MATRIX_PATH.getPath());
        matrixView.updateMatrixFromUI();

        if (matrix != null && isEditable) {
            List<List<String>> matrixData = matrixView.parseMatrixData(matrix);
            if (matrixData != null) {
                MatrixFileHandler.saveMatrixDataToFile(FilePath.DETERMINANT_PATH.getPath(),
                        determinant, matrixData, MatrixType.DETERMINANT);
            } else {
                System.out.println("Error: Matrix data is null.");
            }
        } else {
            System.out.println("Error: Matrix is null.");
        }
    }

    @Override
    public void setupAutoSave() {
        Timer autoSaveTimer = new Timer();
        AUTO_SAVE_INTERVAL = 500;
        autoSaveTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                saveToFile();
            }
        }, AUTO_SAVE_INTERVAL, AUTO_SAVE_INTERVAL);
    }
}


