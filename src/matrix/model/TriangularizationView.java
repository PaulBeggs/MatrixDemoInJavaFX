package matrix.model;

import javafx.animation.Animation;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import matrix.fileManaging.FilePath;
import matrix.fileManaging.MatrixFileHandler;
import matrix.operators.MatrixDeterminantOperations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TriangularizationView {
    ProgressBar progressBar;
    TextField signTextField;
    GridPane matrixGrid = new GridPane();
    private Matrix matrix;
    private List<List<TextField>> matrixTextFields;
    private TriangularizationView tV;
    private Stage stage;
    private MatrixDeterminantOperations MDO;
    private Animation animation;
    private int currentStep;
    private List<Integer> signChanges;
    private int sign = 1;
    private BigDecimal determinant;

    public TriangularizationView(Matrix matrix) {
        this.matrix = matrix;
        this.signChanges = new ArrayList<>();
    }

    public void setMatrixGrid(GridPane matrixGrid) {
        this.matrixGrid = matrixGrid;
    }

    public void setMatrixTextFields(List<List<TextField>> matrixTextFields) {
        this.matrixTextFields = matrixTextFields;
    }

    public void updateViews() {
        List<List<String>> matrixData = MatrixFileHandler.loadMatrixFromFile(FilePath.MATRIX_PATH.getPath());
        if (matrixData != null) {

            if (!matrixData.isEmpty() && !matrixData.getFirst().isEmpty()) {
                this.matrix = new Matrix(matrixData.size(), matrixData.getFirst().size());

            } else {
                System.out.println("Error: matrixData is empty");

            }
            System.out.println("matrix data inside ");
        }
    }

    public void updateMatrixFromUI() {
        matrixTextFields = getMatrixTextFields();
        for (int row = 0; row < matrixTextFields.size(); row++) {
            for (int col = 0; col < matrixTextFields.get(row).size(); col++) {
                String textValue = matrixTextFields.get(row).get(col).getText();
                try {
//                    System.out.println("These are the numCols and numRows within MatrixView: \n" + col);
//                    System.out.println(row);
                    double numericValue = Double.parseDouble(textValue);
                    matrix.setValue(row, col, numericValue);
                } catch (NumberFormatException e) {
                    e.getMessage();
                }
            }
        }
    }

    public BigDecimal getDeterminantValue() {
        return determinant;
    }
    public void setDeterminantValue(BigDecimal determinant) {
        this.determinant = determinant;
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

    public List<List<TextField>> getMatrixTextFields() {
        return matrixTextFields;
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
