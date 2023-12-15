package matrix.operators;

import matrix.model.Matrix;
import matrix.model.MatrixOperations;

public class ElementaryMatrixOperations implements MatrixOperations {
    private Matrix matrix;

    public ElementaryMatrixOperations(Matrix matrix) {
        this.matrix = matrix;
    }

    public void swapRows(int row1, int row2) {
        if (matrix != null && isValidRow(row1) && isValidRow(row2)) {
            double[] temp = matrix.getDoubleMatrix()[row1];
            matrix.getDoubleMatrix()[row1] = matrix.getDoubleMatrix()[row2];
            matrix.getDoubleMatrix()[row2] = temp;
        }
    }

    public void multiplyRow(int row, double multiplier) {
        if (matrix != null && isValidRow(row)) {
            for (int col = 0; col < matrix.getCols(); col++) {
                matrix.getDoubleMatrix()[row][col] *= multiplier;
            }
        }
    }

    public void addRows(int sourceRow, int targetRow, double multiplier) {
        if (matrix != null && isValidRow(sourceRow) && isValidRow(targetRow)) {
            for (int col = 0; col < matrix.getCols(); col++) {
                matrix.getDoubleMatrix()[targetRow][col] += multiplier * matrix.getDoubleMatrix()[sourceRow][col];
            }
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

    public boolean isValidCol(int col) {
        return col >= 0 && col < matrix.getCols();
    }
}
