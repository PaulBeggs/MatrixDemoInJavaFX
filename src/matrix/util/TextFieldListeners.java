package matrix.util;

import javafx.scene.control.TextField;
import matrix.gui.MatrixApp;
import matrix.model.Matrix;
import matrix.model.MatrixSingleton;

public class TextFieldListeners {
    public static void objectOnlyListener(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            String regex = "\\d*"; // Allows positive integers

            if (!newValue.matches(regex)) {
                // The newValue is not a valid format, so don't change the text field.
                // This is to prevent invalid inputs like "--" or "3.-" or multiple dots.
                if (newValue.isEmpty()) {
                    textField.setText(""); // If the new value is empty, allow it to clear the field
                } else {
                    textField.setText(oldValue); // Otherwise, revert to the old value
                }
            }
        });
    }

    public static void addEnterListener(TextField textField) {
        textField.setOnAction(event -> {
            String text = calculateExpression(textField.getText());
            textField.setText(text);
        });
    }

    public static void addEnterListener(TextField textField, Matrix matrix, int row, int col) {
        textField.setOnAction(event -> {
            String text = calculateExpression(textField.getText());
            textField.setText(text);
            matrix.setValue(row, col, text);
            MatrixSingleton.saveMatrix();
        });
    }

    public static void addTextPropertyListener(TextField textField, Matrix matrix, int row, int col) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Validate the input to allow only numbers, decimals, negative signs, and mathematical expressions
            if (!newValue.matches("([-+]?\\d*(\\.\\d*)?)|(\\s*)|(/sqrt\\([-+]?\\d+\\))|([+\\-*/^%()])"
            ) && !newValue.isEmpty()) {
                textField.setText(newValue.replaceAll("[^\\d.\\-+*/^%sqrt()\\s]", ""));
            } else {
                try {
                    // Check if the newValue is a valid numerical value before setting it to the matrix
                    if (newValue.matches("[-+]?\\d*\\.?\\d+")) {
                        matrix.setValue(row, col, newValue); // Update the Matrix object
                        System.out.println("Matrix updated with value: \n" + matrix);
                        MatrixSingleton.saveMatrix();
                    }
                } catch (NumberFormatException e) {
                    textField.setText(oldValue);
                }
            }
        });
    }

    private static String calculateExpression(String inputField) {
        return MatrixApp.isFractionMode()
                ? String.valueOf(MatrixUtil.convertDecimalToFraction((inputField)))
                : MatrixUtil.convertFractionToDecimalString((inputField));
    }
}
