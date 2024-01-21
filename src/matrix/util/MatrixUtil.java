package matrix.util;

import matrix.gui.MatrixApp;
import matrix.model.MatrixCell;

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
        if (!hasRepeatingElements(decimalValue) && input.length() >= 10) {
            return formatToTenDigits(Double.parseDouble(input));
        }

        String result = getFraction(decimalValue);
        if (result.length() >= 12) {
            System.out.println("This is the result length: " + result.length());
            return formatToTenDigits(Double.parseDouble(input)); // The fraction is too large to be of any use.
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



    public static String convertFractionToDecimal(String expression) {
//        System.out.println("Convert Fraction to Decimal Expression: " + expression);
        if (expression.contains(".")) {
            return formatToTenDigits(Double.parseDouble(expression)); // It is already a decimal.
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

        return formatToTenDigits(result);
    }

    public static String formatToTenDigits(double x) {
        DecimalFormat df = new DecimalFormat("#.##########");
        df.setRoundingMode(RoundingMode.HALF_UP);
        return correctRoundingError(df.format(x));
    }

    public static String correctRoundingError(String number) {
        if (number.contains(".")) {
            int indexOfDot = number.indexOf('.');
            String decimalPart = number.substring(indexOfDot + 1);

            // Detect long sequences of 9s or 0s
            if (decimalPart.matches(".*99999999.*") || decimalPart.matches(".*00000000.*")) {
                // Find the position to round
                int roundPosition = decimalPart.indexOf('9') != -1 ? decimalPart.indexOf('9') : decimalPart.indexOf('0');

                // Round the number to the correct position
                double scalingFactor = Math.pow(10, roundPosition);
                return String.valueOf(correctNegativeZero(Math.round(Double.parseDouble(number) * scalingFactor) / scalingFactor));
            }
        }
        return String.valueOf(correctNegativeZero(Double.parseDouble(number)));
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

    public static boolean hasRepeatingElements(double decimal) {
        String decimalString = Double.toString(decimal);
        int threshold = 4;

        // Remove the decimal point if present
        if (decimalString.contains(".")) {
            decimalString = decimalString.replace(".", "");
        }

        int length = decimalString.length();

        // Check for repeating elements
        for (int i = 1; i <= length / 2; i++) {
            if (isRepeating(decimalString, length, i, threshold)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isRepeating(String decimalString, int length, int patternLength, int threshold) {
        for (int i = 0; i < length - patternLength * 2 + 1; i++) {
            String pattern = decimalString.substring(i, i + patternLength);
            boolean isRepeating = true;

            for (int j = i + patternLength; j < i + patternLength * 2; j += patternLength) {
                if (!pattern.equals(decimalString.substring(j, j + patternLength))) {
                    isRepeating = false;
                    break;
                }
            }

            if (isRepeating && patternLength >= threshold) {
                return true;
            }
        }

        return false;
    }

    private static double correctNegativeZero(double value) {
        return value == 0.0 ? 0.0 : value; // Converts -0.0 to 0.0
    }

    public static String[][] convertToDecimalMatrix(String[][] originalMatrix) {
        String[][] decimalMatrix = new String[originalMatrix.length][];
        for (int i = 0; i < originalMatrix.length; i++) {
            decimalMatrix[i] = new String[originalMatrix[i].length];
            for (int j = 0; j < originalMatrix[i].length; j++) {
                decimalMatrix[i][j] = convertFractionToDecimal(originalMatrix[i][j]);
            }
        }
        return decimalMatrix;
    }

    public static String[][] convertBackToOriginalForm(Double[][] matrix) {
        String[][] originalFormMatrix = new String[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            originalFormMatrix[i] = new String[matrix[i].length];
            for (int j = 0; j < matrix[i].length; j++) {
                // Convert each Double element to String
                if (MatrixApp.isFractionMode()) {
                    originalFormMatrix[i][j] = convertDecimalToFraction(matrix[i][j].toString());
                } else {
                    originalFormMatrix[i][j] = matrix[i][j].toString();
                }
            }
        }
        return originalFormMatrix;
    }


    public static Double[][] convertToDoubleMatrix(String[][] stringMatrix) {
        Double[][] doubleMatrix = new Double[stringMatrix.length][];
        for (int i = 0; i < stringMatrix.length; i++) {
            doubleMatrix[i] = new Double[stringMatrix[i].length];
            for (int j = 0; j < stringMatrix[i].length; j++) {
                try {
                    // Convert fraction to decimal before parsing
                    String decimalString = convertFractionToDecimal(stringMatrix[i][j]);
                    doubleMatrix[i][j] = Double.parseDouble(decimalString);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid number format at [" + i + "][" + j + "]: " + stringMatrix[i][j]);
                }
            }
        }
        return doubleMatrix;
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

        // Convert the matrix to Double form
        Double[][] doubleMatrix = convertToDoubleMatrix(matrix);
        System.out.println("Double Matrix:");
        printDoubleMatrix(doubleMatrix);

        // Convert back to fraction form (assuming the original mode is fraction)
        String[][] originalFormMatrix = convertBackToOriginalForm(doubleMatrix);
        System.out.println("Matrix back in Fraction Form:");
        printStringMatrix(originalFormMatrix);
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

