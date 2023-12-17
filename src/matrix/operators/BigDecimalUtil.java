package matrix.operators;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalUtil {

    public static BigDecimal roundBigDecimal(BigDecimal value, int scale) {
        if (value.scale() > scale) {
            return value.setScale(scale, RoundingMode.HALF_UP);
        }
        return value;
    }
}