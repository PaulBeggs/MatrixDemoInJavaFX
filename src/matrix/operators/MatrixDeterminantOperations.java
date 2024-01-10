package matrix.operators;

import matrix.fileManaging.MatrixFileHandler;
import matrix.model.Matrix;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MatrixDeterminantOperations {
    private List<String> operationSummary;
    private final Matrix matrix;
    private final DecimalFormat decimalFormat = new DecimalFormat("#.#####");
    private int steps = 1, operationsStep = 1;

    public MatrixDeterminantOperations(Matrix matrix) {
        this.matrix = matrix;
        this.operationSummary = new ArrayList<>();
    }

    public BigDecimal calculateDeterminant() {
        if (matrix.getRows() != matrix.getCols()) {
            System.out.println("Determinant is not defined for non-square matrices.");
            throw new IllegalArgumentException("Determinant not defined for non-square matrices");
        }

        saveMatrixState("initial");
        if (!isUpperTriangular() && !isLowerTriangular()) {
            System.out.println("Matrix before 'Triangularization': \n" + matrix);
            saveMatrixState("initial");
            makeTriangular();
        }
        BigDecimal deter = multiplyDiameter().multiply(BigDecimal.valueOf(matrix.getSign()));
        System.out.println("Triangular matrix: \n" + formatMatrix());
        return deter;
    }


    public void makeTriangular() {
        for (int j = 0; j < matrix.getRows(); j++) {
            sortCol(j);
            for (int i = matrix.getRows() - 1; i > j; i--) {
                if (matrix.getDoubleMatrix()[i][j] == 0)
                    continue;

                double x = matrix.getDoubleMatrix()[i][j];
                double y = matrix.getDoubleMatrix()[i - 1][j];
                multiplyRow(i, (-y / x));
//                operationSummary.add(operationsStep++ + ", Multiplied row " + (i + 1) + " by " + (-y / x) + "\n");
                addRow(i, i - 1);
                operationSummary.add(operationsStep++ + ", Added row " + (i + 1) + " to row " + (i + 1) + "\n");
                multiplyRow(i, (-x / y));
//                operationSummary.add(operationsStep++ + ", Multiplied row " + (i + 1) + " by " + (-x / y) + "\n");

                saveMatrixState("step_" + steps++);
            }
        }
        System.out.println("These are the amount of steps needed (makeTriangular): \n" + steps);

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

                if (Math.abs(tmp1) < Math.abs(tmp2)) {
                    operationSummary.add(operationsStep++ + ", Swapped row " + (i + 1) + " with row " + (k + 1) + "\n");
                    swapRow(i, k);
                }
            }
        }
    }

    public void swapRow(int row1, int row2) {
        if (row1 != row2) {
            matrix.setSign(matrix.getSign() * -1);
            if (matrix.isValidRow(row1) && matrix.isValidRow(row2)) {
                double[] temp = matrix.getDoubleMatrix()[row1];
                matrix.getDoubleMatrix()[row1] = matrix.getDoubleMatrix()[row2];
                matrix.getDoubleMatrix()[row2] = temp;
            }
        }
        saveMatrixState("step_" + steps++);
    }

    private BigDecimal normalizeZero(BigDecimal value) {return (value.compareTo(BigDecimal.ZERO) == 0) ? BigDecimal.ZERO : value;}

    private List<List<String>> formatMatrix() {
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

    private void saveMatrixState(String key) {
        Matrix copy = new Matrix(matrix.getRows(), matrix.getCols());
        double[][] matrixData = matrix.getDoubleMatrix();
        for (int i = 0; i < matrix.getRows(); i++) {
            System.arraycopy(matrixData[i], 0, copy.getDoubleMatrix()[i], 0, matrix.getCols());
        }

        System.out.println("Matrix inside of the save state: \n" + key + "\n" + copy);
        MatrixFileHandler.setMatrix(key, copy);
    }

    public int getTotalSteps() {
        System.out.println("These are the amount of steps needed (getTotalSteps): \n" + steps);
        return steps;
    }

    public List<String> getOperationSummary() {
        return operationSummary;
    }
}
