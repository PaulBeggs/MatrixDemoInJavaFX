package matrix.model;

import matrix.fileManaging.MatrixFileHandler;
import matrix.util.ErrorsAndSyntax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static matrix.gui.MatrixApp.isFractionMode;
import static matrix.util.ExpressionEvaluator.evaluate;
import static matrix.util.MatrixUtil.*;

public class Matrix implements MatrixOperations {
    private String[][] data;
    private int numCols, numRows, sign, counter = 1, step = 1;
    private final List<String> operationsSummary = new ArrayList<>();

    /*
    ---------------------------------------------------------------------------------------------
                                             CONSTRUCTOR
    ---------------------------------------------------------------------------------------------
     */

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


    /*
    ---------------------------------------------------------------------------------------------
                                      ELEMENTARY ROW OPERATIONS
    ---------------------------------------------------------------------------------------------
     */


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
            ErrorsAndSyntax.showErrorPopup("Invalid row indices for swapping: " + row1 + ", " + row2);
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
                    ErrorsAndSyntax.showErrorPopup("Problem parsing at column " + col + " in row " + row);
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
//            System.out.println("Data before parsing in setValue: \n" + "Row: " + row + ", Col: " + col + ", value: " + value);
            this.data[row][col] = isFractionMode()
                    ? convertDecimalToFraction(String.valueOf(value))
                    : convertFractionToDecimalString(value);
//            System.out.println("Data from setValue: \n" + data[row][col]);
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

                copiedValue = isFractionMode()
                        ? convertDecimalToFraction(originalValue)
                        : convertFractionToDecimalString(originalValue);

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

