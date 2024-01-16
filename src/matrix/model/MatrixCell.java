package matrix.model;

import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import matrix.gui.MatrixApp;
import matrix.utility.BigDecimalUtil;

import java.math.BigDecimal;

public class MatrixCell {
    private final TextField textField;
    private final int row;
    private final int col;
    private final Matrix matrix;
    private boolean override = false;

    public MatrixCell(int row, int col, String initialValue, boolean isEditable) {
        this.row = row;
        this.col = col;
        this.matrix = MatrixSingleton.getDisplayInstance(); // Get the shared Matrix instance
//        System.out.println("This is the matrix from the singleton inside of matrixcell: \n" + matrix);
        this.textField = new TextField(initialValue);
        setupTextField(isEditable);
    }


    private void setupTextField(boolean isEditable) {
        textField.getStyleClass().add("textfield-grid-cell");
        textField.setEditable(isEditable);

        // Listener for input validation and handling
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Allow digits, decimal points, slashes (for fractions), square root expressions, and negative signs
            if (!newValue.matches("[-\\d./sqrt()]*")) {
                textField.setText(oldValue); // Revert to the old value if input is invalid
            }
        });
        // Key event listener for processing the input when the ENTER key is pressed
        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String input = textField.getText();
                processInput(input); // Handle input without overriding square root
            }
        });

        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Focus lost
                String input = textField.getText();
                processInput(input); // Handle input with override for square root
            }
        });
    }

    private void processInput(String input) {
        try {
            BigDecimal numericValue;
            if (input.startsWith("/sqrt(")) {
                this.override = true;
                numericValue = BigDecimalUtil.calculateSquareRoot(input);
            } else if (input.contains("/")) {
                numericValue = BigDecimalUtil.convertFractionToDecimal(input);
            } else {
                numericValue = new BigDecimal(input);
            }
            updateMatrixAndCell(numericValue);
        } catch (Exception e) {
            textField.setText("0"); // Reset cell value on error
            updateMatrixAndCell(BigDecimal.ZERO);
            System.out.println("Error processing input: " + input);
        }
    }

    private void updateMatrixAndCell(BigDecimal value) {
        BigDecimalMatrix computationalMatrix = MatrixSingleton.getComputationalInstance();

        String displayValue = this.override ? BigDecimalUtil.convertBigDecimalToString(value)
                : BigDecimalUtil.formatValueBasedOnMode(value);
        this.override = false;

        textField.setText(displayValue);
        matrix.setValue(getRow(), getCol(), displayValue); // Setting as String
        computationalMatrix.setValue(getRow(), getCol(), value); // Setting as BigDecimal
        MatrixSingleton.saveMatrix();

        System.out.println("Computational matrix: \n" + computationalMatrix);
        System.out.println("Display matrix: \n" + matrix);
    }

    public TextField getTextField() {return textField;}
    public int getRow() {return row;}
    public int getCol() {return col;}
}
