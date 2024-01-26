package matrix.view;

import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import matrix.gui.MatrixApp;
import matrix.model.Matrix;
import matrix.model.MatrixCell;

import java.util.ArrayList;
import java.util.List;

import static matrix.fileManaging.MatrixFileHandler.matrices;

public class TriangularizationView {
    GridPane matrixGrid;
    MatrixCell[][] matrixCells;
    private Matrix matrix;
    private int currentStep;
    private List<Integer> signChanges;

    public TriangularizationView(GridPane matrixGridFromController) {
        this.matrix = matrices.get("initial");
        this.matrixGrid = matrixGridFromController;

        this.signChanges = new ArrayList<>();
    }

    public void updateViews(String matrixKey) {
        this.matrix = matrices.get(matrixKey);
        if (matrix != null) {
            populateMatrixFromData();
            resizeMatrix();
        }
    }

    public void populateMatrixFromData() {
        matrixGrid.getChildren().clear();

        // Use the provided matrix to populate the UI
        int numRows = matrix.getRows();
        int numCols = matrix.getCols();

        matrixCells = new MatrixCell[numRows][numCols];
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                String cellValue = matrix.getValue(row, col);
                matrixCells[row][col] = new MatrixCell(row, col, cellValue, false);
                matrixGrid.add(matrixCells[row][col].getTextField(), col, row);
            }
        }
    }

    private void resizeMatrix() {
        Stage mainstage = MatrixApp.getPrimaryStage();
        mainstage.sizeToScene();
    }

    public void setCurrentStep(int currentStep) {this.currentStep = currentStep;}
    public void setMatrix(Matrix matrix) {this.matrix = matrix;}
    public Matrix getMatrix() {return matrix;}
    public void update (int currentStep) {setCurrentStep(currentStep);}
}
