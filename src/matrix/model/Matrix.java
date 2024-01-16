package matrix.model;

import matrix.utility.BigDecimalUtil;

import java.math.BigDecimal;
import java.util.Arrays;

public class Matrix implements MatrixOperands {
    private final String[][] data;
    private int numCols;
    private int numRows;

    public Matrix(int numRows, int numCols) {
        if (numRows <= 0 || numCols <= 0) {
            throw new IllegalArgumentException("Matrix dimensions must be positive. Received numRows: " + numRows + ", numCols: " + numCols);
        }

        this.numRows = numRows;
        this.numCols = numCols;

        this.data = new String[numRows][numCols];
    }

    public Matrix(String[][] data) {this.data = data;}
    @Override
    public String[][] getDisplayMatrix() {return data;}
    @Override
    public BigDecimal[][] getComputationalMatrix() {
        int rows = data.length;
        int cols = data[0].length;
        BigDecimal[][] computationalMatrix = new BigDecimal[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                String cell = data[i][j];
                computationalMatrix[i][j] =  BigDecimalUtil.convertStringToBigDecimal(cell);
            }
        }
        return computationalMatrix;
    }
    @Override
    public void swapRows(int row1, int row2) {
        if (data != null && isValidRow(row1) && isValidRow(row2)) {
            String[] temp = data[row1];
            data[row1] = data[row2];
            data[row2] = temp;
        } else {
            throw new IllegalArgumentException("Invalid row indices for swapping: " + row1 + ", " + row2);
        }
    }
    public void multiplyRow(int row, BigDecimal scalar) {
        if (isValidRow(row)) {
            for (int col = 0; col < getCols(); col++) {
                BigDecimal currentValue = new BigDecimal(data[row][col]);
                data[row][col] = BigDecimalUtil.convertBigDecimalToString(currentValue.multiply(scalar));
            }
        } else {
            System.out.println("Invalid row index: " + row);
        }
    }

    public void addRows(int sourceRow, int targetRow, BigDecimal multiplier) {
        if (isValidRow(sourceRow) && isValidRow(targetRow)) {
            for (int col = 0; col < getCols(); col++) {
                BigDecimal value1 = new BigDecimal(data[sourceRow][col]);
                BigDecimal value2 = new BigDecimal(data[targetRow][col]);
                data[targetRow][col] = BigDecimalUtil.convertBigDecimalToString(value1.multiply(multiplier).add(value2));
            }
        } else {
            System.out.println("Invalid row indices: " + sourceRow + ", " + targetRow);
        }
    }

    public String getValue(int row, int col) {return this.data[row][col];}

    public void setValue(int row, int col, String value) {
        if (isValidRow(row) && isValidColumn(col)) {
            this.data[row][col] = value;
        }
    }

    public void setMatrix (String[][] newMatrix) {
        for (int i = 0; i < numRows; i++) {
            System.arraycopy(newMatrix[i], 0, this.data[i], 0, numCols);
        }
    }

    public Matrix copy() {
        Matrix copiedMatrix = new Matrix(this.getRows(), this.getCols());
        for (int i = 0; i < this.getRows(); i++) {
            for (int j = 0; j < this.getCols(); j++) {
                copiedMatrix.setValue(i, j, this.getValue(i, j));
            }
        }
        return copiedMatrix;
    }

    public BigDecimalMatrix toBigDecimalMatrix() {
        int rows = data.length;
        int cols = data[0].length;
        BigDecimalMatrix bigDecimalMatrix = new BigDecimalMatrix(rows, cols);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                String cell = data[i][j];
                bigDecimalMatrix.setValue(i, j, BigDecimalUtil.convertStringToBigDecimal(cell));
            }
        }
        return bigDecimalMatrix;
    }

    @Override
    public int getRows() {return this.data.length;}
    @Override
    public int getCols() {return data.length > 0 ? data[0].length : 0;}
    @Override
    public boolean isValidRow(int row) {return row >= 0 && row < numRows;}
    @Override
    public boolean isValidCol(int col) {return false;}
    private boolean isValidColumn(int col) {return col >= 0 && col < numCols;}
    public boolean isSquare() {return getRows() == getCols();}

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (String[] row : this.data) { // Using "this.matrix" to refer to the Matrix class instance variable
            result.append(Arrays.toString(row)).append("\n");
        }
        return result.toString();
    }
}
