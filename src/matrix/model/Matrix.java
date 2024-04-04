package matrix.model;

import matrix.fileManaging.MatrixFileHandler;
import matrix.gui.MatrixApp;
import matrix.util.ErrorsAndSyntax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static matrix.gui.MatrixApp.isFractionMode;
import static matrix.util.ExpressionEvaluator.evaluate;
import static matrix.util.MatrixUtil.*;

public class Matrix implements MatrixOperations {
    private String[][] data;
    private int numCols, numRows, sign, counter = 1, step = 1;
    private final List<String> operationsSummary = new ArrayList<>();
    private boolean signChange = false;

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
                    // System.println("Current value: " + currentValue + " from: " + Arrays.deepToString(data) + " at " + row + ", " + col);
                    // System.println();
                    double multipliedValue = currentValue * scalar;  // Multiply by the scalar
                    // System.println("multiplied Value: " + multipliedValue);

                    // Convert the result to the appropriate format
                    String result = isFractionMode() ?
                            convertDecimalToFraction(String.valueOf(multipliedValue)) :
                            convertFractionToDecimalString(String.valueOf(multipliedValue));

                    // System.println("result: " + result);

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

                    // System.println("Eliminate below at: " + "[" + targetRow + "] " + "[" + col + "] " + "Entry: \n" + result);

