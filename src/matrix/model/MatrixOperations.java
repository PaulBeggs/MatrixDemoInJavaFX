package matrix.model;

public interface MatrixOperations {
    double[][] getDoubleMatrix();
    int getRows();
    int getCols();
    boolean isValidRow(int row);
}
