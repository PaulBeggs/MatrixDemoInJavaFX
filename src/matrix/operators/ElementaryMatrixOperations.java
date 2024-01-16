package matrix.operators;

import matrix.fileManaging.FilePath;
import matrix.fileManaging.MatrixType;
import matrix.gui.MatrixApp;
import matrix.model.BigDecimalMatrix;
import matrix.model.Matrix;
import matrix.fileManaging.MatrixFileHandler;
import matrix.model.MatrixSingleton;
import matrix.utility.BigDecimalUtil;

import java.math.BigDecimal;
import java.util.List;

public class ElementaryMatrixOperations implements Operators {
    private final BigDecimalMatrix computationalMatrix;
    private final Matrix displayMatrix;

    public ElementaryMatrixOperations(Matrix displayMatrix) {
        this.displayMatrix = displayMatrix;
        System.out.println("(inside EMO) This is the display matrix: \n" + displayMatrix);
        BigDecimal[][] computationalData = displayMatrix.toBigDecimalMatrix().getComputationalMatrix();
        this.computationalMatrix = new BigDecimalMatrix(computationalData);
        System.out.println("(inside EMO) This is the computational matrix: \n" + computationalMatrix);
    }

    public void swapRows(int row1, int row2) {
        computationalMatrix.swapRows(row1, row2);
        syncDisplayMatrix();
    }

    public void multiplyRow(int row, BigDecimal scalar) {
        computationalMatrix.multiplyRow(row, scalar);
        syncDisplayMatrix();
    }

    public void addRows(int row1, int row2, BigDecimal multiplier) {
        computationalMatrix.addRows(row1, row2, multiplier);
        syncDisplayMatrix();
    }

    @Override
    public void syncDisplayMatrix() {
        for (int i = 0; i < computationalMatrix.getRows(); i++) {
            for (int j = 0; j < computationalMatrix.getCols(); j++) {
                BigDecimal value = computationalMatrix.getValue(i, j);
                String formattedValue = BigDecimalUtil.formatValueBasedOnMode(value);
                displayMatrix.setValue(i, j, formattedValue);
            }
        }
        MatrixSingleton.setInstance(displayMatrix);
        List<List<String>> matrixData = MatrixFileHandler.loadMatrixDataFromMatrix(displayMatrix);
        MatrixFileHandler.saveMatrixDataToFile(FilePath.MATRIX_PATH.getPath(), "0", matrixData, MatrixType.REGULAR);
    }
}
