package matrix.operators;

import matrix.model.MatrixCell;


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

    public static String calculateSquareRoot(String sqrtExpression) {
        try {
            String numInsideSqrt = sqrtExpression.replace("/sqrt(", "").replace(")", "");
            double number = Double.parseDouble(numInsideSqrt);
            return String.valueOf(Math.sqrt(number));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid square root expression: " + sqrtExpression, e);
        }
    }
}
