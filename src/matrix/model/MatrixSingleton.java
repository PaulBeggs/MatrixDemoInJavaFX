package matrix.model;

import matrix.fileManaging.FilePath;
import matrix.fileManaging.MatrixFileHandler;
import matrix.fileManaging.MatrixType;

import java.util.List;

public class MatrixSingleton {
    private static Matrix instance;
    private static Matrix triangularInstance;

    private MatrixSingleton() {}

    // Use to handle the main matrix instance across different classes.
    public static Matrix getInstance() {
        if (instance == null) {
            // Load from file or create a new Matrix
            System.out.println("Instance is null");
            instance = MatrixFileHandler.loadMatrixFromFile(FilePath.MATRIX_PATH.getPath());
//            instance = new Matrix(1,1);
//            instance.setValue(0,0, "0.01614006291888");
        }
        return instance;
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
            instance = new Matrix(numRows, numCols);
        }
    }

    public static Matrix getTriangularInstance() {
        return triangularInstance;
    }

    // Use sparsely. This is only to be used when the matrix is initialized.
    public static void setInstance(Matrix newMatrix) {instance = newMatrix;}
    public static void setTriangularInstance(Matrix newTriMatrix) {triangularInstance = newTriMatrix;}

}

