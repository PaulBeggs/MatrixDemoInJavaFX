package matrix.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import matrix.model.Matrix;
import matrix.model.MatrixSingleton;
import matrix.view.MatrixView;

import static matrix.fileManaging.MatrixFileHandler.matrices;

public class InitializeMatrixController {
    @FXML
    private GridPane inverseMatrix, REFMatrix, RREFMatrix, diagonalMatrix, mainMatrix, triMatrix, transposeMatrix;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab inverseTab, diagonalTab, triTab, mainTab;
    @FXML
    private Button setInverseInstance, setREFInstance, setRREFInstance, setDiagonalInstance, setMainInstance, setTriInstance, setTransposeInstance;
    @FXML
    private TextField inverseDeterminant, triangularDeterminant, diagonalDeterminant;
    private Matrix matrix;
    private MatrixSelectionCallback selectionCallback;

    @FXML
    private void initialize() {
        matrix = MatrixSingleton.getInstance();
        uploadFromFile();
    }

    private void uploadFromFile() {
        if (matrix.isSquare()) {
            getInverse();
            getTriangular();
            getDiagonal();

            setSquareMatrixGrids(inverseMatrix,diagonalMatrix, triMatrix);
        } else {
            // Remove tabs if not square
            tabPane.getTabs().removeAll(inverseTab, triTab, diagonalTab);
        }
        getTranspose();
        getRREFAndREF();

        setMatrixGrids(REFMatrix, RREFMatrix, transposeMatrix);
        if (!MatrixApp.fromMainController) {
            setMainMatrixGrid(mainMatrix);
        } else {
            tabPane.getTabs().remove(mainTab);
        }
    }

    private void getInverse() {
        Matrix matrixCopy = matrix.copy();
        matrixCopy.convertToInvertedForm();
    }

    private void getTriangular() {
        Matrix matrixCopy = matrix.copy();
        String value = matrixCopy.calculateDeterminant();
        inverseDeterminant.setText(value);
        triangularDeterminant.setText(value);
        diagonalDeterminant.setText(value);
    }

    private void getRREFAndREF() {
        Matrix matrixCopy = matrix.copy();
        matrixCopy.convertToReducedEchelonForm();
    }

    private void getDiagonal() {
    }

    private void getTranspose() {
        Matrix matrixCopy = matrix.copy();
        matrixCopy.transpose();
    }

    public void setSquareMatrixGrids(GridPane inverseMatrix, GridPane diagonalMatrix, GridPane triMatrix) {
        this.diagonalMatrix = diagonalMatrix;
        MatrixView matrixView = new MatrixView(diagonalMatrix);
        matrixView.updateViews(false, matrices.get("Inverse"));

        this.triMatrix = triMatrix;
        matrixView = new MatrixView(triMatrix);
        matrixView.updateViews(false, matrices.get("Triangular"));

        this.inverseMatrix = inverseMatrix;
        matrixView = new MatrixView(inverseMatrix);
        matrixView.updateViews(false, matrices.get("Inverse"));
    }

    public void setMatrixGrids(GridPane REFMatrix, GridPane RREFMatrix, GridPane transposeMatrix) {
        this.REFMatrix = REFMatrix;
        MatrixView matrixView = new MatrixView(REFMatrix);
        matrixView.updateViews(false, matrices.get("REF"));

        this.RREFMatrix = RREFMatrix;
        matrixView = new MatrixView(RREFMatrix);
        matrixView.updateViews(false, matrices.get("RREF"));

        this.transposeMatrix = transposeMatrix;
        matrixView = new MatrixView(transposeMatrix);
        matrixView.updateViews(false, matrices.get("Transpose"));
    }

    public void setMainMatrixGrid(GridPane mainMatrixGrid) {
        this.mainMatrix = mainMatrixGrid;
        MatrixView matrixView = new MatrixView(mainMatrix);
        matrixView.updateViews(false, MatrixSingleton.getInstance().copy());
    }

    @FXML
    private void handleMatrixInstanceSet(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        Matrix instanceToSet = switch (clickedButton.getId()) {
            case "setMainInstance" ->
                    MatrixSingleton.getInstance().copy();
            case "setTransposeInstance" ->
                    matrices.get("Transpose");
            case "setTriInstance" -> matrices.get("Triangular");
            case "setDiagonalInstance" -> matrices.get("Diagonal");
            case "setREFInstance" -> matrices.get("REF");
            case "setRREFInstance" -> matrices.get("RREF");
            case "setInverseInstance" -> matrices.get("Inverse");
            default -> null;
        };

        if (instanceToSet != null) {
            matrixSelected(instanceToSet);
            closeStage(event);
        }
    }

    private void closeStage(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    private void resizePane() {

    }

    public void setSelectionCallback(MatrixSelectionCallback selectionCallback) {
        this.selectionCallback = selectionCallback;
    }

    private void matrixSelected(Matrix matrix) {
        if (selectionCallback != null) {
            selectionCallback.onMatrixSelected(matrix);
        }
    }
}
