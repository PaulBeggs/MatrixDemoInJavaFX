package matrix.model;

import javafx.scene.control.TextField;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public class MatrixView {
    private BorderPane borderPane;
    private MatrixCell[][] matrixCells;
    private GridPane matrixGrid;
    private TextField sizeRowsField, sizeColsField;

    public MatrixView(GridPane matrixGridFromController, MatrixCell[][] matrixCells, BorderPane borderPane) {
        this.matrixGrid = matrixGridFromController;
        this.matrixCells = matrixCells;
        this.borderPane = borderPane;
    }


    public void updateViews(boolean isEditable) {
        Matrix matrix = MatrixSingleton.getInstance();
        System.out.println("returned matrix from updatedViews: \n" + matrix);
        if (matrix != null && matrix.getRows() > 0 && matrix.getCols() > 0) {
            populateMatrixFromData(matrix, isEditable);
        } else {
            System.out.println("Error: Matrix is null or empty.");
        }
    }

    public void populateMatrixFromData(Matrix matrix, boolean isEditable) {
        matrixGrid.getChildren().clear();

        // Use the provided matrix to populate the UI
        int numRows = matrix.getRows();
        int numCols = matrix.getCols();

        matrixCells = new MatrixCell[numRows][numCols];
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                double cellValue = matrix.getValue(row, col);
                matrixCells[row][col] = new MatrixCell(row, col, String.valueOf(cellValue), isEditable);
                matrixGrid.add(matrixCells[row][col].getTextField(), col, row);
            }
        }
    }

    public void setSizeColsField(TextField sizeColsField) {this.sizeColsField = sizeColsField;}
    public void setSizeRowsField(TextField sizeRowsField) {this.sizeRowsField = sizeRowsField;}
    public void setMatrixGrid(GridPane matrixGrid) {this.matrixGrid = matrixGrid;}
    public void setMatrixCells (MatrixCell[][] matrixCells) {this.matrixCells = matrixCells;}

}