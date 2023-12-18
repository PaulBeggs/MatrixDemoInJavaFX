package matrix.operators;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class DecimalToFraction {

    static private String convertDecimalToFraction(BigDecimal x) {
        if (x.compareTo(BigDecimal.ZERO) < 0) {
            return "-" + convertDecimalToFraction(x.negate());
        }
        BigDecimal tolerance = new BigDecimal("1.0E-8", MathContext.DECIMAL128);
        BigDecimal remainder = x;
        BigDecimal numeratorCurrent = BigDecimal.ONE;
        BigDecimal numeratorPrevious = BigDecimal.ZERO;
        BigDecimal denominatorCurrent = BigDecimal.ZERO;
        BigDecimal denominatorPrevious = BigDecimal.ONE;

        while (x.subtract(numeratorCurrent.divide(denominatorCurrent.compareTo(BigDecimal.ZERO) != 0 ? denominatorCurrent : BigDecimal.ONE,
                MathContext.DECIMAL128)).abs().compareTo(x.multiply(tolerance)) > 0) {
            BigDecimal integerPart = remainder.setScale(0, RoundingMode.FLOOR);
            BigDecimal tempNumerator = numeratorCurrent;
            BigDecimal tempDenominator = denominatorCurrent;

            numeratorCurrent = integerPart.multiply(numeratorCurrent).add(numeratorPrevious);
            numeratorPrevious = tempNumerator ;

            denominatorCurrent = integerPart.multiply(denominatorCurrent).add(denominatorPrevious);
            denominatorPrevious = tempDenominator ;

            if (remainder.subtract(integerPart).compareTo(BigDecimal.ZERO) == 0) { // Prevent division by zero
                break;
            }
            remainder = BigDecimal.ONE.divide(remainder.subtract(integerPart), MathContext.DECIMAL128);
        }

        return numeratorCurrent + "/" + denominatorCurrent;
    }

        public static void main(String[] args) {
            BigDecimal x = new BigDecimal("0");
            if (x.equals(BigDecimal.ZERO)) {
                System.out.println("0");
            } else {
                System.out.println(convertDecimalToFraction(x));
            }
        }
}

