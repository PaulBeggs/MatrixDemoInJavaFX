package matrix.view;

import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import matrix.gui.MatrixApp;
import matrix.model.Matrix;
import matrix.model.MatrixCell;

public class MatrixView {
    private MatrixCell[][] matrixCells;
    private final GridPane matrixGrid;

    public MatrixView(GridPane matrixGridFromController, MatrixCell[][] matrixCells) {
        this.matrixGrid = matrixGridFromController;
        this.matrixCells = matrixCells;
    }

    public void updateViews(boolean isEditable, Matrix matrix) {
        System.out.println("returned matrix from updatedViews: \n" + matrix);
        if (matrix != null && matrix.getRows() > 0 && matrix.getCols() > 0) {
            populateMatrixFromData(isEditable, matrix);
            resizeMatrix();
        } else {
            System.out.println("Error: Matrix is null or empty.");
        }
    }

    public void populateMatrixFromData(boolean isEditable, Matrix matrix) {
        matrixGrid.getChildren().clear();

        // Use the provided matrix to populate the UI
        int numRows = matrix.getRows();
        int numCols = matrix.getCols();

        matrixCells = new MatrixCell[numRows][numCols];
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                String cellValue = matrix.getValue(row, col);
                matrixCells[row][col] = new MatrixCell(row, col, cellValue, isEditable);
                matrixGrid.add(matrixCells[row][col].getTextField(), col, row);
            }
        }
    }

    private void resizeMatrix() {
        Stage mainstage = MatrixApp.getPrimaryStage();
        mainstage.sizeToScene();
    }
}