package matrix.operators;

import matrix.model.Matrix;
import matrix.model.MatrixOperations;

public class MatrixRREF implements MatrixOperations {

    private Matrix matrix;

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
