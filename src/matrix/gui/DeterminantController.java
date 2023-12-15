package matrix.gui;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import matrix.model.Matrix;
import matrix.model.MatrixFileHandler;
import matrix.model.MatrixView;
import matrix.operators.MatrixDeterminantOperations;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    private Movement clock;

    private class Movement extends AnimationTimer {

        private final long FRAMES_PER_SEC = 50L;
        private final long INTERVAL = 1000000L / FRAMES_PER_SEC;
        private long last = 0;

        @Override
        public void handle(long now) {
            if (now - last > INTERVAL) {
                //System.out.println("This is running!");
//                if (matrixGrid.getBoundsInParent().getMaxX() > borderPane.getPrefWidth()) {
//                    borderPane.setPrefWidth(matrixGrid.getBoundsInParent().getMaxX() + 10);
//                }

                matrixView.saveToFile(FilePath.MATRIX_PATH.getPath());
                saveToFile();
                last = now;
            }

        }
    }

    @FXML
    private void initialize() {
        tickedBox = false;
        matrix = MatrixFileHandler.getMatrix(FilePath.MATRIX_PATH.getPath());

        matrixTextFields = new ArrayList<>();
        matrixView = new MatrixView(matrix, matrixGrid, matrixTextFields);
        determinantOperations = new MatrixDeterminantOperations(matrix, matrixView);

        checkBox.setSelected(true);

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

        scenes.getItems().setAll(Scenes.values());
        scenes.setValue(Scenes.DETERMINANT);

        scenes.setOnAction(event -> {
            Scenes selectedScene = scenes.getValue();
            try {
                stop();
                selectedScene.switchScene(event);
            } catch (IOException e) {
                e.getMessage();
            }
        });
        clock = new Movement();

        update();
    }

    @FXML
    public void start() {
        clock.start();
    }

    @FXML
    public void stop() {
        clock.stop();
    }

    @FXML
    public void handleDeterminantFunctionality() {
        stop();
        tickedBox = true;
        // System.out.println("2nd Matrix: \n" + matrix + "\n");

        if (checkBox.isSelected()) {
            showDeterminantAnimation();

            stop();
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
        if (tickedBox) {
            matrix = MatrixFileHandler.getMatrix(FilePath.TRIANGULAR_PATH.getPath());
            matrixView.updateViews(FilePath.TRIANGULAR_PATH.getPath(), false);
            stop();
        }
    }

    private void handleMissingMatrix() {
        //System.out.println("Matrix not found. Generate a matrix first.");
        stop();
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
        saveController.setDeterminantValue(determinant);
        saveController.setStage(saveStage);
        saveController.setSavableController(this, SaveOperationType.MATRIX_AND_DETERMINANT);


        Scene saveScene = new Scene(root);
        saveStage.setScene(saveScene);
        saveStage.showAndWait();
    }

    private void showDeterminantAnimation() {
        if (!determinantIsZero) {
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
//            stop();
        } else {
            showErrorPopup("Determinant is either zero or undefined. Therefore, the matrix is not invertible.");
            stop();
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
        matrixView.updateViews(FilePath.MATRIX_PATH.getPath(), true);
        if (matrix != null) {
            this.determinant = determinantOperations.calculateDeterminant();
            List<List<String>> matrixData = matrixView.parseMatrixData(matrix);
            MatrixFileHandler.saveMatrixAndDeterminantToFile
                    (FilePath.DETERMINANT_PATH.getPath(), determinant,
                            matrixData);
            processMatrixForDeterminant();
            start();
        } else {
            handleMissingMatrix();
            stop();
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

            start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveToFile() {
        matrixView.updateMatrixFromUI();
        matrix = MatrixFileHandler.getMatrix(FilePath.MATRIX_PATH.getPath());

        if (matrix != null) {
            List<List<String>> matrixData = matrixView.parseMatrixData(matrix);
            if (matrixData != null) {
                MatrixFileHandler.saveMatrixToFile(FilePath.MATRIX_PATH.getPath(), matrixData);
            } else {
                System.out.println("Error: Matrix data is null.");
            }
        } else {
            System.out.println("Error: Matrix is null.");
        }
    }
}


