package matrix.model;

import matrix.fileManaging.FilePath;
import matrix.fileManaging.MatrixFileHandler;
import matrix.fileManaging.MatrixType;
import matrix.gui.MatrixApp;

import java.math.BigDecimal;
import java.util.List;

public class MatrixSingleton {
    private static Matrix instance;

    private MatrixSingleton() {}

    // Use to handle the main matrix instance across different classes.
    public static Matrix getInstance() {
        if (instance == null) {
            // Load from file or create a new Matrix
            System.out.println("Instance is null");
            instance = MatrixFileHandler.loadMatrixFromFile(FilePath.MATRIX_PATH.getPath());

        }
        return instance;
    }
    // Save the instance directly to the matrix data file.
    public static void saveMatrix() {
        if (instance != null) {
            List<List<String>> matrixData = MatrixFileHandler.loadMatrixDataFromMatrix(instance);
            MatrixFileHandler.saveMatrixDataToFile(FilePath.MATRIX_PATH.getPath(),
                    0, matrixData, MatrixType.REGULAR);
        }
    }

    // If the matrix dimensions are not accurate, then generate a new instance with the specified rows and columns.
    public static void resizeInstance(int numRows, int numCols) {
        if (instance == null || instance.getRows() != numRows || instance.getCols() != numCols) {
            instance = new Matrix(numRows, numCols);
        }
    }


    // Use sparsely. This is only to be used when the matrix is initialized.
    public static void setInstance(Matrix newMatrix) {instance = newMatrix;}
}

