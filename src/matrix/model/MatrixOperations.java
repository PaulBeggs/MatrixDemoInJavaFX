package matrix.model;

public interface MatrixOperations {
    // Method to get the display matrix
    String[][] getMatrix();

    // Method to swap rows in both matrices
    void swapRows(int row1, int row2);

    // Method to multiply a row by a scalar in both matrices
    void multiplyRow(int row, double scalar);

    // Method to add two rows and update both matrices
    void addRows(int destinationRow, int sourceRow, double scalar);

    // Method to verify the total rows of a matrix.
    int getRows();

    // Method to verify the total cols of a matrix.
    int getCols();

    // Method to determine if a row is valid.
    boolean isValidRow(int row);

    // Method to determine if a column is valid.
    boolean isValidCol(int col);
}
