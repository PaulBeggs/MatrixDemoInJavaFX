package matrix.model;

import matrix.fileManaging.FilePath;
import matrix.fileManaging.MatrixFileHandler;
import matrix.fileManaging.MatrixType;
import matrix.gui.MatrixApp;
import matrix.utility.BigDecimalUtil;

import java.math.BigDecimal;
import java.util.List;

public class MatrixSingleton {
    private static Matrix instance;
    private static BigDecimalMatrix computationalMatrix;

    private MatrixSingleton() {}

    // Use to handle the main matrix instance across different classes.
    public static Matrix getDisplayInstance() {
        if (instance == null) {
            System.out.println("Display Instance is null");
            instance = MatrixFileHandler.loadMatrixFromFile(FilePath.MATRIX_PATH.getPath());
            if (instance == null) {
                System.out.println("Display instance is still null after load attempt.");
                MatrixFileHandler.populateMatrixIfEmpty();
                instance = new Matrix(1, 1);
            }
        }
        formatMatrixBasedOnMode(instance);
        return instance;
    }

    private static void formatMatrixBasedOnMode(Matrix matrix) {
        for (int row = 0; row < matrix.getRows(); row++) {
            for (int col = 0; col < matrix.getCols(); col++) {
                String cellValue = matrix.getValue(row, col);

                if (cellValue == null) {
                    cellValue = MatrixApp.isFractionMode() ? "0/1" : "0";
                }

                try {
                    BigDecimal decimalValue = new BigDecimal(cellValue.contains("/") ?
                            BigDecimalUtil.convertFractionToDecimal(cellValue).toPlainString() :
                            cellValue);

                    String formattedValue = MatrixApp.isFractionMode() ?
                            BigDecimalUtil.convertDecimalToFraction(decimalValue) :
                            decimalValue.toPlainString();

                    matrix.setValue(row, col, formattedValue);
                } catch (NumberFormatException e) {
                    System.out.println("Error parsing cell value: " + cellValue);
                    matrix.setValue(row, col, "0"); // Set default value on error
                }
            }
        }
    }


    public static BigDecimalMatrix getComputationalInstance() {
        if (computationalMatrix == null) {
            System.out.println("Computational Instance is null");
            Matrix displayInstance = getDisplayInstance();
            BigDecimal[][] computationalData = instance.toBigDecimalMatrix().getComputationalMatrix();
            computationalMatrix = new BigDecimalMatrix(computationalData);
        }
        return computationalMatrix;
    }


    // Save the instance directly to the matrix data file.
    public static void saveMatrix() {
        if (instance != null) {
            List<List<String>> matrixData = MatrixFileHandler.loadMatrixDataFromMatrix(instance);
            MatrixFileHandler.saveMatrixDataToFile(FilePath.MATRIX_PATH.getPath(),
                    "0", matrixData, MatrixType.REGULAR);
        }
    }

    // If the matrix dimensions are not accurate, then generate a new instance with the specified rows and columns.
    public static void resizeInstance(int numRows, int numCols) {
        if (instance == null || instance.getRows() != numRows || instance.getCols() != numCols) {
            System.out.println("The matrix must be resized.");
            instance = new Matrix(numRows, numCols);
            initializeMatrix(instance, numRows, numCols);
        }
    }

    private static void initializeMatrix(Matrix matrix, int numRows, int numCols) {
        String defaultValue = MatrixApp.isFractionMode() ? "0/1" : "0";
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                matrix.setValue(row, col, defaultValue);
            }
        }
    }


    // Use sparsely. This is only to be used when the matrix is initialized.
    public static void setInstance(Matrix newMatrix) {instance = newMatrix;}
    public static BigDecimalMatrix getComputationalMatrix() {return computationalMatrix;}
}

