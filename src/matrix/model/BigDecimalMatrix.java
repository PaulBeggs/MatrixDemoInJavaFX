package matrix.model;

import matrix.utility.BigDecimalUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public class BigDecimalMatrix implements MatrixOperands {

    private BigDecimal[][] data;
    private int numCols;
    private int numRows;
    private int sign = 1;

    public BigDecimalMatrix(int numRows, int numCols) {
        if (numRows <= 0 || numCols <= 0) {
            throw new IllegalArgumentException("Matrix dimensions must be positive. Received numRows: " + numRows + ", numCols: " + numCols);
        }
        this.numRows = numRows;
        this.numCols = numCols;
        this.data = new BigDecimal[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            Arrays.fill(data[i], BigDecimal.ZERO);
        }
    }

    public BigDecimalMatrix(BigDecimal[][] data) {
        this.data = data;
        this.numRows = getRows();
        this.numCols = getCols();
    }

    public BigDecimalMatrix copy() {
        BigDecimalMatrix copiedMatrix = new BigDecimalMatrix(this.getRows(), this.getCols());
        for (int i = 0; i < this.getRows(); i++) {
            for (int j = 0; j < this.getCols(); j++) {
                copiedMatrix.setValue(i, j, this.getValue(i, j));
            }
        }
        return copiedMatrix;
    }

    @Override
    public String[][] getDisplayMatrix() {
        int rows = data.length;
        int cols = data[0].length;
        String[][] displayMatrix = new String[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                BigDecimal cell = data[i][j];
                displayMatrix[i][j] =  BigDecimalUtil.convertBigDecimalToString(cell);
            }
        }
        return displayMatrix;
    }

    @Override
    public BigDecimal[][] getComputationalMatrix() {return data;}

    public void swapRows(int row1, int row2) {
        if (isValidRow(row1) && isValidRow(row2)) {
            BigDecimal[] temp = data[row1];
            data[row1] = data[row2];
            data[row2] = temp;
        } else {
            System.out.println("These are the correct amount of rows: \n" + getRows());
            throw new IllegalArgumentException("Invalid row indices for swapping: " + row1 + ", " + row2);
        }
    }

    public void multiplyRow(int row, BigDecimal scalar) {
        if (isValidRow(row)) {
            for (int col = 0; col < getCols(); col++) {
                data[row][col] = data[row][col].multiply(scalar);
            }
        } else {
            throw new IllegalArgumentException("Invalid row index: " + row);
        }
    }

    public void addRows(int sourceRow, int targetRow, BigDecimal multiplier) {
        if (isValidRow(sourceRow) && isValidRow(targetRow)) {
            for (int col = 0; col < getCols(); col++) {
                data[targetRow][col] = data[targetRow][col].add(data[sourceRow][col].multiply(multiplier));
            }
        } else {
            throw new IllegalArgumentException("Invalid row indices: " + sourceRow + ", " + targetRow);
        }
    }

    public Matrix toMatrix() {
        int rows = data.length;
        int cols = data[0].length;
        Matrix matrix = new Matrix(rows, cols);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                BigDecimal cell = data[i][j];
                matrix.setValue(i, j, BigDecimalUtil.convertBigDecimalToString(cell));
            }
        }
        return matrix;
    }

    @Override
    public int getRows() {return data.length;}
    @Override
    public int getCols() {return data.length > 0 ? data[0].length : 0;}
    @Override
    public boolean isValidRow(int row) {
        return row >= 0 && row < numRows;
    }
    @Override
    public boolean isValidCol(int col) {return false;}
    private boolean isValidColumn(int col) {return col >= 0 && col < numCols;}

    public void setValue(int row, int col, BigDecimal value) {
        if (isValidRow(row) && isValidColumn(col)) {
            data[row][col] = value;
        }
    }

    public BigDecimal getValue(int row, int col) {return data[row][col];}

    public boolean isSquare() {return getRows() == getCols();}

    public void convertToEchelonForm() {
        int numRows = getRows();
        int numCols = getCols();

        for (int p = 0; p < Math.min(numRows, numCols); p++) {
            int maxRow = findPivotRow(p);
            if (maxRow != p) {
                swapRows(p, maxRow);
            }

            normalizePivotRow(p);
            eliminateBelow(p);
        }
    }


    public void convertToReducedEchelonForm() {
        // First, convert to echelon form
        convertToEchelonForm();

        // Then, iterate from bottom to top to make elements above pivots zero
        for (int p = getRows() - 1; p >= 0; p--) {
            // Find the pivot column in the current row
            int pivotCol = findPivotColumn(p);
            if (pivotCol != -1) {
                eliminateAbove(p, pivotCol);
            }
        }
    }

    private int findPivotColumn(int row) {
        for (int j = 0; j < getCols(); j++) {
            if (getValue(row, j).compareTo(BigDecimal.ZERO) != 0) {
                return j; // Pivot column found
            }
        }
        return -1; // No pivot in this row
    }

    private int findPivotRow(int p) {
        int maxRow = p;
        for (int i = p + 1; i < getRows(); i++) {
            BigDecimal currentVal = (getValue(i, p).abs());
            BigDecimal maxVal = (getValue(maxRow, p).abs());
            if (currentVal.compareTo(maxVal) > 0) {
                maxRow = i;
            }
        }
        return maxRow;
    }

    private void normalizePivotRow(int p) {
        BigDecimal pivotValue = getValue(p, p);
        if (pivotValue.compareTo(BigDecimal.ZERO) != 0) {
            multiplyRow(p, BigDecimal.ONE.divide(pivotValue, BigDecimalUtil.COMPUTATIONAL_SCALE, RoundingMode.HALF_UP));
        }
    }

    private void eliminateBelow(int p) {
        int numCols = getCols();
        for (int i = p + 1; i < getRows(); i++) {
            BigDecimal factor = (getValue(i, p).divide(getValue(p, p), BigDecimalUtil.COMPUTATIONAL_SCALE, RoundingMode.HALF_UP));
            if (factor.compareTo(BigDecimal.ZERO) != 0) {
                for (int j = 0; j < numCols; j++) {
                    BigDecimal valueToAdd = (factor.multiply(getValue(p, j)).negate());
                    setValue(i, j, (getValue(i, j).add(valueToAdd)));
                }
            }
        }
    }

    private void eliminateAbove(int pivotRow, int pivotCol) {
        for (int i = 0; i < pivotRow; i++) {
            BigDecimal factor = (getValue(i, pivotCol));
            if (factor.compareTo(BigDecimal.ZERO) != 0) {
                addRows(pivotRow, i, factor.negate());
            }
        }
    }

    // The method, "getRows()" is used interchangeably for "getCols()" as this matrix will always need
    // to be rectangular to have a defined determinant. Therefore, the dimensions will be equal.

    public BigDecimal calculateDeterminant() {
        if (!isSquare()) {
            System.out.println("Determinant is not defined for non-square matrices.");
            throw new IllegalArgumentException("Determinant is not defined for non-square matrices.");
        }

        if (!isUpperTriangular() && ! isLowerTriangular()) {
            System.out.println("Matrix before 'Triangularization': \n" + Arrays.deepToString(data));
            makeTriangular();
        }
        BigDecimal deter = multiplyDiameter().multiply(BigDecimal.valueOf(getSign()));
        System.out.println("Triangular matrix \n: " + Arrays.deepToString(data));
        return deter;
    }

    public void makeTriangular() {
        for (int col = 0; col < data.length; col++) {
            sortCol(col);
            for (int row = data.length - 1; row > col; row--) {
                BigDecimal element = data[row][col];
                if (element.compareTo(BigDecimal.ZERO) == 0) {
                    continue;
                }

                BigDecimal x = data[row][col];
                BigDecimal y = data[row - 1][col];

                multiplyRowForDeterminant(row, y.negate().divide(x, BigDecimalUtil.COMPUTATIONAL_SCALE, RoundingMode.HALF_UP));

                addRowsForDeterminant(row, row - 1);
                multiplyRowForDeterminant(row, x.negate().divide(y, BigDecimalUtil.COMPUTATIONAL_SCALE, RoundingMode.HALF_UP));
            }
        }
    }

    private void sortCol(int paramCol) {
        for (int row = getRows() - 1; row >= paramCol; row--) {
            for (int col = getRows() - 1; col >= paramCol; col--) {
                BigDecimal temp1 = data[row][paramCol];
                BigDecimal temp2 = data[col][paramCol];

                if (temp1.abs().compareTo(temp2.abs()) < 0) {
                    swapRowsForDeterminant(row, col);
                }
            }
        }
    }

    private void swapRowsForDeterminant(int row1, int row2) {
        setSign(getSign() * -1);
        swapRows(row1, row2);
    }

    public void multiplyRowForDeterminant(int row, BigDecimal scalar) {
        if (scalar.compareTo(BigDecimal.ZERO) <0) {
            setSign(getSign() * -1);
        }
        multiplyRow(row, scalar);
    }

    public void addRowsForDeterminant(int row1, int row2) {
        for (int col = 0; col < getCols(); col++) {
            BigDecimal value1 = data[row1][col];
            BigDecimal value2 = data[row2][col];

            data[row1][col] = value1.add(value2);
        }
    }

    public BigDecimal multiplyDiameter() {
        BigDecimal result = BigDecimal.ONE;
        for (int row = 0; row < getRows(); row++) {
            for (int col = 0; col < getRows(); col++) {
                if (row == col) {
                    result = result.multiply(data[row][col]);
                }
            }
        }
        return result;
    }

    private boolean isUpperTriangular() {
        if (getRows() < 2) {
            return false;
        }
        for (int row = 0; row < getRows(); row++) {
            for (int col = 0; col < row; col++) {
                if (data[row][col].compareTo(BigDecimal.ZERO) != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isLowerTriangular() {
        if (getRows() < 2) {
            return false;
        }
        for (int col = 0; col < getRows(); col++) {
            for (int row = 0; col > row; row++) {
                if (data[row][col].compareTo(BigDecimal.ZERO) != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getSign() {return sign;}
    public void setSign(int sign) {this.sign = sign;}

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (BigDecimal[] row : this.data) { // Using "this.matrix" to refer to the Matrix class instance variable
            result.append(Arrays.toString(row)).append("\n");
        }
        return result.toString();
    }
}
