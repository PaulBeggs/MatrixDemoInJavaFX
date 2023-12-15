package matrix.model;

import javafx.animation.Animation;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
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
    private MatrixView matrixView;
    private Animation animation;
    private int currentStep;
    private List<Integer> signChanges;
    private int sign = 1;
    private BigDecimal determinant;

    public TriangularizationView(Matrix matrix) {
        this.matrix = matrix;
        this.signChanges = new ArrayList<>();
    }

    public BigDecimal getDeterminantValue() {
        return determinant;
    }
    public void setDeterminantValue(BigDecimal determinant) {
        this.determinant = determinant;
    }
    public void setMatrixView(MatrixView matrixView) {
        this.matrixView = matrixView;
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
    public void setMatrixGrid(GridPane matrixGrid) {
        this.matrixGrid = matrixGrid;
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
    public void setMatrixTextFields(List<List<TextField>> matrixTextFields) {
        this.matrixTextFields = matrixTextFields;
    }
    public void update (int currentStep) {
        setCurrentStep(currentStep);
    }
}
