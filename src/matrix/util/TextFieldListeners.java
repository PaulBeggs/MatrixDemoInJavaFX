package matrix.util;

import javafx.scene.control.TextField;
import matrix.gui.MatrixApp;
import matrix.model.Matrix;
import matrix.model.MatrixSingleton;

public class TextFieldListeners {
    public static void objectOnlyListener(TextField textField, ObjectType objectType) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            String regex = switch (objectType) {
                case INTEGER -> "-?\\d*"; // Allows negative integers
                case DOUBLE -> "-?\\d*(\\.\\d*)?"; // Allows negative doubles and decimals
            };

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
            if (!newValue.matches("(-?\\d+\\.?\\d*(E-?\\d+)?)|([+\\-*^/()])")) {
                textField.setText(newValue.replaceAll("[^\\d.\\-+*/^()]", ""));
            } else {
                try {
                    String valueToSet = newValue.isEmpty() ? "0" : newValue;
                    matrix.setValue(row, col, valueToSet); // Update the Matrix object
                    System.out.println("Did the matrix update properly? \n" + matrix);
                    MatrixSingleton.saveMatrix();
                } catch (NumberFormatException e) {
                    textField.setText(oldValue);
                }
            }
        });
    }


    private static String calculateExpression(String inputField) {
        String result;
        if (MatrixApp.isFractionMode()) {
            result = String.valueOf(MatrixUtil.convertDecimalToFraction(String.valueOf(ExpressionEvaluator.evaluate(inputField))));
        } else {
            result = MatrixUtil.convertFractionToDecimalString(String.valueOf(ExpressionEvaluator.evaluate(inputField)));
        }
        return result;
    }
}
