package matrix.model;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import matrix.fileManaging.FilePath;
import matrix.fileManaging.MatrixFileHandler;
import matrix.operators.MatrixDeterminantOperations;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TriangularizationView {
    GridPane matrixGrid;
    MatrixCell[][] matrixCells;
    private Matrix matrix;
    private List<List<TextField>> matrixTextFields = new ArrayList<>();
    private int currentStep;
    private List<Integer> signChanges;
    private int sign = 1;

    public TriangularizationView(Matrix matrix, GridPane matrixGridFromController) {
        this.matrix = matrix;
        this.matrixGrid = matrixGridFromController;

        this.signChanges = new ArrayList<>();
    }

    public void updateViews(String matrixKey) {
        Matrix matrix = MatrixFileHandler.getMatrix(matrixKey); // Assume getMatrix fetches from hashmap
        if (matrix != null) {
            List<List<String>> matrixData = convertMatrixToList(matrix);
            System.out.println("This is the matrixData inside of updateViews (TriangularizationViews): \n" + matrixData);
            populateMatrixGrid(matrixData);
        }
    }

    private List<List<String>> convertMatrixToList(Matrix matrix) {
        List<List<String>> matrixData = new ArrayList<>();
        for (int i = 0; i < matrix.getRows(); i++) {
            List<String> row = new ArrayList<>();
            for (int j = 0; j < matrix.getCols(); j++) {
                row.add(String.format("%.1f", matrix.getValue(i, j)));
            }
            matrixData.add(row);
        }
        return matrixData;
    }

    private void populateMatrixGrid(List<List<String>> matrixData) {
        matrixGrid.getChildren().clear();
        matrixTextFields.clear(); // Clear previous TextFields if any

        for (int row = 0; row < matrixData.size(); row++) {
            List<String> rowData = matrixData.get(row);
            List<TextField> rowList = new ArrayList<>();

            for (int col = 0; col < rowData.size(); col++) {
                TextField cell = createCell(rowData.get(col), row, col);
                matrixGrid.add(cell, col, row);
                rowList.add(cell);
            }

            matrixTextFields.add(rowList);
        }
    }

    private TextField createCell(String value, int row, int col) {
        TextField cell = new TextField();
        cell.getStyleClass().add("textfield-grid-cell");
        cell.setEditable(false);
        cell.setText(value);

        return cell;
    }

    public void setMatrixGrid(GridPane matrixGrid) {
        this.matrixGrid = matrixGrid;
    }
    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }
    public int getCurrentStep() {
        return currentStep;
    }
    public Integer getSign() {
        return sign;
    }
    public void setSign(int sign) {
        this.sign = sign;
    }
    public List<Integer> compileSignChanges() {
        return signChanges;
    }
    public void updateSignChanges(int sign) {
        signChanges.add(sign);
    }
    public GridPane getMatrixGrid() {
        return matrixGrid;
    }
    public MatrixCell[][] getMatrixCells() {
        return matrixCells;
    }
    public void setMatrixCells (MatrixCell[][] matrixCells) {
        this.matrixCells = matrixCells;
    }
    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }
    public Matrix getMatrix() {
        return matrix;
    }

    public void update (int currentStep) {
        setCurrentStep(currentStep);
    }
}
