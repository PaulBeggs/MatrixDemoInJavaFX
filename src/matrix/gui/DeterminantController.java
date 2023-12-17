package matrix.gui;

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
import matrix.model.TriangularizationView;
import matrix.operators.BigDecimalUtil;
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
    private MatrixDeterminantOperations MDO;
    private int numRows, numCols;
    private MatrixView matrixView;
    private TriangularizationView tView;
    private List<List<TextField>> matrixTextFields;
    private Matrix matrix;
    private Matrix tMatrix;
    private BigDecimal determinant;
    private boolean tickedBox;
    private boolean determinantIsZero;
    private boolean isEditable;
    private boolean isRegNull;
    private boolean isTriNull;
    private long AUTO_SAVE_INTERVAL;


    @FXML
    private void initialize() {
        initializeMatrixView();
        setupAutoSaveAndLoad();
        initializeTriangularizationView();
        setupBooleans();
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
        long AUTO_SAVE_INTERVAL = 500;
        autoSaveTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!isEditable) {
                    uploadFromFile();
                    saveToFile();
                } else {
                    saveTMatrixToFile();
                }
            }
        }, AUTO_SAVE_INTERVAL, AUTO_SAVE_INTERVAL);
    }

    public void initializeTriangularizationView() {
        MDO = new MatrixDeterminantOperations(matrix);
        tView = new TriangularizationView(tMatrix, matrixGrid, matrixTextFields);
    }

    private void setupBooleans() {
        checkBox.setSelected(true);
        isEditable = true;
        tickedBox = false;
        determinantIsZero = false;
        isRegNull = false;
        isTriNull = false;
    }

    private void setupTextArea() {
        directions.setText(
                """
                        Additive:\s
                        det(B) = det(A)\s
                        \s
                        Interchangeability:\s
                        det(B) = -det(A);\s
                        \s
                        Scalar Multiplication:\s
                        det(B) = k * det(A)""");
        directions.setWrapText(true);
        directions.setEditable(false);
    }

    private void setupChoiceBoxes() {
        scenes.getItems().setAll(Scenes.values());
        scenes.setValue(Scenes.DETERMINANT);
    }

    private void setupListeners() {
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
    public void handleDeterminantFunctionality() {
        tickedBox = true;

        if (checkBox.isSelected()) {
            showDeterminantAnimation();
            isEditable = false;
        }
        BigDecimal roundedDeterminant = BigDecimalUtil.roundBigDecimal(determinant, 5);
        determinantValue.setText(String.valueOf(roundedDeterminant));
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
        if (tickedBox) {
            tMatrix = MatrixFileHandler.getMatrix(FilePath.TRIANGULAR_PATH.getPath());
            MatrixFileHandler.setMatrix(FilePath.TRIANGULAR_PATH.getPath(), tMatrix);
        }
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

        this.saveController = loader.getController();
        saveController.setMatrixTextFields(matrixTextFields);
        BigDecimal roundedDeterminant = BigDecimalUtil.roundBigDecimal(determinant, 5);
        determinantValue.setText(String.valueOf(roundedDeterminant));
        saveController.setStage(saveStage);

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

    public void uploadFromFile() {
        matrix = MatrixFileHandler.getMatrix(FilePath.MATRIX_PATH.getPath());
        if (matrix == null && !isRegNull) {
            isRegNull = true;
            System.out.println("(DeterminantController) regMatrix is null from the start.");
            System.out.println("Populating with 0.0...");

            MatrixFileHandler.populateFileIfEmpty(FilePath.MATRIX_PATH.getPath());
            matrix = MatrixFileHandler.getMatrix(FilePath.MATRIX_PATH.getPath());

            if (matrix == null) {
                System.out.println("(DeterminantController) Error: Unable to load initial matrix.");
            }
        } else {
            isRegNull = false;
            matrixView.setMatrixTextFields(matrixTextFields);
            matrixView.updateMatrixFromUI();
            matrix = MatrixFileHandler.getMatrix(FilePath.MATRIX_PATH.getPath());

            tMatrix = MatrixFileHandler.getMatrix(FilePath.TRIANGULAR_PATH.getPath());
            if (tMatrix == null && !isTriNull) {
                isTriNull = true;
                System.out.println("(DeterminantController) triMatrix is null from the start.");
                System.out.println("Populating with 0.0...");

                MatrixFileHandler.populateFileIfEmpty(FilePath.TRIANGULAR_PATH.getPath());
                tMatrix = MatrixFileHandler.getMatrix(FilePath.TRIANGULAR_PATH.getPath());

                if (tMatrix == null) {
                    System.out.println("(DeterminantController) Error: Unable to load or create a Triangular Matrix");
                }
            } else {
                tView.setMatrixTextFields(matrixTextFields);
                tView.updateMatrixFromUI();
            }
        }

    }

    @Override
    public void saveToFile() {
        List<List<String>> matrixData = matrixView.parseMatrixData(matrix);
        if (matrixData != null) {
            this.determinant = MDO.calculateDeterminant();

            MatrixFileHandler.saveMatrixDataToFile(
                    FilePath.DETERMINANT_PATH.getPath(), determinant,
                    matrixData, MatrixType.DETERMINANT);

            MatrixFileHandler.saveMatrixDataToFile(FilePath.MATRIX_PATH.getPath(),
                    BigDecimal.valueOf(0), matrixData, MatrixType.REGULAR);
            MatrixFileHandler.setMatrix(FilePath.MATRIX_PATH.getPath(), matrix);
        } else {
            System.out.println("(DeterminantController) Error: regMatrix data is null.");
        }
    }

    public void saveTMatrixToFile() {
        List<List<String>> matrixData = tView.parseMatrixData(tMatrix);
        if (matrixData != null) {
            MatrixFileHandler.saveMatrixDataToFile(FilePath.TRIANGULAR_PATH.getPath(),
                    BigDecimal.valueOf(0), matrixData, MatrixType.TRIANGULAR);
            MatrixFileHandler.setMatrix(FilePath.TRIANGULAR_PATH.getPath(), tMatrix);
        } else {
            System.out.println("(DeterminantController) Error: triMatrix data is null.");
        }
    }
}


