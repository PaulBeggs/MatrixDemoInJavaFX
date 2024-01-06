package matrix.model;

import javafx.scene.control.TextField;
import matrix.fileManaging.FilePath;
import matrix.fileManaging.MatrixFileHandler;
import matrix.fileManaging.MatrixType;

import java.math.BigDecimal;
import java.util.List;

public class MatrixCell {
    private final TextField textField;
    private final int row;
    private final int col;
    private final Matrix matrix;

    public MatrixCell(int row, int col, String initialValue, boolean isEditable) {
        this.row = row;
        this.col = col;
        this.matrix = MatrixSingleton.getInstance(); // Get the shared Matrix instance
        this.textField = new TextField(initialValue);
        setupTextField(isEditable);
    }


    private void setupTextField(boolean isEditable) {
        textField.getStyleClass().add("textfield-grid-cell");
        textField.setEditable(isEditable);

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                textField.setText(newValue.replaceAll("[^\\d.]", ""));
            } else {
                try {
                    double newCellValue = Double.parseDouble(newValue);
                    matrix.setValue(row, col, newCellValue); // Update the Matrix object
                    System.out.println("Did the matrix update properly? \n" + matrix);
                    MatrixSingleton.saveMatrix();
                } catch (NumberFormatException e) {
                    textField.setText(oldValue);
                }
            }
        });
    }

//    private void saveMatrix() {
//        List<List<String>> matrixData = MatrixFileHandler.loadMatrixDataFromMatrix(matrix);
//        MatrixFileHandler.saveMatrixDataToFile(FilePath.MATRIX_PATH.getPath(),
//                BigDecimal.valueOf(0), matrixData, MatrixType.REGULAR);
//        System.out.println("The matrix should be saved correctly within the matrixcell class: \n" + matrix);
//    }

    public TextField getTextField() {
        return textField;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
