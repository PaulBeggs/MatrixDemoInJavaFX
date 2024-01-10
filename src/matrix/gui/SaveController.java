package matrix.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import matrix.fileManaging.FilePath;
import matrix.fileManaging.MatrixType;
import matrix.model.Matrix;
import matrix.fileManaging.MatrixFileHandler;
import matrix.model.MatrixCell;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SaveController {
    @FXML
    private TextField fileNameField;
    @FXML
    private ChoiceBox<String> matrixSelectionComboBox;
    private List<List<TextField>> matrixTextFields;
    private MatrixCell[][] matrixCells;
    private BigDecimal determinantValue;
    private Matrix triangularMatrix;

    @FXML
    public void initialize() {
        setMatrixTextFields(matrixTextFields);

        matrixSelectionComboBox.getItems().addAll("Default Matrix", "Determinant Matrix", "Triangular Matrix");
        matrixSelectionComboBox.setValue("Default Matrix");

        fileNameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                saveButtonPressed();
            }
        });
    }

    public void setMatrixTextFields(List<List<TextField>> matrixTextFields) {
        this.matrixTextFields = matrixTextFields;
    }

    public MatrixCell[][] getMatrixCells() {
        return matrixCells;
    }
    public void setMatrixCells (MatrixCell[][] matrixCells) {
        this.matrixCells = matrixCells;
    }
    public void setDeterminantValue(BigDecimal determinantValue) {
        this.determinantValue = determinantValue;
    }
    public BigDecimal getDeterminant() {
        return determinantValue;
    }
    public Matrix getTriangularMatrix() {
        return triangularMatrix;
    }

    @FXML
    private void saveButtonPressed() {
        String fileName = fileNameField.getText().trim();

        if (!fileName.isEmpty() && !fileName.contains(File.separator) && !fileName.contains(".")) {

            String selectedMatrixOption = matrixSelectionComboBox.getValue();

            List<List<String>> matrixData;

            switch (selectedMatrixOption) {
                case "Default Matrix" -> {
                    matrixData = MatrixFileHandler.loadMatrixDataFromFile(FilePath.MATRIX_PATH.getPath());
                    MatrixFileHandler.saveMatrixDataToFile("savedMatrices/matrices/"
                            + fileName + ".txt", BigDecimal.valueOf(0), matrixData, MatrixType.REGULAR);

                    MatrixApp.getPrimaryStage().close();
                }
                case "Determinant Matrix" -> {
                    if (getDeterminant() != null) {
                        matrixData = MatrixFileHandler.loadMatrixDataFromFile(FilePath.TRIANGULAR_PATH.getPath());
                        MatrixFileHandler.saveMatrixDataToFile("savedMatrices/determinants/"
                                + fileName + ".txt", determinantValue, matrixData, MatrixType.DETERMINANT);
                        MatrixApp.getPrimaryStage().close();
                    } else {
                        showErrorPopup("Must find the determinant before you can save.");
//                        System.out.println("Must find the determinant before you can save.");
                    }
                }
                case "Triangular Matrix" -> {
                    if (getTriangularMatrix() != null) {

                    } else {
                        showErrorPopup("Must find the triangular matrix before you can save.");
//                        System.out.println("Must find the triangular matrix before you can save.");
                    }
                }
                default -> {
                }
            }


        } else {
            // Show an error message or handle invalid input
            showErrorPopup("Please enter a valid file name without special characters or file extensions.");
            fileNameField.setText("Example");
        }
    }

    private void showErrorPopup(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}
