package matrix.model;

import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatrixFileHandler {
    private static final Map<String, Matrix> matrices = new HashMap<>();

    public static void saveMatrixToFile(String fileName, List<List<String>> matrixData) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false))) {

            for (List<String> row : matrixData) {
                //System.out.println("This SHOULD BE the matrixData being transferred: " + row);
                String line = String.join(" ", row);
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<List<String>> loadMatrixFromFile(String filePath) {
        List<List<String>> matrixData = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            matrixData = reader.lines()
                    .map(line -> List.of(line.split(" ")))
                    .toList();
            //System.out.println("Matrix data: \n" + matrixData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return matrixData;
    }

    public static Matrix getMatrix(String key) {
        return matrices.get(key);
    }

    public static void setMatrix(String key, Matrix matrix) {
        matrices.put(key, matrix);
    }

    public static void saveMatrixAndDeterminantToFile(String fileName, BigDecimal determinant, List<List<String>> matrixData) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false))) {

            for (List<String> row : matrixData) {
                String line = String.join(" ", row);
                writer.write(line);
                writer.newLine();
            }
            writer.newLine();
            writer.write("Determinant: " + determinant);
            writer.newLine();
            writer.write("--------------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
