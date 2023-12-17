package matrix.operators;

import matrix.fileManaging.FilePath;
import matrix.fileManaging.MatrixType;
import matrix.model.Matrix;
import matrix.fileManaging.MatrixFileHandler;
import matrix.model.MatrixView;
import matrix.model.TriangularizationView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MatrixDeterminantOperations {

    private DecimalFormat decimalFormat = new DecimalFormat("#.#####");
    private Matrix computationalMatrix;
    private MatrixView matrixView;
    private int sign = 1;
    private int steps;
    private List<Integer> signChanges = new ArrayList<>();

    public MatrixDeterminantOperations(Matrix matrix) {
        this.computationalMatrix = matrix;
    }

    public BigDecimal calculateDeterminant() {
        double[][] matrixArray = computationalMatrix.getDoubleMatrix();
        int rows = computationalMatrix.getRows();
        int cols = computationalMatrix.getCols();
        computationalMatrix.setMatrix(matrixArray);

        if (rows != cols) {
            System.out.println("Determinant is not defined for non-square matrices.");
            return BigDecimal.valueOf(.12319620031999);
        }

        return determinant();
    }

    public BigDecimal determinant() {
        BigDecimal deter;
        if (isUpperTriangular() || isLowerTriangular()) {
            deter = multiplyDiameter().multiply(BigDecimal.valueOf(sign));
        } else {
            makeTriangular();
            deter = multiplyDiameter().multiply(BigDecimal.valueOf(sign));
        }
        return deter;
    }

    public void makeTriangular() {
        System.out.println("Matrix before 'Triangularization': \n" + computationalMatrix);

        for (int j = 0; j < computationalMatrix.getRows(); j++) {
            sortCol(j);
            for (int i = computationalMatrix.getRows() - 1; i > j; i--) {
                if (computationalMatrix.getDoubleMatrix()[i][j] == 0)
                    continue;

                double x = computationalMatrix.getDoubleMatrix()[i][j];
                double y = computationalMatrix.getDoubleMatrix()[i - 1][j];
                multiplyRow(i, (-y / x));
                addRow(i, i - 1);
                multiplyRow(i, (-x / y));

                steps++;
            }
        }
        System.out.println("Triangular matrix: \n" + formatMatrix(computationalMatrix, decimalFormat));
        MatrixFileHandler.setMatrix(FilePath.TRIANGULAR_PATH.getPath(), computationalMatrix);
    }

    public boolean isUpperTriangular() {
        if (computationalMatrix.getRows() < 2)
            return false;

        for (int i = 0; i < computationalMatrix.getRows(); i++) {
            for (int j = 0; j < i; j++) {
                if (computationalMatrix.getDoubleMatrix()[i][j] != 0)
                    return false;
            }
        }
        return true;
    }

    public boolean isLowerTriangular() {
        if (computationalMatrix.getRows() < 2)
            return false;

        for (int j = 0; j < computationalMatrix.getRows(); j++) {
            for (int i = 0; j > i; i++) {
                if (computationalMatrix.getDoubleMatrix()[i][j] != 0)
                    return false;
            }
        }
        return true;
    }

    public BigDecimal multiplyDiameter() {
        BigDecimal result = BigDecimal.ONE;
        for (int i = 0; i < computationalMatrix.getRows(); i++) {
            for (int j = 0; j < computationalMatrix.getRows(); j++) {
                if (i == j)
                    result = result.multiply(BigDecimal.valueOf(computationalMatrix.getDoubleMatrix()[i][j]));
            }
        }
        return result;
    }

    public void makeNonZero(int rowPos, int colPos) {
        int len = computationalMatrix.getRows();

        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                if (computationalMatrix.getDoubleMatrix()[i][j] != 0) {
                    if (i == rowPos) {
                        addCol(colPos, j);
                        return;
                    }
                    if (j == colPos) {
                        addRow(rowPos, i);
                        return;
                    }
                }
            }
        }
    }

    public void addRow(int row1, int row2) {
        for (int j = 0; j < computationalMatrix.getCols(); j++) {
            computationalMatrix.getDoubleMatrix()[row1][j] += computationalMatrix.getDoubleMatrix()[row2][j];
        }
    }

    public void addCol(int col1, int col2) {
        for (int i = 0; i < computationalMatrix.getRows(); i++) {
            computationalMatrix.getDoubleMatrix()[i][col1] += computationalMatrix.getDoubleMatrix()[i][col2];
        }
    }

    public void multiplyRow(int row, double num) {
        if (num < 0) {
            computationalMatrix.setSign(computationalMatrix.getSign() * -1);
        }

        for (int j = 0; j < computationalMatrix.getCols(); j++) {
            computationalMatrix.getDoubleMatrix()[row][j] *= num;
        }
    }

    private void sortCol(int col) {
        for (int i = computationalMatrix.getRows() - 1; i >= col; i--) {
            for (int k = computationalMatrix.getRows() - 1; k >= col; k--) {
                double tmp1 = computationalMatrix.getDoubleMatrix()[i][col];
                double tmp2 = computationalMatrix.getDoubleMatrix()[k][col];

                if (Math.abs(tmp1) < Math.abs(tmp2))
                    swapRow(i, k);
            }
        }
    }

    public void swapRow (int row1, int row2) {
        if (row1 != row2) {
            computationalMatrix.setSign(computationalMatrix.getSign() * -1);
            if (computationalMatrix != null && computationalMatrix.isValidRow(row1) && computationalMatrix.isValidRow(row2)) {
                double[] temp = computationalMatrix.getDoubleMatrix()[row1];
                computationalMatrix.getDoubleMatrix()[row1] = computationalMatrix.getDoubleMatrix()[row2];
                computationalMatrix.getDoubleMatrix()[row2] = temp;
            }
        }
    }

    private List<List<String>> formatMatrix(Matrix matrix, DecimalFormat decimalFormat) {
        List<List<String>> formattedMatrix = new ArrayList<>();

        for (int i = 0; i < matrix.getRows(); i++) {
            List<String> rowValues = new ArrayList<>();

            for (int j = 0; j < matrix.getCols(); j++) {
                double value = matrix.getDoubleMatrix()[i][j];

                if (value == 0) {
                    rowValues.add("0.0");
                } else {
                    rowValues.add(decimalFormat.format(value));
                }
            }

            formattedMatrix.add(rowValues);
        }

        return formattedMatrix;
    }

}