    public void setToIdentity() {
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                data[i][j] = (i == j) ? "1" : "0";
            }
        }
    }

    /*
    ---------------------------------------------------------------------------------------------
                                         GETTERS & VERIFIERS
    ---------------------------------------------------------------------------------------------
     */

    // Note, there are no setters because all the instance variables are final.
    // (It makes keeping track of variable values much easier.)

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
    public List<String> getOperationSummary() {return operationsSummary;}
    public int getSign() {return sign;}
    public void setSign(int sign) {this.sign = sign;}
    public int getTotalSteps() {return counter;}


    /*
    ---------------------------------------------------------------------------------------------
                                              INVERTING
    ---------------------------------------------------------------------------------------------
     */

    public void handleInvertingFunctionality() {
        Matrix originalMatrix = MatrixSingleton.getInstance().copy();
        Matrix augmentedMatrix = augmentMatrixWithIdentity(originalMatrix);

        augmentedMatrix.convertToReducedEchelonForm(); // Assuming this method exists and works for the augmented matrix

        String[][] inverseMatrix = extractRightSide(augmentedMatrix);
        MatrixFileHandler.matrices.put("Inverse", convertBackToOriginalForm(inverseMatrix));
    }

    private Matrix augmentMatrixWithIdentity(Matrix matrix) {
        int numRows = matrix.getRows();
        int numCols = matrix.getCols();
        Matrix augmentedMatrix = new Matrix(numRows, numCols * 2);

        // Copy original matrix and append identity matrix
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                augmentedMatrix.setValue(i, j, matrix.getValue(i, j));
                augmentedMatrix.setValue(i, j + numCols, (i == j) ? "1" : "0");
            }
        }

        return augmentedMatrix;
    }

    private String[][] extractRightSide(Matrix matrix) {
        int numRows = matrix.getRows();
        int numCols = matrix.getCols() / 2; // Assuming augmented matrix has double the columns
        String[][] rightSide = new String[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                rightSide[i][j] = matrix.getValue(i, j + numCols);
            }
        }

        return rightSide;
    }


    // Very similar to the triangularize method, except this is for n x m dimensional matrices.
    // This also normalizes the pivot elements to "1".
    public void convertToEchelonForm() {
        int currentRow = 0;
        int currentCol = 0;
        while (currentRow < getRows() && currentCol < getCols()) {
            int pivotRow = findPivotRow(currentRow, currentCol);

            if (pivotRow != -1) {
                // Swap rows if necessary
                if (pivotRow != currentRow) {
                    swapRows(currentRow, pivotRow);
                    setSign(-1);
                }

                // Normalize the pivot row (make the leading coefficient 1)
                normalizeRow(currentRow, currentCol);

                // Eliminate elements below the pivot
                eliminateBelow(currentRow, currentCol);

                // Move to the next row
                currentRow++;
            } else {
                // Move to the next column when no more pivots are found in the current column
                currentCol++;
            }
        }
        MatrixFileHandler.matrices.put("REF", convertBackToOriginalForm(data));
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
        MatrixFileHandler.matrices.put("RREF", convertBackToOriginalForm(data));
    }

    private int findPivotColumn(int row) {
        for (int j = 0; j < getCols(); j++) {
            if (evaluate(getValue(row, j)) != 0) {
                return j; // Pivot column found
            }
        }
        return -1; // No pivot in this row
    }

    private int findPivotRow(int startRow, int col) {
        int pivotRow = -1;
        double maxAbsValue = 0.0;
        double currentValue;
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

    private void normalizeRow(int row, int col) {
        double pivotValue = evaluate(data[row][col]);
        if (pivotValue != 0) {
            for (int c = col; c < numCols; c++) {
                data[row][c] = String.valueOf(evaluate(data[row][c]) / pivotValue);
            }
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

                String result = isFractionMode() ?
                        convertDecimalToFraction(String.valueOf(newValue)) :
                        convertFractionToDecimalString(String.valueOf(newValue));
//                System.out.println("Putting " + result + " at row: " + row + ", col: " + col);

                data[row][c] = (result);
            }
            String key = "Matrix #" + counter++;
            MatrixFileHandler.matrices.put(key, convertBackToOriginalForm(data));
        }
    }

    private void eliminateAbove(int pivotRow, int pivotCol) {
        for (int i = 0; i < pivotRow; i++) {
            double factor = evaluate(getValue(i, pivotCol));
            if (Double.compare(factor, 0) != 0) {
                addRows(pivotRow, i, -factor);
            }
        }
    }

    /*
    ---------------------------------------------------------------------------------------------
                                             DETERMINANT
    ---------------------------------------------------------------------------------------------
     */

    public String calculateDeterminant() {
        operationsSummary.clear();
        sign = 1;
        step = 1;
        System.out.println("Matrix before 'Triangularization':");
        printStringMatrix(data);
        MatrixFileHandler.matrices.put("initial", convertBackToOriginalForm(data));
        if (!isSquare()) {
            ErrorsAndSyntax.showErrorPopup("Determinant is not defined for non-square matrices.");
            throw new IllegalArgumentException("Determinant is not defined for non-square matrices.");
        }

        if (!isUpperTriangular() && ! isLowerTriangular()) {
            operationsSummary.add("#" + step++ + ", It is not. Moving on to swapping rows if necessary... \n");
            convertToTriangularForm();
        }
        String deter = correctRoundingError(String.valueOf(multiplyDiagonal()));
        System.out.println("Triangular matrix:");
        printStringMatrix(data);
        MatrixSingleton.setTriangularInstance(copy());
        System.out.println("This is the determinant: " + deter);
        printMatrices();
        System.out.println(operationsSummary);
        deter = isFractionMode()
                ? convertDecimalToFraction(deter)
                : convertFractionToDecimalString(deter);
        operationsSummary.add("#" + step++ + ", Final determinant value: " + deter + "\n");
        return deter;
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
                    operationsSummary.add("#" + step++ + ", Swapping rows: " + (currentRow + 1) + ", " + (pivotRow + 1) + "\n");
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
        String resultDupe = ""; // Declare resultDupe outside the for loop

        for (int i = 0; i < Math.min(numRows, numCols); i++) {
            result *= evaluate(data[i][i]);
            resultDupe = String.valueOf(result);
            String evalDupe = String.valueOf(data[i][i]);
            resultDupe = isFractionMode() ? convertDecimalToFraction(resultDupe) : convertFractionToDecimalString(resultDupe);
            evalDupe = isFractionMode() ? convertDecimalToFraction(evalDupe) : convertFractionToDecimalString(evalDupe);
            operationsSummary.add("#" + step++ + ", Multiplying diagonal elements to find determinant: " + resultDupe + " * " + evalDupe + "\n");
        }

        // Use resultDupe outside the for loop
        operationsSummary.add("#" + step++ + ", Determinant (before applying sign): " + resultDupe + "\n");
        operationsSummary.add("#" + step++ + ", Multiplying the result ("+resultDupe+") by the sign ("+getSign()+") to get the final determinant" + "\n");
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
        operationsSummary.add("#" + step++ + ", Checking to see if the matrix is already triangular..." + "\n");
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


    public void printMatrices() {
        System.out.println("initial matrix: \n" + MatrixFileHandler.matrices.get("initial"));
        for (int i = 1; i <= counter; i++) {
            String key = "Matrix #" + i;
            Matrix matrix = MatrixFileHandler.matrices.get(key);
            if (matrix != null) {
                System.out.println(key + ":");
                System.out.println(matrix);
                System.out.println(); // Adding a blank line for readability
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (String[] row : this.data) { // Using "this.matrix" to refer to the Matrix class instance variable
            result.append(Arrays.toString(row)).append("\n");
        }
        return result.toString();
    }

    public static void main(String[] args) {
        // Create a 3x4 matrix and populate it with some values
        Matrix matrix = new Matrix(3, 4);
        matrix.setValue(0, 0, "1");
        matrix.setValue(0, 1, "2");
        matrix.setValue(0, 2, "3");
        matrix.setValue(0, 3, "4");
        matrix.setValue(1, 0, "5");
        matrix.setValue(1, 1, "6");
        matrix.setValue(1, 2, "7");
        matrix.setValue(1, 3, "sqrt(5)");
        matrix.setValue(2, 0, "9");
        matrix.setValue(2, 1, "10/3");
        matrix.setValue(2, 2, "11");
        matrix.setValue(2, 3, "12");
        System.out.println("Init matrix: \n" +  matrix);
//        Matrix matrix = new Matrix(1,2);
//        matrix.setValue(0, 0, "sqrt(5)");

        // Convert to echelon form
        matrix.convertToEchelonForm();

        // Retrieve and print the echelon form matrix
        System.out.println(MatrixFileHandler.matrices.get("REF"));
    }
}
