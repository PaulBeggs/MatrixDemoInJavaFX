package matrix.operators;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

public class UserInputToOutputOperands {

    public static String convertDecimalToFraction(BigDecimal decimal) {
        final int DEFAULT_MAX_DENOMINATOR = 1000000; // Max denominator for precision
        BigInteger numerator = decimal.movePointRight(decimal.scale()).toBigInteger();
        BigInteger denominator = BigInteger.valueOf(10).pow(decimal.scale());

        BigInteger gcd = numerator.gcd(denominator);
        numerator = numerator.divide(gcd);
        denominator = denominator.divide(gcd);

        // If the denominator is too large, approximate the fraction
        if (denominator.compareTo(BigInteger.valueOf(DEFAULT_MAX_DENOMINATOR)) > 0) {
            BigDecimal approxDecimal = decimal.setScale(2, RoundingMode.HALF_UP);
            return convertDecimalToFraction(approxDecimal);
        }

        return numerator + "/" + denominator;
    }

    public static String sqrtWithMode(String input, boolean fractionMode) {
        if (input.startsWith("/sqrt(") && input.endsWith(")")) {
            String insideParenthesis = input.substring(6, input.length() - 1);

            // Check if the input is a fraction
            if (insideParenthesis.contains("/")) {
                // If in fraction mode, return the square root symbol with the fraction
                if (fractionMode) {
                    return "√(" + insideParenthesis + ")";
                } else {
                    // Convert to decimal and compute square root
                    BigDecimal value = convertFractionToDecimal(insideParenthesis);
                    BigDecimal sqrtValue = value.sqrt(MathContext.DECIMAL128);
                    return sqrtValue.toString();
                }
            } else {
                // Handle non-fraction inputs (e.g., decimals)
                BigDecimal decimalValue = new BigDecimal(insideParenthesis);
                BigDecimal sqrtValue = decimalValue.sqrt(MathContext.DECIMAL128);

                if (fractionMode) {
                    // This converts the sqrt value from a decimal and into a fraction
                    return convertDecimalToFraction(sqrtValue);
                } else {
                    return sqrtValue.toString();
                }
            }
        }
        throw new IllegalArgumentException("Invalid square root format");
    }

    private static BigDecimal convertFractionToDecimal(String fraction) {
        String[] parts = fraction.split("/");
        BigDecimal numerator = new BigDecimal(parts[0]);
        BigDecimal denominator = new BigDecimal(parts[1]);
        return numerator.divide(denominator, MathContext.DECIMAL128);
    }

//    public static void main(String[] args) {
//        BigDecimal x = new BigDecimal("0");
//        if (x.equals(BigDecimal.ZERO)) {
//            System.out.println("0");
//        } else {
//            System.out.println(convertDecimalToFraction(x));
//        }
//    }

    public static void main(String[] args) {
        BigDecimal x = new BigDecimal("4234.3");
        String input = "/sqrt("+x+")";

        boolean fractionMode = true; // User preference
        String result = sqrtWithMode(input, fractionMode);
        System.out.println(result); // Outputs √(fraction) or decimal based on fractionMode

        String fraction = convertDecimalToFraction(x);
        System.out.println("Fraction equivalent of "+x+" is: " + fraction);
    }
}

