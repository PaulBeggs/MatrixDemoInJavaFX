package matrix.model;

import matrix.fileManaging.FilePath;
import matrix.fileManaging.MatrixFileHandler;
import matrix.fileManaging.MatrixType;

import java.math.BigDecimal;
import java.util.List;

public class MatrixSingleton {
    private static Matrix instance;

    private MatrixSingleton() {}

    public static void resizeInstance(int numRows, int numCols) {
        if (instance == null || instance.getRows() != numRows || instance.getCols() != numCols) {

            instance = new Matrix(numRows, numCols);
        }
    }

        public static Matrix getInstance() {
        if (instance == null) {
            // Load from file or create a new Matrix
            System.out.println("Instance is null");
            instance = MatrixFileHandler.loadMatrixFromFile(FilePath.MATRIX_PATH.getPath()); // Implement this method to load from file
        }
        System.out.println("Current instance: \n" + instance);
        return instance;
    }

    public static void saveMatrix() {
        if (instance != null) {
            List<List<String>> matrixData = MatrixFileHandler.loadMatrixDataFromMatrix(instance);
            MatrixFileHandler.saveMatrixDataToFile(FilePath.MATRIX_PATH.getPath(),
                    BigDecimal.valueOf(0), matrixData, MatrixType.REGULAR);
        }
    }

    public static void setInstance(Matrix newMatrix) {
        instance = newMatrix;
    }

    // Add methods to update the matrix, which in turn update the UI or vice versa
}

