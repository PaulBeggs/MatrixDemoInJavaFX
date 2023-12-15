package matrix.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import matrix.model.MatrixFileHandler;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SaveController {

    @FXML
    private TextField fileNameField;
    private List<List<TextField>> matrixTextFields;
    private Stage stage;
    private DataManipulation savableController;
    private SaveOperationType saveOperationType;
    private BigDecimal determinantValue;

    @FXML
    public void initialize() {
        setStage(stage);
        setMatrixTextFields(matrixTextFields);

        fileNameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                saveButtonPressed();
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

    public void setSavableController(DataManipulation savableController, SaveOperationType saveOperationType) {
        this.savableController = savableController;
        this.saveOperationType = saveOperationType;
    }

    @FXML
    private void saveButtonPressed() {
        if (savableController != null) {
            String fileName = fileNameField.getText().trim();

            if (!fileName.isEmpty() && !fileName.contains(File.separator) && !fileName.contains(".")) {

                if (saveOperationType == SaveOperationType.MATRIX_ONLY) {
                    List<List<String>> matrixData = getMatrixDataFromTextFields();
                    MatrixFileHandler.saveMatrixToFile("SavedMatrices/matrices/" + fileName + ".txt", matrixData);
                } else if (saveOperationType == SaveOperationType.MATRIX_AND_DETERMINANT) {
                    List<List<String>> matrixData = getMatrixDataFromTextFields();
                    MatrixFileHandler.saveMatrixAndDeterminantToFile("SavedMatrices/determinants/"
                            + fileName + ".txt", determinantValue, matrixData);
                }
                stage.close();
            } else {
                System.out.println("Please enter a valid file name without special characters or file extensions.");
            }
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
