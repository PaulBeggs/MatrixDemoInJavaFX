package matrix.view.paneInMyAss;

import javafx.scene.layout.Pane;
import matrix.model.Matrix;

import java.util.LinkedList;
import java.util.List;

public class MatrixConnection {

    //    Purpose: To track connections between matrix objects, mapping directly from a UI component (Pane) to the
    //    underlying matrix data (MatrixInfo).

    //    Structure: A HashMap<Pane, MatrixInfo> could be effective here, allowing you to quickly access the MatrixInfo
    //    associated with a specific Pane. This facilitates operations that require data about the matrix represented
    //    by a pane.


    private Pane pane;
    private Matrix matrix;
    private List<Matrix> connectedMatrices = new LinkedList<>();

    public MatrixConnection(Pane pane, Matrix matrix) {
        this.pane = pane;
        this.matrix = matrix;
    }

    public Pane getPane() {
        return pane;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public List<Matrix> getConnectedMatrices() {
        return connectedMatrices;
    }

    // Add a matrix to the connection
    public void connectMatrix(Matrix matrix) {
        connectedMatrices.add(matrix);
    }

    // Remove a matrix from the connection
    public void disconnectMatrix(Matrix matrix) {
        connectedMatrices.remove(matrix);
    }
}
