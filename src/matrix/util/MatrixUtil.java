package matrix.util;

import matrix.gui.MatrixApp;
import matrix.model.Matrix;
import matrix.model.MatrixCell;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class MatrixUtil {

    public static String matrixCellsToString(MatrixCell[][] matrixCells) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < matrixCells.length; i++) {
            for (int j = 0; j < matrixCells[i].length; j++) {
                String cellValue = matrixCells[i][j].getTextField().getText();
                sb.append(String.format("(%d,%d): %s ", i, j, cellValue));
            }
            sb.append("\n"); // New line at the end of each row
        }
        return sb.toString();
    }

    public static String convertDecimalToFraction(String input) {
        if (input.contains("/")) {
            return simplifyFraction(input); // It is already a fraction.
        }
        double decimalValue;
        try {
            decimalValue = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            return String.valueOf(0);
        }

        if (decimalValue < 0) {
            return "-" + convertDecimalToFraction(String.valueOf(-decimalValue));
        }

        String result = getFraction(decimalValue);
        if (result.length() >= 12) {
            System.out.println("This is the result length: " + result.length());
            return correctRoundingError(input); // The fraction is too large to be of any use.
        }

        return simplifyFraction(result);
    }

    private static String getFraction(double decimalValue) {
        double tolerance = 1.0E-9;
        double numeratorCurrent = 1;
        double numeratorPrevious = 0;
        double denominatorCurrent = 0;
        double denominatorPrevious = 1;
        double intermediateValue = decimalValue;

        // Loop control to avoid infinite loops
        int maxIterations = 10000;

        for (int i = 0; i < maxIterations; i++) {
            double wholePart = Math.floor(intermediateValue);
            double tempNumerator = numeratorCurrent;
            double tempDenominator = denominatorCurrent;

            numeratorCurrent = wholePart * numeratorCurrent + numeratorPrevious;
            denominatorCurrent = wholePart * denominatorCurrent + denominatorPrevious;

            numeratorPrevious = tempNumerator;
            denominatorPrevious = tempDenominator;

            intermediateValue = 1 / (intermediateValue - wholePart);

            if (Math.abs(decimalValue - numeratorCurrent / denominatorCurrent) < tolerance) {
                break;
            }
        }

        return (int) numeratorCurrent + "/" + (int) denominatorCurrent;
    }



    public static String convertFractionToDecimalString(String expression) {
//        System.out.println("Convert Fraction to Decimal Expression: " + expression);
        if (expression.contains(".")) {
            return correctRoundingError(expression); // It is already a decimal.
        }

        if (!expression.contains("/")) {
            return expression; // Expression is just an integer.
        }

        String[] parts = expression.split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid fraction format"); // Catch all for any input that is not fractional (ex., x/y/e)
        }
        Double numerator = Double.valueOf(parts[0]);
        Double denominator = Double.valueOf(parts[1]);

        double result = numerator / denominator;

        return correctRoundingError(String.valueOf(result));
    }

    public static double convertFractionToDecimal(String decimal) {
        return Double.parseDouble(convertFractionToDecimalString(decimal));
    }

    public static String correctRoundingError(String number) {
        if (number.contains(".")) {
            int indexOfDot = number.indexOf('.');
            String decimalPart = number.substring(indexOfDot + 1);

            // Detect long sequences of 9s or 0s
            if (decimalPart.matches(".*(9999999|0000000).*")) {
                // Round the number using BigDecimal to avoid floating-point issues
                BigDecimal num = new BigDecimal(number);
                int scale = decimalPart.indexOf('9') != -1 ? decimalPart.indexOf('9') : decimalPart.indexOf('0');
                num = num.setScale(scale, RoundingMode.HALF_UP);

                return correctNegativeZero(num.stripTrailingZeros().toPlainString());
            }
        }

        // Format the number to 10 decimal places if no long sequences are found
        DecimalFormat df = new DecimalFormat("#.##########");
        df.setRoundingMode(RoundingMode.HALF_UP);

        return correctNegativeZero(df.format(Double.parseDouble(number)));
    }

    private static String correctNegativeZero(String value) {
        return value.equals("-0.0") ? "0.0" : value;
    }

    public static String simplifyFraction(String fraction) {
        // Split the fraction into numerator and denominator
        String[] parts = fraction.split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid fraction format");
        }

        try {
            int numerator = Integer.parseInt(parts[0]);
            int denominator = Integer.parseInt(parts[1]);

            // Simplify the fraction
            int gcd = gcd(numerator, denominator);
            numerator /= gcd;
            denominator /= gcd;

            // Check if the result is an integer
            if (denominator == 1) {
                return String.valueOf(numerator); // Simplified to an integer
            } else {
                return numerator + "/" + denominator; // Fraction in the simplest form
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format in fraction");
        }
    }

    // Helper method to find the greatest common divisor
    private static int gcd(int a, int b) {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }


    public static String[][] convertToDecimalMatrix(String[][] originalMatrix) {
        String[][] decimalMatrix = new String[originalMatrix.length][];
        for (int i = 0; i < originalMatrix.length; i++) {
            decimalMatrix[i] = new String[originalMatrix[i].length];
            for (int j = 0; j < originalMatrix[i].length; j++) {
                decimalMatrix[i][j] = convertFractionToDecimalString(originalMatrix[i][j]);
            }
        }
        return decimalMatrix;
    }

    public static Matrix convertBackToOriginalForm(String[][] matrix) {
        String[][] originalFormMatrix = new String[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            originalFormMatrix[i] = new String[matrix[i].length];
            for (int j = 0; j < matrix[i].length; j++) {
                originalFormMatrix[i][j] = MatrixApp.isFractionMode() ? convertDecimalToFraction(matrix[i][j]) : matrix[i][j]; // Assuming convertDecimalToFraction is also static
            }
        }
        return new Matrix(originalFormMatrix);
    }



    public static void main(String[] args) {
        // Sample matrix with fractions
        String[][] matrix = {
                {"1/2", "2/3"},
                {"3/4", "4/5"}
        };

        // Convert the matrix to decimal form
        String[][] decimalMatrix = convertToDecimalMatrix(matrix);
        System.out.println("Decimal Matrix:");
        printStringMatrix(decimalMatrix);

//        // Convert the matrix to Double form
//        Double[][] doubleMatrix = convertToDoubleMatrix(matrix);
//        System.out.println("Double Matrix:");
//        printDoubleMatrix(doubleMatrix);
    }

    public static void printStringMatrix(String[][] matrix) {
        for (String[] row : matrix) {
            for (String element : row) {
                System.out.print(element + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void printDoubleMatrix(Double[][] matrix) {
        for (Double[] row : matrix) {
            for (Double element : row) {
                System.out.print(element + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}

