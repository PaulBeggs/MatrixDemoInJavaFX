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

public class MatrixDeterminantOperations implements Operators {
    private final BigDecimalMatrix computationalMatrix;
    private final Matrix displayMatrix;
    private BigDecimal determinant;

    public MatrixDeterminantOperations(Matrix displayMatrix) {
        this.displayMatrix = displayMatrix;
        BigDecimal[][] computationalData = displayMatrix.toBigDecimalMatrix().getComputationalMatrix();
        this.computationalMatrix = new BigDecimalMatrix(computationalData);
    }

    public String calculateDeterminant() {
        determinant = computationalMatrix.calculateDeterminant();
        syncDisplayMatrix(); // Sync the changes back to the display matrix
        String formattedDeterminant;
        if (MatrixApp.isFractionMode()) {
            formattedDeterminant = BigDecimalUtil.convertDecimalToFraction(determinant);
        } else {
            formattedDeterminant = BigDecimalUtil.convertBigDecimalToString(determinant);
        }
        return formattedDeterminant;
    }

    public void makeTriangular() {
        computationalMatrix.makeTriangular();
        syncDisplayMatrix(); // Sync the changes back to the display matrix
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
        List<List<String>> matrixData = MatrixFileHandler.loadMatrixDataFromMatrix(displayMatrix);
        MatrixFileHandler.saveMatrixDataToFile(FilePath.DETERMINANT_PATH.getPath(), BigDecimalUtil.convertBigDecimalToString(determinant), matrixData, MatrixType.DETERMINANT);
    }
}

