package matrix.operators;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import matrix.model.Matrix;

public class MatrixInputHandler {
    public boolean isPositiveIntValid(TextField textField) {
        try {
            int input = Integer.parseInt(textField.getText());
            if (input > 0) {
                return true;
            } else {
                System.out.println("Input must be greater than 0. Please try again.");
                Platform.runLater(textField::clear);
                return false;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a positive integer.");
            Platform.runLater(textField::clear);
            return false;
        }
    }


    public boolean isDoubleValid(TextField textField) {
        try {
            Double.parseDouble(textField.getText());
            return true;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid double.");
            Platform.runLater(textField::clear);
            return false;
        }
    }

    public boolean isRowValid(TextField textField, int numRows) {
        try {
            int row = Integer.parseInt(textField.getText()) - 1;
            if (row >= 0 && row < numRows) {
                return true;
            } else {
                System.out.println("Invalid row selection. Please choose a valid row.");
                Platform.runLater(textField::clear);
                return false;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid integer.");
            Platform.runLater(textField::clear);
            return false;
        }
    }
}
