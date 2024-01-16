package matrix.utility;

import matrix.fileManaging.FilePath;
import matrix.fileManaging.MatrixFileHandler;
import matrix.fileManaging.MatrixType;
import matrix.gui.MatrixApp;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    public static void ensureAllFilesExist() {
        for (FilePath filePath : FilePath.values()) {
            ensureFileExists(filePath);
        }
    }

    private static void ensureFileExists(FilePath filePathEnum) {
        String filePath = filePathEnum.getPath();
        File file = new File(filePath);

        try {
            File parentDir = file.getParentFile();
            if (!parentDir.exists() && !parentDir.mkdirs()) {
                throw new IOException("Failed to create directory: " + parentDir);
            }
            if (!file.exists() && !file.createNewFile()) {
                throw new IOException("Failed to create file: " + file);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating file or directories for: " + filePath, e);
        }
    }

    public static void ensureNumbersAreUpdated(String filePath) {
        List<List<String>> matrixData = MatrixFileHandler.loadMatrixDataFromFile(filePath);
        List<List<String>> updatedMatrixData = new ArrayList<>();

        for (List<String> row : matrixData) {
            List<String> updatedRow = new ArrayList<>();
            for (String cell : row) {
                try {
                    System.out.println("This is the cell before it is updated to reflect the changes with fraction mode: \n" + cell);
                    BigDecimal cellValue = new BigDecimal(cell.contains("/") ?
                            BigDecimalUtil.convertFractionToDecimal(cell).toPlainString() :
                            cell);
                    String updatedCell = MatrixApp.isFractionMode() ?
                            BigDecimalUtil.convertDecimalToFraction(cellValue) :
                            BigDecimalUtil.convertBigDecimalToString(cellValue);
                    updatedRow.add(updatedCell);
                    System.out.println("This is the cell after it is updated to reflect the changes with fraction mode: \n" + updatedCell);
                } catch (NumberFormatException e) {
                    System.out.println("Error parsing cell value: " + cell);
                    updatedRow.add(cell); // Keep the original value in case of parse error
                }
            }
            updatedMatrixData.add(updatedRow);
        }

        MatrixFileHandler.saveMatrixDataToFile(filePath, "0", updatedMatrixData, MatrixType.REGULAR);
    }
}



