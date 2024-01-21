package matrix.fileManaging;

import matrix.model.Matrix;

import java.io.*;
import java.util.*;

import static matrix.fileManaging.MatrixType.*;

public class MatrixFileHandler {
    private static final Map<String, Matrix> matrices = new HashMap<>();

    public static void saveMatrixDataToFile (String fileName, double determinant, List<List<String>> matrixData, MatrixType matrixType) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false))) {
            for (List<String> row : matrixData) {
                String line = String.join(" ", row);
                writer.write(line);
                writer.newLine();
            }
            if (matrixType == REGULAR)  {
                // do nothing
            } else if (matrixType == DETERMINANT) {

                writer.newLine();
                writer.write("Determinant: " + determinant);
                writer.newLine();
                writer.newLine();
                writer.write("--------------");

            } else if (matrixType == TRIANGULAR) {
                // do nothing
            } else if (matrixType == IDENTITY){
                // do nothing
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<List<String>> loadMatrixDataFromFile (String filePath) {
        List<List<String>> matrixData = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) { // Avoid processing empty lines
                    List<String> rowData = Arrays.asList(line.trim().split("\\s+")); // Handles varying amounts of whitespace
                    matrixData.add(rowData);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading matrix from file: " + filePath, e);
        }
        return matrixData;
    }

    public static List<List<String>> loadMatrixDataFromMatrix (Matrix matrix) {
        System.out.println("Matrix is being loaded from the available matrix.");
        System.out.println("this is the matrix that is having its data parsed: \n" + matrix);
        if (matrix == null) {
            throw new IllegalArgumentException("Matrix cannot be null");
        }

        List<List<String>> matrixData = new ArrayList<>();
        for (int row = 0; row < matrix.getRows(); row++) {
            List<String> rowData = new ArrayList<>();
            for (int col = 0; col < matrix.getCols(); col++) {
                rowData.add(matrix.getStringValue(row, col));
            }
            matrixData.add(rowData);
        }
        return matrixData;
    }

    public static Matrix loadMatrixFromFile (String filePath) {
        List<List<String>> matrixData = loadMatrixDataFromFile(filePath);

        int numRows = matrixData.size();
        int numCols = matrixData.getFirst().size();
        Matrix matrix = new Matrix(numRows, numCols);

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                String value = matrixData.get(row).get(col);
                matrix.setValue(row, col, value);
            }
        }

        return matrix;
    }

    public static void populateMatrixIfEmpty() {
        // Set matrix dimensions to 1x1 and value to 0.0
        double defaultValue = 0.0;
        List<List<String>> defaultMatrixData = List.of(
                List.of(String.valueOf(defaultValue))
        );

        // Save the default matrix data to the file
        MatrixFileHandler.saveMatrixDataToFile(FilePath.MATRIX_PATH.getPath(), 0, defaultMatrixData, REGULAR);
    }

    public static Matrix getMatrix(String key) {
        return matrices.get(key);
    }

    public static void setMatrix(String key, Matrix matrix) {
        matrices.put(key, matrix);
    }
}
