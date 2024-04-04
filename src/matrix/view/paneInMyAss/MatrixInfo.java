package matrix.view.paneInMyAss;

import matrix.model.Matrix;

public class MatrixInfo {
    private Matrix matrix;
    private String label;

    public MatrixInfo(Matrix matrix, String label) {
        this.matrix = matrix;
        this.label = label;
    }

    // Getter and Setter for matrix
    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    // Getter and Setter for label
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    // Method to represent the MatrixInfo state as a String
    @Override
    public String toString() {
        return "MatrixInfo{" +
                "matrix=" + matrix +
                ", label='" + label + '\'' +
                '}';
    }
}

