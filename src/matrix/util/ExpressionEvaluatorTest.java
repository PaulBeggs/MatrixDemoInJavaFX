package matrix.util;

import org.junit.Test;
import static matrix.util.ExpressionEvaluator.evaluate;
import static org.junit.Assert.assertEquals;

public class ExpressionEvaluatorTest {
    @Test
    public void testAddition() {
        assertEquals(8, evaluate("5 + 3"), 0.001);
    }

    @Test
    public void testSubtraction() {
        assertEquals(2, evaluate("5 - 3"), 0.001);
    }

    @Test
    public void testMultiplication() {
        assertEquals(10, evaluate("2 * 5"), 0.001);
    }

    @Test
    public void testDivision() {
        assertEquals(4, evaluate("8 / 2"), 0.001);
    }

    @Test
    public void testModulus() {
        assertEquals(1, evaluate("5 % 2"), 0.001);
    }

    @Test
    public void testExponentiation() {
        assertEquals(24, evaluate("2 ^ 3 * 3"), 0.001);
    }

    @Test
    public void testSquareRoot() {
        assertEquals(5, evaluate("sqrt(25)"), 0.001);
    }
    @Test
    public void testComplexExpression() {
        assertEquals(13, evaluate("2 * (3 + 5) - 6 / 2"), 0.001);
    }

    @Test
    public void testDecimalOperations() {
        assertEquals(4.0, evaluate("2.5 + 1.5 * 1"), 0.001);
    }

    @Test
    public void testNegativeNumbers() {
        assertEquals(-6, evaluate("-3 * 2"), 0.001);
    }

    @Test
    public void testCombinedOperators() {
        assertEquals(770, evaluate("2 ^ (2 ^ 3) * (5 - 2) + 10 / 2 % 3"), 0.001);
    }
    /*
     (2 ^ 2 ^ 3 * (5 - 2)) + ((10 / 2) % 3)
           =  768              = 2
                   = 770
     */

    @Test
    public void testMultipleParentheses() {
        assertEquals(14, evaluate("(4 + 5 - (3 + 5 / 5) + (3 * 3))"), 0.001);
    }
}