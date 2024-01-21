package matrix.util;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionEvaluator {

    /*
    (-?\\d+\\.?\\d*(E-?\\d+)?): This part matches numbers in scientific notation.

    -?: Optional negative sign.
    \\d+: One or more digits.
    \\.?: Optional decimal point.
    \\d*: Zero or more digits after the decimal point.
    (E-?\\d+)?: Optional scientific notation part (E-notation), where
      E can be followed by an optional negative sign and one or more digits.


    ([+\\-*^/()]): This part matches common arithmetic operators and parentheses.

    +\\-*^/(): Matches one of the characters: +, -, *, /, ^, (, ).
    (sqrt): This part matches the specific string "sqrt".


    Together, this regex is designed to match numbers in various formats
    (including scientific notation) and common arithmetic operators,
    along with the specific string "sqrt".

    --------------------------------------------------------------------
    (?<!\\d)\\": Finds any decimal that is missing a digit before it and adds a 0 placeholder.

    (?<!\\d): Negative lookbehind assertion, ensuring that there is no digit
      before the double quote.
    \\: This matches a literal backslash.


    This regex is used to find double quotes that are not preceded by a digit.
      The (?<!\\d) part ensures that the double quote is not part of a number.
     */

    private static List<TokenInfo> tokenizeExpression(String expression) {
//        System.out.println("Expression inside 'tokenize': " + expression);
        List<TokenInfo> tokens = new ArrayList<>();
        if (expression.isEmpty()) {
            String errorMessage = "Syntax error: Empty string";
            printSyntaxError(expression, 0, errorMessage);
            tokens.add(new TokenInfo("", 0));
            return tokens;
        }
        expression = expression.replaceAll("(?<!\\d)\\.", "0.");

        String regex = "(-?\\d+\\.?\\d*(E-?\\d+)?)|([+\\-*^/()])|(sqrt)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(expression);

        while (matcher.find()) {
            tokens.add(new TokenInfo(matcher.group(), matcher.start()));
        }
        StringBuilder errorIndicator = getErrorBuilder(expression, tokens);

        if (!errorIndicator.toString().trim().isEmpty()) {
            printSyntaxError(expression, errorIndicator.toString());
            tokens.add(new TokenInfo("", 0));
        }
        return tokens;
    }

    private static StringBuilder getErrorBuilder(String expression, List<TokenInfo> tokens) {
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

    private static void handleExponentiation(int index, List<TokenInfo> newTokens, List<String> processedTokens) {
//        System.out.println("Tokens inside 'Exponentiation': " + newTokens);
        if (index == 0 || index == newTokens.size() - 1) {

            throw new IllegalArgumentException("Invalid expression: Exponentiation requires two operands");
        }
        double base = Double.parseDouble(processedTokens.removeLast());
        double exponent = Double.parseDouble(newTokens.get(index + 1).getToken());
        double result = Math.pow(base, exponent);
        processedTokens.add(String.valueOf(result));
        System.out.println("Processed tokens after exponentiation: " + processedTokens);
    }

    private static void handleSquareRoot(int index, List<TokenInfo> newTokens, List<String> processedTokens) {
//        System.out.println("Tokens inside 'Square Root': " + newTokens);
        if (index == newTokens.size() - 1) {
            throw new IllegalArgumentException("Invalid sqrt usage in token: ");
        }
        String sqrtArgument = newTokens.get(index + 1).getToken();
        if (isExpression(sqrtArgument)) {
            sqrtArgument = String.valueOf(evaluate(sqrtArgument));
        }
        if (!isNumeric(sqrtArgument)) {
            throw new IllegalArgumentException("Invalid sqrt argument: " + sqrtArgument);
        }
        double operand = Double.parseDouble(sqrtArgument);
        if (operand < 0) {
            System.out.println("This calculator does not compute imaginary numbers at this time.");
            processedTokens.add("0");
        } else {
            double sqrtResult = Math.sqrt(operand);
            processedTokens.add(String.valueOf(sqrtResult));
        }

    }


    private static void handleMultiplicationOrDivision(String token, int index, List<TokenInfo> newTokens, List<String> processedTokens) {
//        System.out.println("Tokens inside 'MultiplicationOrDivision': " + newTokens);
        if (index == 0 || index == newTokens.size() - 1) {
            throw new IllegalArgumentException("Invalid expression: " + token);
        }
        double operand1 = Double.parseDouble(processedTokens.removeLast());
        double operand2 = Double.parseDouble(newTokens.get(index + 1).getToken());
        double result = token.equals("*") ? operand1 * operand2 : operand1 / operand2;
        processedTokens.add(String.valueOf(result));
        System.out.println("Processed tokens after multiplication or division: " + processedTokens);
    }


    private static String initialOperations(String expression) {
//        System.out.println("Expression inside 'initialOperations': " + expression);
        List<TokenInfo> newTokens = tokenizeExpression(expression);
        List<String> processedTokens = new ArrayList<>();

        for (int i = 0; i < newTokens.size(); i++) {
            TokenInfo tokenInfo = newTokens.get(i);
            String token = tokenInfo.getToken();

            // Check for consecutive operators
            if (isOperator(token) && i < newTokens.size() - 1 && isOperator(newTokens.get(i + 1).getToken())) {
                String errorMessage = "Syntax error: Duplicate consecutive operators starting";
                printSyntaxError(expression, tokenInfo.getStartIndex(), errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }
            switch (token) {
                case "*", "/" -> {
                    handleMultiplicationOrDivision(token, i, newTokens, processedTokens);
                    i++;
                }
                case "sqrt" -> {
                    handleSquareRoot(i, newTokens, processedTokens);
                    i++;
                }
                case "^" -> {
                    handleExponentiation(i, newTokens, processedTokens);
                    i++;
                }
                default -> processedTokens.add(token);
            }
        }

        // Reconstruct the expression
        return String.join(" ", processedTokens);
    }

    private static boolean isExpression(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        // Check for arithmetic operators or parentheses
        if (str.matches(".*[+\\-*/()].*")) {
            return true;
        }
        // Check if the string is a number
        try {
            Double.parseDouble(str);
            return false; // It's a number, not an expression
        } catch (NumberFormatException e) {
            // Not a number
            return true;
        }
    }

    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isOperator(String token) {
        return token.equals("*") || token.equals("/") || token.equals("+") || token.equals("-") || token.equals("^");
    }

    private static double evaluateAdditionSubtraction(String expression) {
        String[] tokens = expression.split(" ");
        double result = 0.0;
        char operation = '+';

//        System.out.println("Tokens: " + Arrays.toString(tokens));
//        System.out.println("Tokens length = " + tokens.length);

        for (String token : tokens) {
            if (token.equals("+") || token.equals("-")) {
                operation = token.charAt(0);
            } else {
                try {
                    double value = Double.parseDouble(token);
                    if (operation == '+') {
                        result += value;
                    } else {
                        result -= value;
                    }
                } catch (NumberFormatException e) {
                    return result;
                }
            }
        }

        return result;
    }

    public static double evaluate(String expression) {
        double finalExpression;
        while (expression.contains("(")) {
            System.out.println("Handling parenthesis: " + expression);
            int open = expression.lastIndexOf('(');
            int close = expression.indexOf(')', open);

            // Check for missing closing parenthesis
            if (close == -1) {
                String errorMessage = "Syntax error: Opening parenthesis with no closing parenthesis";
                printSyntaxError(expression, open, errorMessage);
                return 0;
            }
            // Remove the parenthesis, and recursively solve until there are no more parentheses remaining.
            double result = evaluate(expression.substring(open + 1, close));
            expression = expression.substring(0, open) + result + expression.substring(close + 1);
        }
//        System.out.println("Expression inside the evaluate method: " + expression);

        // Handle multiplication, division, and sqrt
        expression = initialOperations(expression);

        // Now handle addition and subtraction
        finalExpression = evaluateAdditionSubtraction(expression);

        System.out.println("Final Expression: " + finalExpression);
        return finalExpression;
    }

    private static void printSyntaxError(String expression, String errorIndicator) {
        String message = expression + "\n" + errorIndicator;
        showErrorPopup("Syntax Error: Unrecognized input:\n" + message);
    }

    private static void printSyntaxError(String expression, int errorIndex, String errorMessage) {
        String message = expression + "\n" +
                " ".repeat(Math.max(0, errorIndex)) +
                "^";
        showErrorPopup(errorMessage + " at position " + (errorIndex + 1) + ":\n" + message);
    }

    private static void showErrorPopup(String prompt) {
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
