package matrix.operators;

import matrix.model.FilePath;
import matrix.model.Matrix;
import matrix.model.MatrixFileHandler;
import matrix.model.MatrixOperations;

public class ElementaryMatrixOperations implements MatrixOperations {
    private Matrix matrix;

    public ElementaryMatrixOperations(Matrix matrix) {
        this.matrix = matrix;
    }

    public void swapRows(int row1, int row2) {
        matrix = MatrixFileHandler.getMatrix(FilePath.MATRIX_PATH.getPath());
        if (matrix != null && isValidRow(row1) && isValidRow(row2)) {
            double[] temp = matrix.getDoubleMatrix()[row1];
            matrix.getDoubleMatrix()[row1] = matrix.getDoubleMatrix()[row2];
            matrix.getDoubleMatrix()[row2] = temp;

            MatrixFileHandler.setMatrix(FilePath.MATRIX_PATH.getPath(), matrix);
            System.out.println("this should be the matrix that is printed within 'swapRows: \n" + matrix);
        } else {
            System.out.println("matrix is null inside 'swappedRows:' \n" + matrix);
        }
    }

    public void multiplyRow(int row, double multiplier) {
        matrix = MatrixFileHandler.getMatrix(FilePath.MATRIX_PATH.getPath());
        if (matrix != null && isValidRow(row)) {
            for (int col = 0; col < matrix.getCols(); col++) {
                matrix.getDoubleMatrix()[row][col] *= multiplier;
            }
            MatrixFileHandler.setMatrix(FilePath.MATRIX_PATH.getPath(), matrix);
            System.out.println("this should be the matrix that is printed within 'multipliedRow: \n" + matrix);
        } else {
            System.out.println("matrix is null inside 'multipliedRows:' \n" + matrix);

        }
    }

    public void addRows(int sourceRow, int targetRow, double multiplier) {
        matrix = MatrixFileHandler.getMatrix(FilePath.MATRIX_PATH.getPath());
        if (matrix != null && isValidRow(sourceRow) && isValidRow(targetRow)) {
            for (int col = 0; col < matrix.getCols(); col++) {
                matrix.getDoubleMatrix()[targetRow][col] += multiplier * matrix.getDoubleMatrix()[sourceRow][col];
            }
            MatrixFileHandler.setMatrix(FilePath.MATRIX_PATH.getPath(), matrix);
            System.out.println("this should be the matrix that is printed within 'addRows: \n" + matrix);
        } else {
            System.out.println("matrix is null inside 'addRows:' \n" + matrix);
        }

    }


    @Override
    public double[][] getDoubleMatrix() {
        return new double[0][];
    }

    @Override
    public int getRows() {
        return 0;
    }

    @Override
    public int getCols() {
        return 0;
    }

    @Override
    public boolean isValidRow(int row) {
        return row >= 0 && row < matrix.getRows();
    }
}
