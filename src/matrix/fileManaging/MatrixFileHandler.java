package matrix.fileManaging;

import matrix.gui.MatrixApp;
import matrix.utility.BigDecimalUtil;
import matrix.model.Matrix;
import matrix.utility.FileUtil;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static matrix.fileManaging.MatrixType.*;

public class MatrixFileHandler {
    private static final Map<String, Matrix> matrices = new HashMap<>();

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
            throw new RuntimeException(e);
        }
    }

    public static List<List<String>> loadMatrixDataFromFile(String filePath) {
        List<List<String>> matrixData = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    List<String> rowData = Arrays.stream(line.trim().split("\\s+"))
                            .map(MatrixFileHandler::formatCellValue)
                            .collect(Collectors.toList());
                    matrixData.add(rowData);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading matrix from file: " + filePath, e);
        }
        return matrixData;
    }

    private static String formatCellValue(String value) {
        if (MatrixApp.isFractionMode() && value.contains("/")) {
            return BigDecimalUtil.convertFractionToDecimal(value).toPlainString();
        }
//        System.out.println("This is the formatted cell value inside the file handler class: \n" + value);
        return value;
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
                rowData.add(String.valueOf(matrix.getValue(row, col)));
            }
            matrixData.add(rowData);
        }
        return matrixData;
    }

    public static Matrix loadMatrixFromFile(String filePath) {
        List<List<String>> matrixData = loadMatrixDataFromFile(filePath);

        if (matrixData.isEmpty()) {
            populateMatrixIfEmpty();
            return new Matrix(1, 1);
        } else {
            int numRows = matrixData.size();
            int numCols = matrixData.get(0).size();
            Matrix matrix = new Matrix(numRows, numCols);

            for (int row = 0; row < numRows; row++) {
                for (int col = 0; col < numCols; col++) {
                    String cellValue = matrixData.get(row).get(col);
                    try {
                        if (cellValue.contains("/")) {
                            if (MatrixApp.isFractionMode()) {
                                // In fraction mode, keep the fraction format
                                matrix.setValue(row, col, cellValue);
                            } else {
                                // In decimal mode, convert fraction to decimal
                                BigDecimal decimalValue = BigDecimalUtil.convertFractionToDecimal(cellValue);
                                matrix.setValue(row, col, decimalValue.toPlainString());
                            }
                        } else {
                            // For decimal values, directly set the value
                            if (!MatrixApp.isFractionMode()) {
                                // In decimal mode, use the value as is
                                matrix.setValue(row, col, cellValue);
                            } else {
                                // In fraction mode, convert decimal to fraction
                                BigDecimal decimalValue = new BigDecimal(cellValue);
                                String fractionValue = BigDecimalUtil.convertDecimalToFraction(decimalValue);
                                matrix.setValue(row, col, fractionValue);
                            }
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number format in matrix file: " + cellValue);
                    }
                }
            }
            return matrix;
        }
    }

    public static void populateMatrixIfEmpty() {
        // Set matrix dimensions to 1x1 and value to 0.0
        double defaultValue = 0.0;
        List<List<String>> defaultMatrixData = List.of(
                List.of(String.valueOf(defaultValue))
        );

        // Save the default matrix data to the file
        MatrixFileHandler.saveMatrixDataToFile(FilePath.MATRIX_PATH.getPath(), String.valueOf(BigDecimal.ZERO), defaultMatrixData, REGULAR);
    }

    public static Matrix getMatrix(String key) {
        return matrices.get(key);
    }

    public static void setMatrix(String key, Matrix matrix) {
        matrices.put(key, matrix);
    }
}
