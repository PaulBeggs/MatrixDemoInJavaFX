package matrix.operators;

import matrix.fileManaging.FilePath;
import matrix.fileManaging.MatrixFileHandler;
import matrix.fileManaging.MatrixType;
import matrix.model.Matrix;
import matrix.model.MatrixSingleton;

import java.math.BigDecimal;
import java.util.List;

import static matrix.fileManaging.MatrixType.*;

public class MatrixInvertingOperations {

    public void invert() {
        Matrix ogMatrix = MatrixSingleton.getInstance();
        Matrix matrixInverse = ogMatrix.copy();

        if (matrixInverse.getCols() != matrixInverse.getRows()) {
            System.out.println("Matrix must be square.");
            return;
        }

        MatrixDeterminantOperations op = new MatrixDeterminantOperations(matrixInverse);
        BigDecimal determinant = op.calculateDeterminant();

        if (determinant.compareTo(BigDecimal.ZERO) == 0) {
            System.out.println("Matrix inverse is not defined.");
            return;
        }

        // Apply Gauss-Jordan elimination to get the inverse
        applyGaussJordanElimination(matrixInverse);

        saveMatrix(FilePath.INVERSE_PATH.getPath(), INVERSE, matrixInverse);
    }

    public void toEchelonForm() {
        Matrix ogMatrix = MatrixSingleton.getInstance();
        Matrix matrixREF = ogMatrix.copy();

        // Convert matrix to reduced echelon form
        convertToEchelonForm(matrixREF);
        System.out.println("This is the matrix that is being saved to the REF path: \n" + matrixREF);
        saveMatrix(FilePath.REF_PATH.getPath(), REF, matrixREF);
        MatrixFileHandler.setMatrix("REF", matrixREF);
    }

    public void toReducedEchelonForm() {
        Matrix ogMatrix = MatrixSingleton.getInstance();
        Matrix matrixRREF = ogMatrix.copy();

        // Convert matrix to reduced echelon form
        convertToReducedEchelonForm(matrixRREF);

        saveMatrix(FilePath.RREF_PATH.getPath(), RREF, matrixRREF);
        MatrixFileHandler.setMatrix("RREF", matrixRREF);
    }

    private void applyGaussJordanElimination(Matrix matrix) {

    }

    public void convertToEchelonForm(Matrix matrixREF) {
        ElementaryMatrixOperations emo = new ElementaryMatrixOperations(matrixREF);

        int numRows = matrixREF.getRows();
        int numCols = matrixREF.getCols();

        for (int p = 0; p < Math.min(numRows, numCols); p++) {
            int maxRow = findPivotRow(matrixREF, p);
            if (maxRow != p) {
                emo.swapRows(p, maxRow, false);
            }

            normalizePivotRow(matrixREF, p, emo);
            eliminateBelow(matrixREF, p);
        }
    }

    private int findPivotColumn(Matrix matrix, int row) {
        for (int j = 0; j < matrix.getCols(); j++) {
            if (matrix.getValue(row, j) != 0) {
                return j; // Pivot column found
            }
        }
        return -1; // No pivot in this row
    }

    private int findPivotRow(Matrix matrixREF, int p) {
        int maxRow = p;
        for (int i = p + 1; i < matrixREF.getRows(); i++) {
            if (Math.abs(matrixREF.getValue(i, p)) > Math.abs(matrixREF.getValue(maxRow, p))) {
                maxRow = i;
            }
        }
        return maxRow;
    }

    private void normalizePivotRow(Matrix matrixREF, int p, ElementaryMatrixOperations emo) {
        double pivotValue = matrixREF.getValue(p, p);
        if (pivotValue != 0) {
            emo.multiplyRow(p, 1 / pivotValue, false);
        }
    }

    private void eliminateBelow(Matrix matrixREF, int p) {
        int numCols = matrixREF.getCols();
        for (int i = p + 1; i < matrixREF.getRows(); i++) {
            double factor = matrixREF.getValue(i, p) / matrixREF.getValue(p, p);
            if (factor != 0) {
                for (int j = 0; j < numCols; j++) {
                    double valueToAdd = -factor * matrixREF.getValue(p, j);
                    matrixREF.setValue(i, j, matrixREF.getValue(i, j) + valueToAdd);
                }
            }
        }
    }

    private void eliminateAbove(Matrix matrix, int pivotRow, int pivotCol, ElementaryMatrixOperations emo) {
        for (int i = 0; i < pivotRow; i++) {
            double factor = matrix.getValue(i, pivotCol);
            if (factor != 0) {
                emo.addRows(pivotRow, i, -factor, false);
            }
        }
    }

    private void convertToReducedEchelonForm(Matrix matrix) {
        ElementaryMatrixOperations emo = new ElementaryMatrixOperations(matrix);

        // First, convert to echelon form
        convertToEchelonForm(matrix);

        int numRows = matrix.getRows();
        int numCols = matrix.getCols();

        // Then, iterate from bottom to top to make elements above pivots zero
        for (int p = numRows - 1; p >= 0; p--) {
            // Find the pivot column in the current row
            int pivotCol = findPivotColumn(matrix, p);
            if (pivotCol != -1) {
                eliminateAbove(matrix, p, pivotCol, emo);
            }
        }
    }

    private void saveMatrix(String filePath, MatrixType matrixType, Matrix matrix) {
        List<List<String>> matrixData = MatrixFileHandler.loadMatrixDataFromMatrix(matrix);
        MatrixFileHandler.saveMatrixDataToFile(filePath, BigDecimal.valueOf(0), matrixData, matrixType);
    }

    private BigDecimal normalizeZero(BigDecimal value) {return (value.compareTo(BigDecimal.ZERO) == 0) ? BigDecimal.ZERO : value;}
}

