package matrix.model;

import matrix.fileManaging.MatrixFileHandler;
import org.junit.Test;

public class MatrixTest {

    public Matrix initialize() {
        Matrix matrix = new Matrix(3,4);
        matrix.setValue(0, 0, "1");
        matrix.setValue(0, 1, "2");
        matrix.setValue(0, 2, "3");
        matrix.setValue(0, 3, "4");
        matrix.setValue(1, 0, "5");
        matrix.setValue(1, 1, "6");
        matrix.setValue(1, 2, "7");
        matrix.setValue(1, 3, "sqrt(5)");
        matrix.setValue(2, 0, "9");
        matrix.setValue(2, 1, "10/3");
        matrix.setValue(2, 2, "11");
        matrix.setValue(2, 3, "12");
        return matrix;
    }

    @Test
    public void REF() {
        Matrix matrix = initialize();
        // Create a 3x4 matrix and populate it with some values
        System.out.println("Init matrix: \n" +  matrix);

        // Convert to echelon form
        System.out.println("REF: \n");
        matrix.convertToEchelonForm();

        // Retrieve and print the echelon form matrix
        System.out.println(MatrixFileHandler.matrices.get("REF"));
    }

    @Test
    public void RREF() {
        Matrix matrix = initialize();

        REF();

        System.out.println("RREF: \n");

        matrix.convertToReducedEchelonForm();

        System.out.println(MatrixFileHandler.matrices.get("RREF"));
    }

    @Test
    public void invert() {
        Matrix matrix = MatrixSingleton.getInstance().copy();

        System.out.println("Init matrix: \n");

        System.out.println(matrix);

        matrix.handleInvertingFunctionality();

        System.out.println(MatrixFileHandler.matrices.get(("Inverse")));
    }
}

