package matrix.fileManaging;

import matrix.gui.MatrixApp;
import matrix.model.Matrix;
import matrix.util.ErrorsAndSyntax;
import matrix.util.MatrixUtil;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static matrix.fileManaging.MatrixType.*;

public class MatrixFileHandler {
    public static final Map<String, Matrix> matrices = new HashMap<>();

    public static void saveMatrixDataToFile (String fileName, String determinant, List<List<String>> matrixData, MatrixType matrixType) {
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
            ErrorsAndSyntax.showErrorPopup("Unknown error: \n" + e);
            throw new RuntimeException(e);
        }
    }

    public static List<List<String>> loadMatrixDataFromFile(String filePath) {
        List<List<String>> matrixData = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) { // Avoid processing empty lines
                    List<String> rowData = Arrays.asList(line.trim().split("\\s+")); // Handles varying amounts of whitespace

                    if (MatrixApp.isFractionMode()) {
                        // Convert to fraction
                        rowData = rowData.stream()
                                .map(MatrixUtil::convertDecimalToFraction)
                                .collect(Collectors.toList());
                    } else {
                        // Convert fraction to decimal
                        rowData = rowData.stream()
                                .map(MatrixUtil::convertFractionToDecimalString)
                                .collect(Collectors.toList());
                    }
                    matrixData.add(rowData);
                }
            }
        } catch (IOException e) {
            ErrorsAndSyntax.showErrorPopup("Error reading matrix from file: " + filePath);
            throw new RuntimeException("Error reading matrix from file: " + filePath, e);
        }
        return matrixData;
    }

    public static List<List<String>> loadMatrixDataFromMatrix (Matrix matrix) {
        System.out.println("Matrix is being loaded from the available matrix.");
        System.out.println("this is the matrix that is having its data parsed: \n" + matrix);
        if (matrix == null) {
            ErrorsAndSyntax.showErrorPopup("Matrix cannot be null. Try again.");
            throw new IllegalArgumentException("Matrix cannot be null");
        }

        List<List<String>> matrixData = new ArrayList<>();
        for (int row = 0; row < matrix.getRows(); row++) {
            List<String> rowData = new ArrayList<>();
            for (int col = 0; col < matrix.getCols(); col++) {
                rowData.add(matrix.getValue(row, col));
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
        MatrixFileHandler.saveMatrixDataToFile(FilePath.MATRIX_PATH.getPath(), "0", defaultMatrixData, REGULAR);
    }
}
