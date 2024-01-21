package matrix.model;

import matrix.gui.MatrixApp;
import matrix.util.MatrixUtil;

import java.util.Arrays;

import static java.lang.Double.parseDouble;
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

    public Matrix(String[][] matrixData) {
        if (matrixData == null || matrixData.length == 0 || matrixData[0].length == 0) {
            throw new IllegalArgumentException("Matrix data cannot be null or empty.");
        }
        this.numRows = matrixData.length;
        this.numCols = matrixData[0].length;
        this.data = new String[numRows][numCols];
        for (int i = 0; i < numRows; i++) {
            System.arraycopy(matrixData[i], 0, this.data[i], 0, numCols);
        }
    }

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
                    String temp = convertFractionToDecimalString(data[row1][i]);
                    data[row1][i] = convertFractionToDecimalString(data[row2][i]);
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
                            convertFractionToDecimalString(String.valueOf(multipliedValue));

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
                            convertFractionToDecimalString(String.valueOf(sum));

                    data[targetRow][col] = result; // Assign the result to the target row
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Trouble parsing the input at column " + col + ".");
                }
            }
        } else {
            System.out.println("Invalid row indices: " + sourceRow + ", " + targetRow);
        }
    }

    public String getValue(int row, int col) {return this.data[row][col];}

    public void setValue(int row, int col, String value) {
        if (isValidRow(row) && isValidColumn(col)) {
            if (isFractionMode()) {
                this.data[row][col] = convertDecimalToFraction(value);
            } else {
                this.data[row][col] = convertFractionToDecimalString(value);
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
                String originalValue = this.getValue(i, j);
                String copiedValue;

                if (MatrixApp.isFractionMode()) {
                    // Convert to fraction if in fraction mode
                    copiedValue = convertFractionToDecimalString(originalValue);
                } else {
                    // Convert to decimal if not in fraction mode
                    copiedValue = convertDecimalToFraction(originalValue);
                }

                copiedMatrix.setValue(i, j, copiedValue);
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

//    public void convertToEchelonForm() {
//        int numRows = getRows();
//        int numCols = getCols();
//
//        for (int p = 0; p < Math.min(numRows, numCols); p++) {
//            int maxRow = findPivotRow(p);
//            if (maxRow != p) {
//                swapRows(p, maxRow);
//            }
//
//            normalizePivotRow(p);
//            eliminateBelow(p);
//        }
//    }


    public void convertToReducedEchelonForm() {
        // First, convert to echelon form
//        convertToEchelonForm();

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
            if (parseDouble(getValue(row, j)) != 0) {
                return j; // Pivot column found
            }
        }
        return -1; // No pivot in this row
    }

    private int findPivotRow(int startRow, int col) {
        int pivotRow = -1;
        double maxAbsValue = 0.0;
        double currentValue = 0;
        for (int row = startRow; row < numRows; row++) {
            try {
                currentValue = Math.abs(evaluate(data[row][col]));
            } catch (NumberFormatException e) {
                System.out.println("Value at [" + row + "][" + col + "]: " + data[row][col]);
                throw new IllegalArgumentException(e);
            }

            if (currentValue > maxAbsValue) {
                maxAbsValue = currentValue;
                pivotRow = row;
            }
        }

        return pivotRow;
    }

    private void normalizePivotRow(int p) {
        double pivotValue = parseDouble(getValue(p, p));
        if (Double.compare(pivotValue, 0) != 0) {
            multiplyRow(p, 1.0 / pivotValue);

        }
    }

    private void eliminateBelow(int pivotRow, int col) {
        for (int row = pivotRow + 1; row < numRows; row++) {
            double factor = evaluate(data[row][col]) / evaluate(data[pivotRow][col]);
            if (factor == 0) {
                continue;
            }
            for (int c = col; c < numCols; c++) {
                double newValue = evaluate(data[row][c]) - factor * evaluate(data[pivotRow][c]);
                data[row][c] = String.valueOf((newValue));
            }
        }
    }

    private void eliminateAbove(int pivotRow, int pivotCol) {
        for (int i = 0; i < pivotRow; i++) {
            double factor = evaluate(getValue(i, pivotCol));
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
            convertToTriangularForm();
        }
        String deter = MatrixUtil.correctRoundingError(String.valueOf(multiplyDiagonal()));
        System.out.println("Triangular matrix:");
        MatrixUtil.printStringMatrix(data);
        MatrixSingleton.setTriangularInstance(copy());
        System.out.println("This is the determinant: " + deter);
        if (MatrixApp.isFractionMode()) {
            return MatrixUtil.convertDecimalToFraction(deter);
        } else {
            return MatrixUtil.convertFractionToDecimalString(deter);
        }
    }

    public void convertToTriangularForm() {
        int currentRow = 0;
        int currentCol = 0;

        while (currentRow < numRows && currentCol < numCols) {

            // Find the pivot element in the current column
            int pivotRow = findPivotRow(currentRow, currentCol);

            if (pivotRow != -1) {
                // Swap rows if necessary
                if (pivotRow != currentRow) {
                    swapRows(currentRow, pivotRow);
                    setSign(-1);
                }
                // Eliminate elements below the pivot
                eliminateBelow(currentRow, currentCol);

                // Move to the next row and column
                currentRow++;
                currentCol++;
            }
        }
    }


    private double multiplyDiagonal() {
        double result = 1.0;
        for (int i = 0; i < Math.min(numRows, numCols); i++) {
            result *= evaluate(data[i][i]);
        }
        return result * getSign();
    }

    private boolean isUpperTriangular() {
        if (getRows() < 2) {
            return false;
        }
        for (int row = 0; row < getRows(); row++) {
            for (int col = 0; col < row; col++) {
                if (Double.compare((evaluate(data[row][col])), 0) != 0) {
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
                if (Double.compare(evaluate(data[row][col]), 0) != 0) {
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
