package matrix.model;

import java.util.Arrays;

public class Matrix implements MatrixOperations {

    private double[][] matrix;
    private int sign;
    private int numCols;
    private int numRows;

    public Matrix(int numRows, int numCols) {
        if (numRows <= 0 || numCols <= 0) {
            throw new IllegalArgumentException("Matrix dimensions must be positive. Received numRows: " + numRows + ", numCols: " + numCols);
        }

        this.numRows = numRows;
        this.numCols = numCols;
        this.matrix = new double[numRows][numCols];
    }

    public double getValue(int row, int col) {
        return this.matrix[row][col];
    }

    public void setValue(int row, int col, double value) {
        if (isValidRow(row) && isValidColumn(col)) {
            this.matrix[row][col] = value;
        } else {
            System.out.println("Invalid row or column index.");
        }
    }

    public void setMatrix(double[][] newMatrix) {
        for (int i = 0; i < numRows; i++) {
            System.arraycopy(newMatrix[i], 0, this.matrix[i], 0, numCols);
        }
    }

    public void setToIdentity() {
        for (int i = 0; i < this.numRows; i++) {
            for (int j = 0; j < this.numCols; j++) {
                if (i == j) {
                    this.matrix[i][j] = 1; // Set diagonal elements to 1
                } else {
                    this.matrix[i][j] = 0; // Set off-diagonal elements to 0
                }
            }
        }
    }

    @Override
    public double[][] getDoubleMatrix() {
        return this.matrix;
    }

    @Override
    public int getRows() {
        return this.matrix.length;
    }

    @Override
    public int getCols() {
        return this.matrix[0].length;
    }

    @Override
    public boolean isValidRow(int row) {
        return row >= 0 && row <= numRows;
    }

    private boolean isValidColumn(int col) {
        return col >= 0 && col <= numCols;
    }

    public int getSign() {
        return sign;
    }

    public void setSign(int sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (double[] row : this.matrix) { // Using "this.matrix" to refer to the Matrix class instance variable
            result.append(Arrays.toString(row)).append("\n");
        }
        return result.toString();
    }
}
