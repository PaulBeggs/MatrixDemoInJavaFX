package matrix.model;

import matrix.util.MatrixUtil;

import java.util.Arrays;

import static java.lang.Double.parseDouble;
import static java.lang.String.*;
import static matrix.gui.MatrixApp.isFractionMode;
import static matrix.util.ExpressionEvaluator.evaluate;
import static matrix.util.MatrixUtil.*;

public class Matrix implements MatrixOperations {
    private String[][] data;
    private int numCols;
    private int numRows;
    private int sign = 1;

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
    public String[][] getMatrix() {return data;}

    @Override
    public void swapRows(int row1, int row2) {
        if (data != null && isValidRow(row1) && isValidRow(row2)) {
            if (isFractionMode()) {
                // Convert each element in both rows from decimal to fraction before swapping
                for (int i = 0; i < data[row1].length; i++) {
                    String temp = convertDecimalToFraction(data[row1][i]);
                    data[row1][i] = convertDecimalToFraction(data[row2][i]);
                    data[row2][i] = temp;
                }
            } else {
                // Convert each element in both rows from fraction to decimal before swapping
                for (int i = 0; i < data[row1].length; i++) {
                    String temp = formatToTenDigits(parseDouble(convertFractionToDecimal(data[row1][i])));
                    data[row1][i] = formatToTenDigits(parseDouble(convertFractionToDecimal(data[row2][i])));
                    data[row2][i] = temp;
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid row indices for swapping: " + row1 + ", " + row2);
        }
    }

    @Override
    public void multiplyRow(int row, double scalar) {
        if (isValidRow(row)) {
            for (int col = 0; col < getCols(); col++) {
                try {
                    double currentValue = evaluate(data[row][col]); // Evaluate the current value
                    System.out.println("Current value: " + currentValue + " from: " + Arrays.deepToString(data) + " at " + row + ", " + col);
                    System.out.println();
                    double multipliedValue = currentValue * scalar;  // Multiply by the scalar
                    System.out.println("multiplied Value: " + multipliedValue);

                    // Convert the result to the appropriate format
                    String result = isFractionMode() ?
                            convertDecimalToFraction(String.valueOf(multipliedValue)) :
                            formatToTenDigits(parseDouble(convertFractionToDecimal(String.valueOf(multipliedValue))));

                    System.out.println("result: " + result);

                    data[row][col] = result; // Assign the result back to the row
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Problem parsing at column " + col + " in row " + row);
                }
            }
        } else {
            System.out.println("Invalid row index: " + row);
        }
    }

    public void addRows(int sourceRow, int targetRow, double multiplier) {
        if (isValidRow(sourceRow) && isValidRow(targetRow)) {
            for (int col = 0; col < getCols(); col++) {
                try {
                    double value1 = evaluate(data[sourceRow][col]); // Evaluate the value in sourceRow
                    double value2 = evaluate(data[targetRow][col]); // Evaluate the value in targetRow
                    double sum = (value1 * multiplier) + value2;    // Calculate the sum

                    // Convert the result to the appropriate format
                    String result = isFractionMode() ?
                            convertDecimalToFraction(String.valueOf(sum)) :
                            formatToTenDigits(parseDouble(convertFractionToDecimal(String.valueOf(sum))));

                    data[targetRow][col] = result; // Assign the result to the target row
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Trouble parsing the input at column " + col + ".");
                }
            }
        } else {
            System.out.println("Invalid row indices: " + sourceRow + ", " + targetRow);
        }
    }

    public String getStringValue(int row, int col) {return this.data[row][col];}

    public void setValue(int row, int col, String value) {
        if (isValidRow(row) && isValidColumn(col)) {
            if (isFractionMode()) {
                this.data[row][col] = convertDecimalToFraction(value);
            } else {
                this.data[row][col] = convertFractionToDecimal(value);
            }
        } else {
            System.out.println("Invalid row & col indices: " + row + ", " + col);
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
                copiedMatrix.setValue(i, j, this.getStringValue(i, j));
            }
        }
        return copiedMatrix;
    }

    public void transpose() {
        // Create a temporary matrix to hold the transposed data
        String[][] tempData = new String[numCols][numRows];

        // Perform the transposition
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                tempData[j][i] = this.data[i][j];
            }
        }