                    data[targetRow][col] = result; // Assign the result to the target row
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Trouble parsing the input at column " + col + ".");
                }
            }
        } else {
            System.out.println("Invalid row indices: " + sourceRow + ", " + targetRow);
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
                                        MATRIX MULTIPLICATION
    ---------------------------------------------------------------------------------------------
     */



    /*
    ---------------------------------------------------------------------------------------------
                                              INVERTING
    ---------------------------------------------------------------------------------------------
     */

    // Need to add a boolean check for if the matrix is dependent. For example, if it has a row that is all 0s,
    // then it is dependent, and thus, it does not have an inverse.
    // Use RREF to definitively show dependence.
    public void convertToInvertedForm() {
        if (isSquare() && !isIndependent()) {
            // Save the original mode choice directly based on the current mode
            boolean ogChoice = MatrixApp.isFractionMode();

            // Switch to decimal mode for the inversion process if currently in fraction mode
            if (ogChoice) {
                MatrixApp.setFractionMode(false);
            }

            // Perform the matrix inversion operations
            Matrix originalMatrix = MatrixSingleton.getInstance().copy();
            Matrix augmentedMatrix = augmentMatrixWithIdentity(originalMatrix);

            augmentedMatrix.convertToReducedEchelonForm();
            // System.out.println("Augmented matrix: " + augmentedMatrix);

            String[][] inverseMatrix = extractRightSide(augmentedMatrix);
            // System.out.println("Inverted matrix: \n" + Arrays.deepToString(inverseMatrix));

            // Restore the original mode based on the saved choice
            MatrixApp.setFractionMode(ogChoice);

            // Convert the inverted matrix back to its original form (fraction or decimal)
            // and store it, ensuring the conversion respects the user's original mode preference
            System.out.println("Before converting to og form: \n" + Arrays.deepToString(inverseMatrix));
            MatrixFileHandler.matrices.put("Inverse", convertBackToOriginalForm(inverseMatrix));
        } else {
            System.out.println("Matrix is either not square or is dependent.");
        }
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
        int numCols = matrix.getCols() / 2;
        String[][] rightSide = new String[numRows][numCols];

        // Add the proper entry based on mode to the matrix
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                rightSide[i][j] = isFractionMode() ?
                        convertDecimalToFraction(matrix.getValue(i,j + numCols)) :
                        convertFractionToDecimalString(matrix.getValue(i,j + numCols));
            }
        }
        // Return calculated right side
        return rightSide;
    }


    // Very similar to the triangularize method, except this is for n x m dimensional matrices
    // This also normalizes the pivot elements to "1"
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
        // System.println("REF: \n" + MatrixFileHandler.matrices.get("REF"));
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
        // System.println("RREF: \n" + MatrixFileHandler.matrices.get("RREF"));
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
                double result = evaluate(data[row][c]) - factor * evaluate(data[pivotRow][c]);
                data[row][c] = String.valueOf((result));
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

    public boolean isIndependent() {
        boolean independent = false;
        if (getCols() > getRows()) {
            independent = true;
        } else if (isSquare()) {

        }
        return independent;
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
            operationsSummary.add("#" + step++ + " It is not. Swapping rows if necessary... \n");
            convertToTriangularForm();
        } else {
            operationsSummary.add("#" + step++ + " It is! Returning determinant of the triangular matrix... \n");
        }
        String deter = correctRoundingError(String.valueOf(multiplyDiagonal()));
        System.out.println("Triangular matrix:");
        MatrixFileHandler.matrices.put("Triangular", convertBackToOriginalForm(data));
        printStringMatrix(data);
        MatrixSingleton.setTriangularInstance(copy());
        System.out.println("This is the determinant: " + deter);
        printMatrices();
        System.out.println(operationsSummary);
        deter = isFractionMode()
                ? convertDecimalToFraction(deter)
                : convertFractionToDecimalString(deter);
        operationsSummary.add("#" + step++ + " Final determinant value: " + deter + "\n");
        return deter;
    }

    public void convertToTriangularForm() {
        int currentRow = 0;
        int currentCol = 0;
        String stringSign = "+";

        while (currentRow < numRows && currentCol < numCols) {

            // Find the pivot element in the current column
            int pivotRow = findPivotRow(currentRow, currentCol);

            if (pivotRow != -1) {
                // Swap rows if necessary
                if (pivotRow != currentRow) {
                    swapRows(currentRow, pivotRow);
                    // update "setSign" method to multiply the sign by this value, do not "set" this value as a negative number as for each
                    // row swap, the sign should be inverted.
                    setSign(-1);

                    if (getSign() == 1) {
                        stringSign = "+";
                    } else if (getSign() == -1) {
                        stringSign = "-";
                    }
                    operationsSummary.add("#" + step++ + " Swapping rows: " + (currentRow + 1) + ", " + (pivotRow + 1) + "\n" +
                            "Sign after swapping: (" + stringSign + ")" + "\n");
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
        StringBuilder currentProduct = new StringBuilder();

        for (int i = 0; i < Math.min(numRows, numCols); i++) {
            double diagonalElement = evaluate(data[i][i]);
            result *= diagonalElement;

            String diagonalElementString = isFractionMode() ? convertDecimalToFraction(String.valueOf(diagonalElement))
                    : convertFractionToDecimalString(String.valueOf(diagonalElement));

            if (i > 0) {
                currentProduct.append(" * ");
            }
            currentProduct.append("(").append(diagonalElementString).append(")");
        }

        String resultString = isFractionMode() ? convertDecimalToFraction(String.valueOf(result))
                : convertFractionToDecimalString(String.valueOf(result));

        operationsSummary.add("#" + step++ + " Multiplying diagonal elements to find determinant: \n" + currentProduct + "\n");
        operationsSummary.add("#" + step++ + " Determinant (before applying sign): " + resultString + "\n");
        String stringSign = "";
        if (getSign() == 1) {
            stringSign = "+";
        } else if (getSign() == -1) {
            stringSign = "-";
        }
        operationsSummary.add("#" + step++ + " Multiplying the result (" + resultString + ") by the sign (" + stringSign + ") to get the final determinant value..." + "\n");

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
        operationsSummary.add("#" + step++ + " Checking to see if the matrix is already triangular..." + "\n");
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

    /*
    --------------------------------------------------------------------------------------------
                                              UTILITY
    --------------------------------------------------------------------------------------------
     */

    public String getValue(int row, int col) {return this.data[row][col];}

    public void setValue(int row, int col, String value) {
        if (isValidRow(row) && isValidColumn(col)) {
//            System.out.println("Data before parsing in setValue: \n" + "Row: " + row + ", Col: " + col + ", value: " + value);
            if (Objects.equals(value, "")) {
                this.data[row][col] = "0";
            } else {
                this.data[row][col] = isFractionMode()
                        ? convertDecimalToFraction(value)
                        : convertFractionToDecimalString(value);
            }
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

        System.out.println("Putting transpose matrix: \n" + convertBackToOriginalForm(data));
        MatrixFileHandler.matrices.put("Transpose", convertBackToOriginalForm(data));
    }

    public void setToIdentity() {
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                data[i][j] = (i == j) ? "1" : "0";
            }
        }
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
}
