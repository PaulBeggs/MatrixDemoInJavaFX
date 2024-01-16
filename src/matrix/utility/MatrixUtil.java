package matrix.utility;

import matrix.model.MatrixCell;

import java.math.BigDecimal;
import java.util.Arrays;

public class MatrixUtil {

    public static String matrixCellsToString(MatrixCell[][] matrixCells) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < matrixCells.length; i++) {
            for (int j = 0; j < matrixCells[i].length; j++) {
                String cellValue = matrixCells[i][j].getTextField().getText();
                sb.append(String.format("(%d,%d): %s ", i, j, cellValue));
            }
            sb.append("\n"); // New line at the end of each row
        }
        return sb.toString();
    }
}
