package matrix.model;

import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import matrix.fileManaging.FilePath;
import matrix.fileManaging.MatrixFileHandler;
import matrix.fileManaging.MatrixType;
import matrix.operators.MatrixDeterminantOperations;
import matrix.operators.MatrixInputHandler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TriangularizationView {
    ProgressBar progressBar;
    TextField signTextField;
    GridPane matrixGrid = new GridPane();
    private Matrix tMatrix;
    private List<List<TextField>> matrixTextFields;
    private TriangularizationView tV;
    private Stage stage;
    private MatrixDeterminantOperations MDO;
    private MatrixInputHandler MIP = new MatrixInputHandler();
    private int currentStep;
    private int sign = 1;
    private int numRows = 0, numCols = 0;
    private List<Integer> signChanges;
    private BigDecimal determinant;
    private String filePath = FilePath.TRIANGULAR_PATH.getPath();

    public TriangularizationView(Matrix tMatrix, GridPane matrixGridFromController, List<List<TextField>> matrixTextFields) {
        this.tMatrix = tMatrix;
        this.matrixTextFields = matrixTextFields;
        this.matrixGrid = matrixGridFromController;
        this.signChanges = new ArrayList<>();
    }

    public void saveToFile() {
        List<List<String>> tMatrixData = new ArrayList<>();
        for (int row = 0; row < tMatrix.getRows(); row++) {
            List<String> rowData = new ArrayList<>();
            for (int col = 0; col < tMatrix.getCols(); col++) {
                rowData.add(String.valueOf(tMatrix.getValue(row, col)));
            }
            tMatrixData.add(rowData);
        }

        System.out.println("tMatrix inside tView: \n" + tMatrixData);
        MatrixFileHandler.saveMatrixDataToFile(FilePath.TRIANGULAR_PATH.getPath(), BigDecimal.valueOf(0), tMatrixData, MatrixType.TRIANGULAR);
    }

    public void updateViews () {
        List<List<String>> matrixData = MatrixFileHandler.loadMatrixFromFile(FilePath.TRIANGULAR_PATH.getPath());
        if (matrixData != null) {

            if (!matrixData.isEmpty() && !matrixData.get(0).isEmpty()) {
                this.tMatrix = new Matrix(matrixData.size(), matrixData.get(0).size());

            } else {
                System.out.println("Error: matrixData is empty.");
            }
            System.out.println("(tView) 2nd matrix data: \n" + matrixData);
            populateMatrixFromData();
            MatrixFileHandler.setMatrix(filePath, tMatrix);
            saveToFile();
        }
    }

    public void populateMatrixFromData() {
        setMatrixTextFields(matrixTextFields);
        matrixGrid.getChildren().clear();
        setMatrixGrid(matrixGrid);

        matrixDimensionWOTextFields();
    }

    private void matrixDimensionWOTextFields() {

        try {
            BufferedReader br = new BufferedReader(new FileReader(FilePath.TRIANGULAR_PATH.getPath()));

            while (br.readLine() != null) {
                numRows++;
            }

            br.close();
            br = new BufferedReader(new FileReader(filePath));

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\\s+");
                numCols = Math.max(numCols, values.length);
            }
            matrixFromFileWOTextFields();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void matrixFromFileWOTextFields() throws IOException {

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            if (numCols == 0) {
                if (numRows == 0) {
                    tMatrix = new Matrix(1, 1);
                }
            } else {
                tMatrix = new Matrix(numRows, numCols);
            }
            for (int row = 0; row < numRows; row++) {

                List<TextField> rowList = createAndAddRow(br.readLine(), row);
                matrixTextFields.add(rowList);
                setMatrixTextFields(matrixTextFields);
            }
        }
    }

    private List<TextField> createAndAddRow(String line, int row) {

        List<TextField> rowList = new ArrayList<>();
        String[] values = line.split("\\s+");

        int rowLength = values.length;

        for (int col = 0; col < rowLength; col++) {
            TextField cell = createAndConfigureCell(values[col], row, col);
            matrixGrid.add(cell, col, row);
            setMatrixGrid(matrixGrid);
            rowList.add(cell);
        }

        return rowList;
    }

    private TextField createAndConfigureCell(String value, int row, int col) {

        TextField cell = new TextField();
        MIP.addNumericValidation(cell);
        cell.setMinHeight(50);
        cell.setMinWidth(50);
        cell.setAlignment(Pos.CENTER);
        cell.setEditable(false);

        cell.setText(value);
        this.tMatrix.setValue(row, col, Double.parseDouble(value));
        MatrixFileHandler.setMatrix(filePath, tMatrix);

        return cell;
    }

    public void updateMatrixFromUI() {
        matrixTextFields = getMatrixTextFields();
        for (int row = 0; row < matrixTextFields.size(); row++) {
            for (int col = 0; col < matrixTextFields.get(row).size(); col++) {
                String textValue = matrixTextFields.get(row).get(col).getText();
                try {
                    System.out.println("These are the numCols and numRows within tView: \n" + col);
                    System.out.println(row);
                    double numericValue = Double.parseDouble(textValue);
                    tMatrix.setValue(row, col, numericValue);
                } catch (NumberFormatException e) {
                    e.getMessage();
                }
            }
        }
    }

    public List<List<String>> parseMatrixData (Matrix matrix) {
        List<List<String>> matrixData = new ArrayList<>();
        for (int row = 0; row < matrix.getRows(); row++) {
            List<String> rowData = new ArrayList<>();
            for (int col = 0; col < matrix.getCols(); col++) {
                rowData.add(String.valueOf(matrix.getValue(row, col)));
            }
            matrixData.add(rowData);
        }
        return matrixData;
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
    public void setMatrixGrid(GridPane matrixGrid) {
        this.matrixGrid = matrixGrid;
    }
    public List<List<TextField>> getMatrixTextFields() {
        return matrixTextFields;
    }
    public Matrix gettMatrix() {
        return tMatrix;
    }
    public void setMatrixTextFields(List<List<TextField>> matrixTextFields) {
        this.matrixTextFields = matrixTextFields;
    }
    public void update (int currentStep) {
        setCurrentStep(currentStep);
    }
}
