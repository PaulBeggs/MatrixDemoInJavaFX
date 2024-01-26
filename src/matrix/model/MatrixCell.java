package matrix.model;

import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import matrix.util.TextFieldListeners;

public class MatrixCell {
    private final TextField textField;
    private final int row;
    private final int col;
    private final Matrix matrix;

    public MatrixCell(int row, int col, String initialValue, boolean isEditable) {
        this.row = row;
        this.col = col;
        this.matrix = MatrixSingleton.getInstance(); // Get the shared Matrix instance
        this.textField = new TextField(initialValue);
        setupTextField(isEditable);
    }

    private void setupTextField(boolean isEditable) {
        textField.getStyleClass().add("textfield-grid-cell");
        textField.setEditable(isEditable);
        TextFieldListeners.addEnterListener(textField, matrix, row, col);
        TextFieldListeners.addTextPropertyListener(textField, matrix, row, col);

        Tooltip tooltip = new Tooltip("Row: " + (row + 1) + ", Col: " + (col + 1));
        textField.setTooltip(tooltip);
    }


    public TextField getTextField() {return textField;}
    public int getRow() {return row;}
    public int getCol() {return col;}
}
