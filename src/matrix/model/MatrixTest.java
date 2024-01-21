package matrix.model;

import matrix.gui.MatrixApp;
import matrix.util.MatrixUtil;

import static java.lang.Double.parseDouble;
import static matrix.gui.MatrixApp.isFractionMode;
import static matrix.util.ExpressionEvaluator.evaluate;
import static matrix.util.MatrixUtil.*;

public class MatrixTest {

    private static String[][] data;
    private static int numRows;
    private static int numCols;
    private static int sign = 1;

    public static void swapRows(int row1, int row2) {
        if (isFractionMode()) {
            // Convert each element in both rows from decimal to fraction before swapping
            System.out.println("In fraction mode.");
            for (int i = 0; i < data[row1].length; i++) {
                String temp = convertDecimalToFraction(data[row1][i]);
                System.out.println("temp: " + temp);
                data[row1][i] = convertDecimalToFraction(data[row2][i]);
                System.out.println("data[row1][i]: " + data[row1][i]);
                data[row2][i] = temp;
                System.out.println("data[row2][i]: " + data[row2][i]);
            }
        } else {
            // Convert each element in both rows from fraction to decimal before swapping
            System.out.println("In decimal mode.");
            for (int i = 0; i < data[row1].length; i++) {
                String temp = convertFractionToDecimalString(data[row1][i]);
                data[row1][i] = convertFractionToDecimalString(data[row2][i]);
                data[row2][i] = temp;
            }
        }
    }

    public static void convertToTriangularForm() {
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

    private static int findPivotRow(int startRow, int col) {
        int pivotRow = -1;
        double maxAbsValue = 0.0;

        for (int row = startRow; row < numRows; row++) {
            double currentValue = Math.abs(convertFractionToDecimal(data[row][col]));

            if (currentValue > maxAbsValue) {
                maxAbsValue = currentValue;
                pivotRow = row;
            }
        }

        return pivotRow;
    }

    private static void eliminateBelow(int pivotRow, int col) {
        for (int row = pivotRow + 1; row < numRows; row++) {
            double factor = parseAndConvert(data[row][col]) / parseAndConvert(data[pivotRow][col]);
            if (factor == 0) {
                continue;
            }
            for (int c = col; c < numCols; c++) {
                double newValue = parseAndConvert(data[row][c]) - factor * parseAndConvert(data[pivotRow][c]);
                if (isFractionMode()) {
                    data[row][c] = convertDecimalToFraction(String.valueOf(evaluate(String.valueOf(newValue))));
                } else {
                    data[row][c] = String.valueOf((evaluate(String.valueOf(newValue))));
                }
            }
        }
    }

    private static double multiplyDiagonal() {
        double result = 1.0;
        for (int i = 0; i < Math.min(numRows, numCols); i++) {
            result *= (evaluate(data[i][i]));
        }
        return result * getSign();
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


    public static int getSign() {return sign;}
    public static void setSign(int sign) {MatrixTest.sign = sign;}
    public int getRows() {return data.length;}
    public int getCols() {return data.length > 0 ? data[0].length : 0;}

    private static double parseAndConvert(String value) {
        return convertFractionToDecimal(value);
    }

    public static void main(String[] args) {
        // Example matrix initialization
        MatrixApp.setFractionMode(true);
        data = new String[][]{
                {"1", "2", "3/4"},
                {"4", "5", "6"},
                {"7/3", "8", "3/6"}
        };
        numRows = data.length;
        numCols = data[0].length;

        convertToTriangularForm();

        // Print the matrix after elimination
        printMatrix();
        System.out.println();
        String determinant = String.valueOf(multiplyDiagonal());
        double determinantDecimal = (MatrixUtil.convertFractionToDecimal(determinant));
        String determinantString = MatrixUtil.convertDecimalToFraction(determinant);
        System.out.println("Determinant: " + determinant);
        System.out.println("determinantDecimal: " + determinantDecimal);
        System.out.println("determinantString: " + determinantString);
    }

    private static void printMatrix() {
        for (String[] row : data) {
            for (String value : row) {
                System.out.print(MatrixUtil.convertDecimalToFraction(value) + " ");
            }
            System.out.println();
        }
    }
}

