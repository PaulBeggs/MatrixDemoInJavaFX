package matrix.operators;

import matrix.fileManaging.FilePath;
import matrix.fileManaging.MatrixType;
import matrix.model.Matrix;
import matrix.fileManaging.MatrixFileHandler;
import matrix.model.MatrixView;
import matrix.model.TriangularizationView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MatrixDeterminantOperations {

    private final Matrix matrix;

    public MatrixDeterminantOperations(Matrix matrix) {
        this.matrix = matrix;
    }

    public BigDecimal calculateDeterminant() {
        double[][] matrixArray = matrix.getDoubleMatrix();
        int rows = matrix.getRows();
        int cols = matrix.getCols();
        matrix.setMatrix(matrixArray);

        if (rows != cols) {
            System.out.println("Determinant is not defined for non-square matrices.");
            return BigDecimal.valueOf(.12319620031999);
        }

        DeterminantCalc determinantCalc = new DeterminantCalc(matrix);

        return determinantCalc.determinant();
    }

    private static class DeterminantCalc {

        private final DecimalFormat decimalFormat = new DecimalFormat("#.#####");
        private final Matrix matrix;
        private final int sign = 1;
        private int steps;
        private final List<Integer> signChanges = new ArrayList<>();
        private final TriangularizationView tV;

        DeterminantCalc(Matrix matrix) {
            this.matrix = matrix;
            tV = new TriangularizationView(matrix);
        }

        public BigDecimal determinant() {
            BigDecimal deter;
            if (!isUpperTriangular() && !isLowerTriangular()) {
                makeTriangular();
            }
            deter = multiplyDiameter().multiply(BigDecimal.valueOf(sign));
            tV.setMatrix(matrix);
            System.out.println("Triangular matrix: \n" + formatMatrix(matrix, decimalFormat));
//            MatrixFileHandler.setMatrix(FilePath.TRIANGULAR_PATH.getPath(), matrix);
            tV.setDeterminantValue(deter);
            return deter;
        }


        public void makeTriangular() {
            System.out.println("Matrix before 'Triangularization': \n" + matrix);

            for (int j = 0; j < matrix.getRows(); j++) {
                sortCol(j);
                for (int i = matrix.getRows() - 1; i > j; i--) {
                    if (matrix.getDoubleMatrix()[i][j] == 0)
                        continue;

                    double x = matrix.getDoubleMatrix()[i][j];
                    double y = matrix.getDoubleMatrix()[i - 1][j];
                    multiplyRow(i, (-y / x));
                    addRow(i, i - 1);
                    multiplyRow(i, (-x / y));

                    steps++;
                    tV.update(steps);
                }
            }
        }


        public boolean isUpperTriangular() {
            if (matrix.getRows() < 2)
                return false;

            for (int i = 0; i < matrix.getRows(); i++) {
                for (int j = 0; j < i; j++) {
                    if (matrix.getDoubleMatrix()[i][j] != 0)
                        return false;
                }
            }
            return true;
        }

        public boolean isLowerTriangular() {
            if (matrix.getRows() < 2)
                return false;

            for (int j = 0; j < matrix.getRows(); j++) {
                for (int i = 0; j > i; i++) {
                    if (matrix.getDoubleMatrix()[i][j] != 0)
                        return false;
                }
            }
            return true;
        }

        public BigDecimal multiplyDiameter() {
            BigDecimal result = BigDecimal.ONE;
            for (int i = 0; i < matrix.getRows(); i++) {
                for (int j = 0; j < matrix.getRows(); j++) {
                    if (i == j)
                        result = result.multiply(BigDecimal.valueOf(matrix.getDoubleMatrix()[i][j]));
                }
            }
            return result;
        }

//        public void makeNonZero(int rowPos, int colPos) {
//            int len = matrix.getRows();
//
//            for (int i = 0; i < len; i++) {
//                for (int j = 0; j < len; j++) {
//                    if (matrix.getDoubleMatrix()[i][j] != 0) {
//                        if (i == rowPos) {
//                            addCol(colPos, j);
//                            return;
//                        }
//                        if (j == colPos) {
//                            addRow(rowPos, i);
//                            return;
//                        }
//                    }
//                }
//            }
//        }

        public void addRow(int row1, int row2) {
            for (int j = 0; j < matrix.getCols(); j++) {
                BigDecimal value1 = BigDecimal.valueOf(matrix.getDoubleMatrix()[row1][j]);
                BigDecimal value2 = BigDecimal.valueOf(matrix.getDoubleMatrix()[row2][j]);
                BigDecimal result = value1.add(value2).setScale(5, RoundingMode.HALF_UP);
                matrix.getDoubleMatrix()[row1][j] = normalizeZero(result).doubleValue();
            }
        }


        public void multiplyRow(int row, double num) {
            if (num < 0) {
                matrix.setSign(matrix.getSign() * -1);
                tV.setSign(matrix.getSign());
                tV.updateSignChanges(matrix.getSign());
            }

            for (int j = 0; j < matrix.getCols(); j++) {
                BigDecimal value = BigDecimal.valueOf(matrix.getDoubleMatrix()[row][j]);
                BigDecimal result = value.multiply(BigDecimal.valueOf(num)).setScale(5, RoundingMode.HALF_UP);
                matrix.getDoubleMatrix()[row][j] = normalizeZero(result).doubleValue();
            }
        }


        private void sortCol(int col) {
            for (int i = matrix.getRows() - 1; i >= col; i--) {
                for (int k = matrix.getRows() - 1; k >= col; k--) {
                    double tmp1 = matrix.getDoubleMatrix()[i][col];
                    double tmp2 = matrix.getDoubleMatrix()[k][col];

                    if (Math.abs(tmp1) < Math.abs(tmp2))
                        swapRow(i, k);
                }
            }
        }

        public void swapRow (int row1, int row2) {
            if (row1 != row2) {
                matrix.setSign(matrix.getSign() * -1);
                tV.setSign(matrix.getSign());
                tV.updateSignChanges(matrix.getSign());
                if (matrix.isValidRow(row1) && matrix.isValidRow(row2)) {
                    double[] temp = matrix.getDoubleMatrix()[row1];
                    matrix.getDoubleMatrix()[row1] = matrix.getDoubleMatrix()[row2];
                    matrix.getDoubleMatrix()[row2] = temp;
                }
            }
        }

        private BigDecimal normalizeZero(BigDecimal value) {
            return (value.compareTo(BigDecimal.ZERO) == 0) ? BigDecimal.ZERO : value;
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
//        public void addCol(int col1, int col2) {
//            for (int i = 0; i < matrix.getRows(); i++) {
//                matrix.getDoubleMatrix()[i][col1] += matrix.getDoubleMatrix()[i][col2];
//            }
//        }
}
