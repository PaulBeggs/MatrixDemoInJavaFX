package matrix.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import matrix.fileManaging.FilePath;
import matrix.fileManaging.MatrixFileHandler;
import matrix.fileManaging.MatrixType;
import matrix.model.Matrix;
import matrix.model.MatrixCell;
import matrix.util.ErrorsAndSyntax;

import java.io.File;
import java.util.List;

public class SaveController {
    @FXML
    private TextField fileNameField;
    @FXML
    private ChoiceBox<String> matrixSelectionComboBox;
    @FXML
    private CheckBox saveAsFractions;
    @FXML
    private Button saveButton;
    private List<List<TextField>> matrixTextFields;
    private MatrixCell[][] matrixCells;
    private String determinantValue;
    private Matrix triangularMatrix;
    private Stage saveStage;

    @FXML
    public void initialize() {
        setToolTips();
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
    public void setDeterminantValue(String determinantValue) {
        this.determinantValue = determinantValue;
    }
    public String getDeterminant() {
        return determinantValue;
    }
    public Matrix getTriangularMatrix() {
        return triangularMatrix;
    }

    @FXML
    private void saveButtonPressed() {
        ThemeController.setFraction(saveAsFractions.isSelected());
        MatrixApp.setFractionMode(saveAsFractions.isSelected());

        String fileName = fileNameField.getText().trim();

        if (!fileName.isEmpty() && !fileName.contains(File.separator) && !fileName.contains(".")) {

            String selectedMatrixOption = matrixSelectionComboBox.getValue();

            List<List<String>> matrixData;

            switch (selectedMatrixOption) {
                case "Default Matrix" -> {
                    matrixData = MatrixFileHandler.loadMatrixDataFromFile(FilePath.MATRIX_PATH.getPath());
                    MatrixFileHandler.saveMatrixDataToFile("savedMatrices/matrices/"
                            + fileName + ".txt", "0", matrixData, MatrixType.REGULAR);
                    saveStage.close();
                }
                case "Determinant Matrix" -> {
                    if (getDeterminant() != null) {
                        matrixData = MatrixFileHandler.loadMatrixDataFromFile(FilePath.TRIANGULAR_PATH.getPath());
                        MatrixFileHandler.saveMatrixDataToFile("savedMatrices/determinants/"
                                + fileName + ".txt", determinantValue, matrixData, MatrixType.DETERMINANT);
                        saveStage.close();
                    } else {
                        ErrorsAndSyntax.showErrorPopup("Must find the determinant before you can save.");
//                        System.out.println("Must find the determinant before you can save.");
                    }
                }
                case "Triangular Matrix" -> {
                    if (getTriangularMatrix() != null) {
                        saveStage.close();
                    } else {
                        ErrorsAndSyntax.showErrorPopup("Must find the triangular matrix before you can save.");
//                        System.out.println("Must find the triangular matrix before you can save.");
                    }
                }
                default -> {
                }
            }


        } else {
            // Show an error message or handle invalid input
            ErrorsAndSyntax.showErrorPopup("Please enter a valid file name without special characters or file extensions.");
            fileNameField.setText("Example");
        }
    }
    public void setStage(Stage stage) {saveStage = stage;}

    public void setToolTips() {
        fileNameField.setTooltip(new Tooltip("Enter file name"));
        matrixSelectionComboBox.setTooltip(new Tooltip("Save matrix type"));
        saveButton.setTooltip(new Tooltip("Save"));
    }
}
