package matrix.operators;

import matrix.model.BigDecimalMatrix;
import matrix.model.Matrix;

import java.math.BigDecimal;

public class MatrixInvertingOperationsTest {

    public static void main(String[] args) {
        testToEchelonForm();
    }

    private static void testToEchelonForm() {
        // Create an instance of Matrix with known values
        BigDecimalMatrix testMatrix = new BigDecimalMatrix(3, 3);
        testMatrix.setValue(0, 0, BigDecimal.ONE);
        testMatrix.setValue(0, 1, BigDecimal.TWO);
        testMatrix.setValue(0, 2, BigDecimal.valueOf(3.0));
        testMatrix.setValue(1, 0, BigDecimal.TWO);
        testMatrix.setValue(1, 1, BigDecimal.valueOf(5.0));
        testMatrix.setValue(1, 2, BigDecimal.valueOf(3.0));
        testMatrix.setValue(2, 0, BigDecimal.ONE);
        testMatrix.setValue(2, 1, BigDecimal.ZERO);
        testMatrix.setValue(2, 2, BigDecimal.valueOf(8.0));

        // Convert matrix to echelon form
        MatrixInvertingOperations operations = new MatrixInvertingOperations();

        // Print the resulting matrix
        System.out.println("Matrix in Echelon Form:");
    }

    private static void printMatrix(Matrix matrix) {
        for (int i = 0; i < matrix.getRows(); i++) {
            for (int j = 0; j < matrix.getCols(); j++) {
                System.out.print(matrix.getValue(i, j) + " ");
            }
            System.out.println();
        }
    }
}
