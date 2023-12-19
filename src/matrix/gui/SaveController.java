package matrix.gui;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import matrix.fileManaging.MatrixType;
import matrix.model.Matrix;
import matrix.fileManaging.MatrixFileHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SaveController {
    @FXML
    private TextField fileNameField;
    @FXML
    private ChoiceBox<String> matrixSelectionComboBox;
    private List<List<TextField>> matrixTextFields;
    private Stage stage;
    private BigDecimal determinantValue;
    private Matrix triangularMatrix;

    @FXML
    public void initialize() {
        setStage(stage);
        setMatrixTextFields(matrixTextFields);

        matrixSelectionComboBox.getItems().addAll("Default Matrix", "Determinant Matrix", "Triangular Matrix");
        matrixSelectionComboBox.setValue("Default Matrix");

        fileNameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                saveButtonPressed();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                stage.close();
            }
        });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public void setMatrixTextFields(List<List<TextField>> matrixTextFields) {
        this.matrixTextFields = matrixTextFields;
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

            List<List<String>> matrixData = getMatrixDataFromTextFields();

            switch (selectedMatrixOption) {
                case "Default Matrix" ->
                        MatrixFileHandler.saveMatrixDataToFile("savedMatrices/matrices/" + fileName + ".txt", BigDecimal.valueOf(0), matrixData, MatrixType.REGULAR);
                case "Determinant Matrix" -> {
                    if (getDeterminant() != null) {
                        MatrixFileHandler.saveMatrixDataToFile("savedMatrices/determinants/"
                                + fileName + ".txt", determinantValue, matrixData, MatrixType.DETERMINANT);
                    } else {
                        System.out.println("Must find the determinant first before you can save.");
                    }
                }
                case "Triangular Matrix" -> {
                    if (getTriangularMatrix() != null) {

                    } else {
                        System.out.println("Must find the determinant first before you can save.");
                    }
                }
                default -> {
                }
            }
            stage.close();

        } else {
            // Show an error message or handle invalid input
            System.out.println("Please enter a valid file name without special characters or file extensions.");
        }
    }

    public List<List<String>> getMatrixDataFromTextFields() {
        List<List<String>> matrixData = new ArrayList<>();

        for (List<TextField> row : this.matrixTextFields) {
            List<String> rowData = row.stream().map(TextField::getText).collect(Collectors.toList());
            matrixData.add(rowData);
        }

        return matrixData;
    }
}
