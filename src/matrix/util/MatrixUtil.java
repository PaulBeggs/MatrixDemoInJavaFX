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
        double decimalValue = ExpressionEvaluator.evaluate(input);

        if (decimalValue < 0) {
            return "-" + convertDecimalToFraction(String.valueOf(-decimalValue));
        }

        if (!MatrixApp.isForceFractions() && !hasRepeatingDecimals(decimalValue, 2, 2)) {
            return convertFractionToDecimalString(correctRoundingError(String.valueOf(decimalValue)));
        }

        String result = getFraction(decimalValue);
        if (result.length() >= 16) {
            System.out.println("This is the result length: " + result.length());
            return correctRoundingError(input); // The fraction is too large to be displayed.
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
        String parsedExpression = String.valueOf(ExpressionEvaluator.evaluate(expression));
        if (parsedExpression.contains(".")) {
            return correctRoundingError(parsedExpression); // It is already a decimal.
        }

        if (!parsedExpression.contains("/")) {
            return parsedExpression; // Expression is just an integer.
        }

        String[] parts = parsedExpression.split("/");
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
            if (decimalPart.matches(".*(999999|000000).*")) {
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
//                System.out.println("numerator: " + numerator);
//                System.out.println("Denominator: " + denominator);
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

    public static boolean hasRepeatingDecimals(double value, int minPatternLength, int minRepetitions) {
        String decimalString = String.format("%.15f", value);
        String decimalPart = decimalString.substring(decimalString.indexOf('.') + 1);

        for (int i = 0; i <= decimalPart.length() - minPatternLength; i++) {
            for (int len = minPatternLength; len <= (decimalPart.length() - i) / minRepetitions; len++) {
                String pattern = decimalPart.substring(i, i + len);
                boolean isRepeating = true;

                for (int j = i + len; j <= decimalPart.length() - len; j += len) {
                    if (!decimalPart.startsWith(pattern, j)) {
                        isRepeating = false;
                        break;
                    }
                }

                if (isRepeating) {
                    return true;
                }
            }
        }
        return false;
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
                originalFormMatrix[i][j] = MatrixApp.isFractionMode() ? convertDecimalToFraction(matrix[i][j])
                        : convertFractionToDecimalString(matrix[i][j]);
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

