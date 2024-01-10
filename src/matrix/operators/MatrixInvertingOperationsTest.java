package matrix.operators;

import matrix.model.Matrix;

public class MatrixInvertingOperationsTest {

    public static void main(String[] args) {
        testToEchelonForm();
    }

    private static void testToEchelonForm() {
        // Create an instance of Matrix with known values
        Matrix testMatrix = new Matrix(3, 3);
        testMatrix.setValue(0, 0, 1);
        testMatrix.setValue(0, 1, 2);
        testMatrix.setValue(0, 2, 3);
        testMatrix.setValue(1, 0, 2);
        testMatrix.setValue(1, 1, 5);
        testMatrix.setValue(1, 2, 3);
        testMatrix.setValue(2, 0, 1);
        testMatrix.setValue(2, 1, 0);
        testMatrix.setValue(2, 2, 8);

        // Convert matrix to echelon form
        MatrixInvertingOperations operations = new MatrixInvertingOperations();
        operations.convertToEchelonForm(testMatrix);

        // Print the resulting matrix
        System.out.println("Matrix in Echelon Form:");
        printMatrix(testMatrix);
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
