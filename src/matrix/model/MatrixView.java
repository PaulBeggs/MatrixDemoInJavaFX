package matrix.model;

import javafx.geometry.Pos;
import javafx.scene.control.TextField;

import javafx.scene.layout.GridPane;
import matrix.gui.FilePath;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MatrixView {
    private Matrix matrix;
    private List<List<TextField>> matrixTextFields;
    private GridPane matrixGrid;
    private TextField sizeRowsField, sizeColsField;

    public MatrixView(Matrix matrix, GridPane matrixGridFromController, List<List<TextField>> matrixTextFields) {
        this.matrix = matrix;
        this.matrixGrid = matrixGridFromController;
        this.matrixTextFields = matrixTextFields;
    }

    public void updateViews (String fileName, boolean isEditable) {
        List<List<String>> matrixData = MatrixFileHandler.loadMatrixFromFile(fileName);
        if (matrixData != null) {
            if (!matrixData.isEmpty() && !matrixData.get(0).isEmpty()) {
                this.matrix = new Matrix(matrixData.size(), matrixData.get(0).size());
            } else {
                System.out.println("Error: matrixData is empty.");
            }
            System.out.println("2nd matrix data: \n" + matrixData);
            populateMatrixFromData(fileName, isEditable);
            MatrixFileHandler.setMatrix(fileName, matrix);
        }
    }

    public void populateMatrixFromData(String filePath, boolean isEditable) {
        try {
            setMatrixTextFields(matrixTextFields);
            matrixGrid.getChildren().clear();
            setMatrixGrid(matrixGrid);
            if (sizeRowsField != null && sizeColsField != null) {
                determineMatrixDimensions(filePath);
                createMatrixFromFile(filePath, isEditable);
            } else {
                int numRows = 0, numCols = 0;
                matrixDimensionWOTextFields(numRows, numCols, filePath, isEditable);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void determineMatrixDimensions(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            sizeRowsField.setText(String.valueOf(0));
            sizeColsField.setText(String.valueOf(0));

            String firstLine = br.readLine();
            if (firstLine != null) {
                String[] values = firstLine.split("\\s+");
                sizeColsField.setText(String.valueOf(values.length));
                System.out.println("Cols field set to: " + sizeColsField.getText());
            }

            int i = 1;
            while (br.readLine() != null) {
                i++;
            }
            sizeRowsField.setText(String.valueOf(i));
            System.out.println("Rows field set to: " + sizeRowsField.getText());
            setSizeRowsField(sizeRowsField);
            setSizeColsField(sizeColsField);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void matrixDimensionWOTextFields(int numRows, int numCols, String filePath, boolean isEditable) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(FilePath.MATRIX_PATH.getPath()));

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
            matrixFromFileWOTextFields(numRows, numCols, filePath, isEditable);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createMatrixFromFile(String filePath, boolean isEditable) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            System.out.println("createMatrixFromFile dimensions: \n" + Integer.parseInt(getSizeRowsField().getText()));
            System.out.println(Integer.parseInt(getSizeColsField().getText()));
            matrix = new Matrix(Integer.parseInt(getSizeRowsField().getText()), Integer.parseInt(getSizeColsField().getText()));
            for (int row = 0; row < Integer.parseInt(sizeRowsField.getText()); row++) {
                List<TextField> rowList = createAndAddRow(br.readLine(), row, isEditable);
                matrixTextFields.add(rowList);
            }
            MatrixFileHandler.setMatrix(FilePath.MATRIX_PATH.getPath(), matrix);
        }
    }

    private void matrixFromFileWOTextFields(int numRows, int numCols, String filePath, boolean isEditable) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            matrix = new Matrix(numRows, numCols);
            for (int row = 0; row < numRows; row++) {
                List<TextField> rowList = createAndAddRow(br.readLine(), row, isEditable);
                matrixTextFields.add(rowList);
            }
            MatrixFileHandler.setMatrix(filePath, matrix);
        }
    }

    private List<TextField> createAndAddRow(String line, int row, boolean isEditable) {
        List<TextField> rowList = new ArrayList<>();
        String[] values = line.split("\\s+");

        int rowLength = values.length;

        for (int col = 0; col < rowLength; col++) {
            TextField cell = createAndConfigureCell(values[col], row, col, isEditable);
            matrixGrid.add(cell, col, row);
            rowList.add(cell);
        }

        return rowList;
    }

    private TextField createAndConfigureCell(String value, int row, int col, boolean isEditable) {
        TextField cell = new TextField();
        cell.setMinHeight(50);
        cell.setMinWidth(50);
        cell.setAlignment(Pos.CENTER);
        cell.setEditable(isEditable);

        cell.setText(value);
        this.matrix.setValue(row, col, Double.parseDouble(value));

        cell.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                cell.setText(newValue.replaceAll("[^\\d.]", ""));
            }
        });

        return cell;
    }

    public void updateMatrixFromUI() {
        for (int row = 0; row < matrixTextFields.size(); row++) {
            for (int col = 0; col < matrixTextFields.get(row).size(); col++) {
                String textValue = matrixTextFields.get(row).get(col).getText();
                try {
                    double numericValue = Double.parseDouble(textValue);
                    matrix.setValue(row, col, numericValue);
                } catch (NumberFormatException ignored) {}
            }
        }
    }

    public void saveToFile(String matrixFileName) {
        List<List<String>> matrixData = new ArrayList<>();
        for (int row = 0; row < matrix.getRows(); row++) {
            List<String> rowData = new ArrayList<>();
            for (int col = 0; col < matrix.getCols(); col++) {
                rowData.add(String.valueOf(matrix.getValue(row, col)));
            }
            matrixData.add(rowData);
        }
        //System.out.println("after saveToFile inside matrixView");
        //textFieldToString(matrixTextFields);
        MatrixFileHandler.saveMatrixToFile(matrixFileName, matrixData);
    }



    public TextField getSizeColsField() {
        return sizeColsField;
    }

    public TextField getSizeRowsField() {
        return sizeRowsField;
    }
    public void setSizeColsField(TextField sizeColsField) {
        this.sizeColsField = sizeColsField;
    }

    public void setSizeRowsField(TextField sizeRowsField) {
        this.sizeRowsField = sizeRowsField;
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
    public void setMatrixTextFields(List<List<TextField>> matrixTextFields) {
        this.matrixTextFields = matrixTextFields;
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

    public List<List<String>> getMatrixDataFromTextFields() {
        List<List<String>> matrixData = new ArrayList<>();

        for (List<TextField> row : this.matrixTextFields) {
            List<String> rowData = row.stream().map(TextField::getText).collect(Collectors.toList());
            matrixData.add(rowData);
        }

        return matrixData;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("MatrixView [\n");
        int numRows = matrix.getRows();
        int numCols = matrix.getCols();

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                stringBuilder.append(matrix.getValue(row, col)).append("\t");
            }
            stringBuilder.append("\n");
        }

        stringBuilder.append("]");
        return stringBuilder.toString();
    }

//    public void textFieldToString(List<List<TextField>> matrixTextFields) {
//        System.out.println("TextFields:");
//        for (List<TextField> rowList : matrixTextFields) {
//            for (TextField cell : rowList) {
//                System.out.println("TextField: " + cell.getText());
//            }
//        }
//    }
}