        this.data = tempData;
        int temp = numRows;
        numRows = numCols;
        numCols = temp;
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
            if (parseDouble(getStringValue(row, j)) != 0) {
                return j; // Pivot column found
            }
        }
        return -1; // No pivot in this row
    }

    private int findPivotRow(int p) {
        int maxRow = p;
        for (int i = p + 1; i < getRows(); i++) {
            double currentVal = Math.abs(parseDouble(getStringValue(i, p)));
            double maxVal = Math.abs(parseDouble(getStringValue(maxRow, p)));
            if (currentVal - maxVal > 0) {
                maxRow = i;
            }
        }
        return maxRow;
    }

    private void normalizePivotRow(int p) {
        double pivotValue = parseDouble(getStringValue(p, p));
        if (Double.compare(pivotValue, 0) != 0) {
            multiplyRow(p, 1.0 / pivotValue);

        }
    }

    private void eliminateBelow(int p) {
        int numCols = getCols();
        for (int i = p + 1; i < getRows(); i++) {
            double factor = parseDouble(getStringValue(i, p)) / parseDouble(getStringValue(p, p));
            if (Double.compare(factor, 0) != 0) {
                for (int j = 0; j < numCols; j++) {
                    double valueToAdd = factor * -parseDouble(getStringValue(p, j));
                    setValue(i, j, getStringValue(i, j) + valueToAdd);
                }
            }
        }
    }

    private void eliminateAbove(int pivotRow, int pivotCol) {
        for (int i = 0; i < pivotRow; i++) {
            double factor = parseDouble(getStringValue(i, pivotCol));
            if (Double.compare(factor, 0) != 0); {
                addRows(pivotRow, i, -factor);
            }
        }
    }

    // The method, "getRows()" is used interchangeably for "getCols()" as this matrix will always need
    // to be rectangular to have a defined determinant. Therefore, the dimensions will be equal.

    public String calculateDeterminant() {
        System.out.println("Matrix before 'Triangularization':");
        MatrixUtil.printStringMatrix(data);

        if (!isSquare()) {
            System.out.println("Determinant is not defined for non-square matrices.");
            throw new IllegalArgumentException("Determinant is not defined for non-square matrices.");
        }

        if (!isUpperTriangular() && ! isLowerTriangular()) {
            makeTriangular();
        }
        String deter = MatrixUtil.formatToTenDigits(multiplyDiameter() * getSign());
        System.out.println("Triangular matrix:");
        MatrixUtil.printStringMatrix(data);
        return deter;
    }

    public void makeTriangular() {
        for (int col = 0; col < data.length; col++) {
            sortCol(col);
            for (int row = data.length - 1; row > col; row--) {
                double element = parseDouble(data[row][col]);
                if (Double.compare(element, 0) == 0) {
                    continue;
                }

                double x = parseDouble(data[row][col]);
                double y = parseDouble(data[row - 1][col]);

                multiplyRowForDeterminant(row, -y / x);

                addRowsForDeterminant(row, row - 1);

                multiplyRowForDeterminant(row, -x / y);
            }
        }
    }

    private void sortCol(int paramCol) {
        for (int row = getRows() - 1; row >= paramCol; row--) {
            for (int col = getRows() - 1; col >= paramCol; col--) {
                double temp1 = parseDouble(data[row][paramCol]);
                double temp2 = parseDouble(data[col][paramCol]);

                if (Double.compare(Math.abs(temp1), Math.abs(temp2)) < 0) {
                    swapRowsForDeterminant(row, col);
                }
            }
        }
    }

    private void swapRowsForDeterminant(int row1, int row2) {
        setSign(getSign() * -1);
        swapRows(row1, row2);
    }

    public void multiplyRowForDeterminant(int row, double scalar) {
        if (Double.compare(scalar, 0) < 0) {
            setSign(getSign() * -1);
        }
        multiplyRow(row, scalar);
    }

    public void addRowsForDeterminant(int row1, int row2) {
        for (int col = 0; col < getCols(); col++) {
            double value1 = parseDouble(data[row1][col]);
            double value2 = parseDouble(data[row2][col]);

            data[row1][col] = String.valueOf(value1 + value2);
        }
    }

    public double multiplyDiameter() {
        double result = 1;
        for (int row = 0; row < getRows(); row++) {
            for (int col = 0; col < getRows(); col++) {
                if (row == col) {
                    result = result * parseDouble(data[row][col]);
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
                if (Double.compare(parseDouble(data[row][col]), 0) != 0) {
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
                if (Double.compare(parseDouble(data[row][col]), 0) != 0) {
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
        for (String[] row : this.data) { // Using "this.matrix" to refer to the Matrix class instance variable
            result.append(Arrays.toString(row)).append("\n");
        }
        return result.toString();
    }
}
