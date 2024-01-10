package matrix.operators;

import matrix.fileManaging.FilePath;
import matrix.fileManaging.MatrixType;
import matrix.model.Matrix;
import matrix.fileManaging.MatrixFileHandler;
import matrix.model.MatrixOperations;

import java.math.BigDecimal;
import java.util.List;

public class ElementaryMatrixOperations {
    private final Matrix matrix;

    public ElementaryMatrixOperations(Matrix matrix) {
        this.matrix = matrix;
    }

    public void swapRows(int row1, int row2, boolean save) {
        if (matrix != null && matrix.isValidRow(row1) && matrix.isValidRow(row2)) {
            double[] temp = matrix.getDoubleMatrix()[row1];
            matrix.getDoubleMatrix()[row1] = matrix.getDoubleMatrix()[row2];
            matrix.getDoubleMatrix()[row2] = temp;
            if (save) {
                saveMatrix();
            }
        } else {
            System.out.println("Invalid row indices or matrix is null");
        }
    }

    public void multiplyRow(int row, double multiplier, boolean save) {
        if (matrix != null && matrix.isValidRow(row)) {
            for (int col = 0; col < matrix.getCols(); col++) {
                matrix.getDoubleMatrix()[row][col] *= multiplier;
            }
            if (save) {
                saveMatrix();
            }
        } else {
            System.out.println("Invalid row index or matrix is null");
        }
    }

    public void addRows(int sourceRow, int targetRow, double multiplier, boolean save) {
        if (matrix != null && matrix.isValidRow(sourceRow) && matrix.isValidRow(targetRow)) {
            for (int col = 0; col < matrix.getCols(); col++) {
                matrix.getDoubleMatrix()[targetRow][col] += multiplier * matrix.getDoubleMatrix()[sourceRow][col];
            }
            if (save) {
                saveMatrix();
            }
        } else {
            System.out.println("Invalid row indices or matrix is null");
        }
    }

    private void saveMatrix() {
        List<List<String>> matrixData = MatrixFileHandler.loadMatrixDataFromMatrix(matrix);
        MatrixFileHandler.saveMatrixDataToFile(FilePath.MATRIX_PATH.getPath(), BigDecimal.valueOf(0), matrixData, MatrixType.REGULAR);
    }
}
