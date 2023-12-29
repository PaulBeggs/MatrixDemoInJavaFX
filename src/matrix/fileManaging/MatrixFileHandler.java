package matrix.fileManaging;

import matrix.model.Matrix;

import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static matrix.fileManaging.MatrixType.*;

public class MatrixFileHandler {
    private static final Map<String, Matrix> matrices = new HashMap<>();

    public static void saveMatrixDataToFile(String fileName, BigDecimal determinant, List<List<String>> matrixData, MatrixType matrixType) {
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

    public static List<List<String>> loadMatrixFromFile(String filePath) {
        List<List<String>> matrixData;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            matrixData = reader.lines()
                    .map(line -> List.of(line.split(" ")))
                    .toList();
            //System.out.println("Matrix data: \n" + matrixData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return matrixData;
    }

    public static Matrix getMatrix(String key) {
        return matrices.get(key);
    }

    public static void setMatrix(String key, Matrix matrix) {
        matrices.put(key, matrix);
    }

    public static void populateFileIfEmpty(String filePath) {
        File file = new File(filePath);

        try {
            // Check if the file is empty
            if (file.length() == 0) {
                // If the file is empty, write "0.0" to it
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("0.0");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
