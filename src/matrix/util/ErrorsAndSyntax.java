package matrix.util;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

import java.util.List;
import java.util.stream.Collectors;

public class ErrorsAndSyntax {
    public static void printSyntaxError(String expression, String errorIndicator) {
        String message = expression + "\n" + errorIndicator;
        showErrorPopup("Syntax Error: Unrecognized input:\n" + message);
    }

    public static void printSyntaxError(String expression, int errorIndex, String errorMessage) {
        String message = expression + "\n" +
                " ".repeat(Math.max(0, errorIndex)) +
                "^";
        showErrorPopup(errorMessage + " at position " + (errorIndex + 1) + ":\n" + message);
    }

    public static void printSyntaxError(List<TokenInfo> tokens, int errorIndex, String errorMessage) {
        // Reconstruct the expression with spaces
        String expression = tokens.stream()
                .map(TokenInfo::getToken)
                .collect(Collectors.joining(" "));

        // Calculate the position of the error, accounting for spaces
        int errorPosition = 0;
        for (int i = 0; i < errorIndex; i++) {
            // Add the length of the token and the space after it
            errorPosition += tokens.get(i).getToken().length() + 1;
        }

        // Create the error indicator string
        String indicator = " ".repeat(Math.max(0, errorPosition)) + "^";

        // Show the error message
        showErrorPopup(errorMessage + " at position " + (errorPosition + 1) + ":\n" + expression + "\n" + indicator);
    }

    public static StringBuilder getErrorBuilder(String expression, List<TokenInfo> tokens) {
        int lastParsedIndex = 0;
        StringBuilder errorIndicator = new StringBuilder(" ".repeat(expression.length()));

        for (TokenInfo token : tokens) {
            int startIndexOfToken = token.getStartIndex();

            // Process the segment between lastParsedIndex and startIndexOfToken
            for (int i = lastParsedIndex; i < startIndexOfToken; i++) {
                if (expression.charAt(i) != ' ') {
                    errorIndicator.setCharAt(i, '^');
                }
            }

            lastParsedIndex = startIndexOfToken + token.getToken().length();
        }

        // Check for any remaining unparsed segment at the end of the expression
        for (int i = lastParsedIndex; i < expression.length(); i++) {
            if (expression.charAt(i) != ' ') {
                errorIndicator.setCharAt(i, '^');
            }
        }
        return errorIndicator;
    }


    public static void showErrorPopup(String prompt) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);

        TextArea textArea = new TextArea(prompt);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setFont(javafx.scene.text.Font.font("Monospaced"));

        // Calculate the size of the TextArea based on the length of the prompt
        int rows = Math.min(prompt.split("\n").length, 10) + 1; // Limit the number of rows
        int columns = Math.min(prompt.length(), 80); // Limit the number of columns

        textArea.setPrefRowCount(rows);
        textArea.setPrefColumnCount(columns);

        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }
}
