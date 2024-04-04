package matrix.util;

import org.junit.Test;

import static matrix.util.MatrixUtil.*;

public class MatrixUtilTest {

//    @Test
//    public void decimalMatrix() {
//        // Sample matrix with fractions
//        String[][] matrix = {
//                {"1/2", "2/3"},
//                {"3/4", "4/5"}
//        };
//
//        // Convert the matrix to decimal form
//        String[][] decimalMatrix = convertToDecimalMatrix(matrix);
//        System.out.println("Decimal Matrix:");
//        printStringMatrix(decimalMatrix);
//    }

    @Test
    public void correctFrac() {
        String input = "0.0161400629";
//        System.out.println("Init input: " + input);
        String frac = convertDecimalToFraction(input);
        System.out.println("Fraction representation: " + frac);

        String computedDecimal = convertFractionToDecimalString("1133/70198");
        System.out.println("Computed decimal: " + computedDecimal);
    }

    @Test
    public void correctDecimal() {
        String input = "1133/70198";
        System.out.println("Init input: " + input);
        String decimal = convertFractionToDecimalString(input);
        System.out.println("Decimal representation: " + decimal);
    }
}
