package matrix.operators;

import matrix.fileManaging.FilePath;
import matrix.fileManaging.MatrixFileHandler;
import matrix.fileManaging.MatrixType;
import matrix.gui.MatrixApp;
import matrix.model.BigDecimalMatrix;
import matrix.model.Matrix;
import matrix.model.MatrixSingleton;
import matrix.utility.BigDecimalUtil;

import java.math.BigDecimal;
import java.util.List;

import static matrix.fileManaging.MatrixType.*;

public class MatrixInvertingOperations implements Operators {

    public void invert() {
        Matrix ogMatrix = MatrixSingleton.getDisplayInstance();
        Matrix matrixDisplayInverse = ogMatrix.copy();
        BigDecimalMatrix ogComputationalMatrix = MatrixSingleton.getComputationalMatrix();
        BigDecimalMatrix computationalInverseMatrix = ogComputationalMatrix.copy();

        if (computationalInverseMatrix.getCols() != computationalInverseMatrix.getRows()) {
            System.out.println("Matrix must be square.");
            return;
        }

        MatrixDeterminantOperations op = new MatrixDeterminantOperations(matrixDisplayInverse);
        String determinant = op.calculateDeterminant();

        if (determinant.compareTo(String.valueOf(BigDecimal.ZERO)) == 0) {
            System.out.println("Matrix inverse is not defined.");
            return;
        }

        // Apply Gauss-Jordan elimination to get the inverse
//        applyGaussJordanElimination(computationalInverseMatrix);

        saveMatrix(FilePath.INVERSE_PATH.getPath(), INVERSE, matrixDisplayInverse);
    }

    public void toEchelonForm() {
        Matrix ogMatrix = MatrixSingleton.getDisplayInstance();
        Matrix matrixDisplayREF = ogMatrix.copy();
        BigDecimalMatrix ogComputationalMatrix = MatrixSingleton.getComputationalMatrix();
        BigDecimalMatrix computationalREF = ogComputationalMatrix.copy();

        // Convert matrix to reduced echelon form
        computationalREF.convertToEchelonForm();

        System.out.println("This is the matrix that is being saved to the REF path: \n" + matrixDisplayREF);
        saveMatrix(FilePath.REF_PATH.getPath(), REF, matrixDisplayREF);
        MatrixFileHandler.setMatrix("REF", matrixDisplayREF);
    }

    public void toReducedEchelonForm() {
        Matrix ogMatrix = MatrixSingleton.getDisplayInstance();
        Matrix matrixDisplayRREF = ogMatrix.copy();
        BigDecimalMatrix ogComputationalMatrix = MatrixSingleton.getComputationalMatrix();
        BigDecimalMatrix computationalRREF = ogComputationalMatrix.copy();

        // Convert matrix to reduced echelon form
        computationalRREF.convertToReducedEchelonForm();

        saveMatrix(FilePath.RREF_PATH.getPath(), RREF, matrixDisplayRREF);
        MatrixFileHandler.setMatrix("RREF", matrixDisplayRREF);
    }

    private void saveMatrix(String filePath, MatrixType matrixType, Matrix matrix) {
        List<List<String>> matrixData = MatrixFileHandler.loadMatrixDataFromMatrix(matrix);
        MatrixFileHandler.saveMatrixDataToFile(filePath, "0", matrixData, matrixType);
    }

    @Override
    public void syncDisplayMatrix() {
//        for (int i = 0; i < computationalMatrix.getRows(); i++) {
//            for (int j = 0; j < computationalMatrix.getCols(); j++) {
//                BigDecimal value = computationalMatrix.getValue(i, j);
//                String formattedValue;
//
//                if (MatrixApp.isFractionMode()) {
//                    // Convert to fraction format if in fraction mode
//                    formattedValue = BigDecimalUtil.convertDecimalToFraction(value);
//                } else {
//                    // Keep in decimal format
//                    formattedValue = value.stripTrailingZeros().toPlainString();
//                }
//
//                displayMatrix.setValue(i, j, formattedValue);
//            }
//        }
//        MatrixSingleton.setInstance(displayMatrix);
//        List<List<String>> matrixData = MatrixFileHandler.loadMatrixDataFromMatrix(displayMatrix);
//        MatrixFileHandler.saveMatrixDataToFile(FilePath.MATRIX_PATH.getPath(), BigDecimal.ZERO, matrixData, MatrixType.REGULAR);
    }
}

