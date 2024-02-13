package matrix.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static matrix.util.ErrorsAndSyntax.printSyntaxError;
import static matrix.util.ErrorsAndSyntax.showErrorPopup;

public class ExpressionEvaluator {

    /*
    String regex = "(-?\\d+\\.?\\d*(E-?\\d+)?)|([+\\-*^%/()])|(sqrt)";

    (-?\\d+\\.?\\d*(E-?\\d+)?): This part matches numbers in scientific notation.

    -?: Optional negative sign.
    \\d+: One or more digits.
    \\.?: Optional decimal point.
    \\d*: Zero or more digits after the decimal point.
    (E-?\\d+)?: Optional scientific notation part (E-notation), where
      E can be followed by an optional negative sign and one or more digits.


    ([+\\-*^%/()]): This part matches common arithmetic operators and parentheses.

    +\\-*^/(): Matches one of the characters: +, -, *, /, ^, (, ), %.
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

    private static boolean shownError = false;

    private static List<TokenInfo> tokenizeExpression(String expression) {
//        // System.out.println("Expression inside 'tokenize': " + expression);
        List<TokenInfo> tokens = new ArrayList<>();
        if (expression.isEmpty() && !shownError) {
            shownError = true;
            String errorMessage = "Syntax error: Empty string";
            printSyntaxError(expression, 0, errorMessage);
            tokens.add(new TokenInfo("", 0));
            return tokens;
        }
        expression = expression.replaceAll("(?<!\\d)\\.", "0.");

        String regex = "(-?\\d+\\.?\\d*(E-?\\d+)?)|([+\\-*^%/()])|(sqrt)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(expression);

        while (matcher.find()) {
            tokens.add(new TokenInfo(matcher.group(), matcher.start()));
        }

        // Process tokens to combine adjacent number tokens
        List<TokenInfo> processedTokens = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            TokenInfo currentToken = tokens.get(i);
            if (i < tokens.size() - 1 && isNumeric(currentToken.getToken()) && isNumeric(tokens.get(i + 1).getToken())) {
                // Combine current number with the next number
                String combinedNumber = currentToken.getToken() + tokens.get(i + 1).getToken();
                processedTokens.add(new TokenInfo(combinedNumber, currentToken.getStartIndex()));
                i++; // Skip the next token as it's now combined
            } else {
                processedTokens.add(currentToken);
            }
        }

        StringBuilder errorIndicator = ErrorsAndSyntax.getErrorBuilder(expression, processedTokens);

        if (!errorIndicator.toString().trim().isEmpty() && !shownError) {
            shownError = true;
            // System.out.println("Error indicator tripped in Tokenize");
            printSyntaxError(expression, errorIndicator.toString());
            throw new IllegalArgumentException();
//            processedTokens.add(new TokenInfo("", 0));
        }
//        // System.out.println("Tokens: " + processedTokens);
        return processedTokens;
    }

    private static void handleModulus(int index, List<TokenInfo> newTokens, List<String> processedTokens) {
        // Check if the modulus operation is at the start or end of the token list
        if (index == 0 || index == newTokens.size() - 1 && !shownError) {
            shownError = true;
            printSyntaxError(newTokens, index, "Invalid expression: Modulus requires two operands");
            throw new IllegalArgumentException("Invalid expression: Modulus requires two operands");
        }
        try {
            double operand1 = Double.parseDouble(processedTokens.removeLast());
            double operand2 = Double.parseDouble(newTokens.get(index + 1).getToken());

            if (operand2 == 0 && !shownError) {
                shownError = true;
                printSyntaxError(newTokens, index, "Math error: Division by zero");
                throw new ArithmeticException("Division by zero in modulus operation");
            }

            double result = operand1 % operand2;
            processedTokens.add(String.valueOf(result));
//            // System.out.println("Processed tokens after modulus: " + processedTokens);

            // Remove the operand token that has been processed
            newTokens.remove(index + 1);

        } catch (NumberFormatException e) {
            shownError = true;
            printSyntaxError(newTokens, index, "Invalid number format");
            throw new IllegalArgumentException("Invalid number format in modulus operation");
        }
    }


    private static void handleExponentiation(int index, List<TokenInfo> newTokens, List<String> processedTokens) {
        // System.out.println("before entering the ring of exponentiation: \n" + processedTokens + "\n newTokens: \n" + newTokens);
        // For the case of left to right exponentiation, you must include a parenthesis around the final exponent that you want to use.
        // e.g., 2 ^ 2 ^ 3 -> 2 ^ (2 ^ 3)
        // Check if the exponentiation operation is at the start or end of the token list
        if (index == 0 || index >= newTokens.size() - 1 && !shownError) {
            shownError = true;
            printSyntaxError(newTokens, index, "Invalid expression: Exponentiation requires two operands");
            throw new IllegalArgumentException("Invalid expression: Exponentiation requires two operands");
        }
        try {
            double base = Double.parseDouble(processedTokens.removeLast());
            double exponent = Double.parseDouble(newTokens.get(index + 1).getToken());
            int nextIndex = index + 1; // Start from the first exponent

            // Adjust loop to process chained exponentiation correctly
            while (nextIndex + 1 < newTokens.size() && newTokens.get(nextIndex).getToken().equals("^")) {
                exponent = Math.pow(exponent, Double.parseDouble(newTokens.get(nextIndex + 1).getToken()));
                // Remove the "^" and its right-hand operand as they are processed
                newTokens.remove(nextIndex); // Remove "^"
                newTokens.remove(nextIndex); // Remove the operand right of "^"
            }

            double result = Math.pow(base, exponent);
            processedTokens.add(String.valueOf(result));

            // No need to manually remove tokens here as they are already handled in the loop
        } catch (NumberFormatException e) {
            if (!shownError) {
                shownError = true;
                printSyntaxError(newTokens, index, "Invalid number format");
            }
            throw new IllegalArgumentException("Invalid number format in exponentiation");
        }
    }


    private static void handleSquareRoot(int index, List<TokenInfo> newTokens, List<String> processedTokens) {
        // Check if sqrt is used correctly
        if (index == newTokens.size() - 1 && !shownError) {
            shownError = true;
            printSyntaxError(newTokens, index, "Invalid sqrt usage in token: ");
            throw new IllegalArgumentException("Invalid sqrt usage in token: ");
        }

        String sqrtArgument = newTokens.get(index + 1).getToken();

        // Check if sqrt argument is an expression, and if so, evaluate it
        try {
            if (isExpression(sqrtArgument)) {
                sqrtArgument = String.valueOf(evaluate(sqrtArgument));
            }

            // Check if the argument is numeric
            if (!isNumeric(sqrtArgument) && !shownError) {
                shownError = true;
                printSyntaxError(newTokens, index, "Invalid sqrt argument: " + sqrtArgument);
                throw new IllegalArgumentException("Invalid sqrt argument: " + sqrtArgument);
            }

            double operand = Double.parseDouble(sqrtArgument);
            if (operand < 0 && !shownError) {
                shownError = true;
                showErrorPopup("This calculator does not compute imaginary numbers at this time.");
                // System.out.println("This calculator does not compute imaginary numbers at this time.");
                processedTokens.add("0");
            } else {
                double sqrtResult = Math.sqrt(operand);
                processedTokens.add(String.valueOf(sqrtResult));
            }
        } catch (NumberFormatException e) {
            if (!shownError) {
                shownError = true;
                printSyntaxError(newTokens, index, "Invalid number format in sqrt");
            }
            throw new IllegalArgumentException("Invalid number format in sqrt");
        }
    }


    private static void handleMultiplicationOrDivision(String token, int index, List<TokenInfo> newTokens, List<String> processedTokens) {
        try {
            // Check if the operation is at the start or end of the token list
            if (index == 0 || index == newTokens.size() - 1 && !shownError) {
                shownError = true;
                if (Objects.equals(token, "/")) {
                    ErrorsAndSyntax.showErrorPopup("Missing operands for division.");
                } else if (Objects.equals(token, "*")) {
                    ErrorsAndSyntax.showErrorPopup("Missing operands for multiplication.");
                }
                throw new IllegalArgumentException("Invalid expression: " + token);
            }

            double operand1 = Double.parseDouble(processedTokens.removeLast());
            double operand2 = Double.parseDouble(newTokens.get(index + 1).getToken());
            double result = token.equals("*") ? operand1 * operand2 : operand1 / operand2;
            processedTokens.add(String.valueOf(result));
        } catch (NumberFormatException e) {
            String errorMessage = (index == 0 || index == newTokens.size() - 1) ?
                    "Invalid expression: " + token : "Invalid number format in multiplication/division";
            if (!shownError) {
                shownError = true;
                ErrorsAndSyntax.printSyntaxError(newTokens, index, errorMessage);
            }
            throw new IllegalArgumentException(errorMessage, e);
        }
    }

    private static String initialOperations(String expression) {
//        // System.out.println("Expression inside 'initialOperations': " + expression);
        List<TokenInfo> newTokens = tokenizeExpression(expression);
        List<String> processedTokens = new ArrayList<>();

        for (int i = 0; i < newTokens.size(); i++) {
            TokenInfo tokenInfo = newTokens.get(i);
            String token = tokenInfo.getToken();

            // Error detection:
            if (isOperator(token) && i < newTokens.size() - 1 && isOperator(newTokens.get(i + 1).getToken()) && !shownError) {
                int chainLength = 1;
                int j = i + 1;
                while (j < newTokens.size() && isOperator(newTokens.get(j).getToken())) {
                    chainLength += newTokens.get(j).getToken().length(); // Add the length of each consecutive operator
                    j++;
                }
                shownError = true;
                String errorMessage = "Syntax error: Duplicate consecutive operators starting";
                printSyntaxError(expression, tokenInfo.getStartIndex(), chainLength, errorMessage);
            }

            switch (token) {
                case "sqrt" -> {
                    handleSquareRoot(i, newTokens, processedTokens);
                    i++;
                    // System.out.println("After sqrt case. \n" + processedTokens + "\n newTokens: \n" + newTokens);
                }
                case "^" -> {
                    handleExponentiation(i, newTokens, processedTokens);
                    i++;
                    // System.out.println("After exponential case. \n" + processedTokens + "\n newTokens: \n" + newTokens);
                }
                case "%" -> {
                    handleModulus(i, newTokens, processedTokens);
                    i++;
                    // System.out.println("After modulus case. \n" + processedTokens + "\n newTokens: \n" + newTokens);
                }
                case "*", "/" -> {
                    handleMultiplicationOrDivision(token, i, newTokens, processedTokens);
                    i++;
                    // System.out.println("After multiplication or division cases. \n" + processedTokens + "\n newTokens: \n" + newTokens);
                }
                default -> {
                    // System.out.println("Adding token in default call: \n" + token);
                    processedTokens.add(token);
                }
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

    private static String insertImplicitMultiplication(String expression) {
        expression = expression.replaceAll("(\\d)\\(", "$1*("); // Insert * between a digit and (
        expression = expression.replaceAll("\\)(\\d)", ")*$1"); // Insert * between ) and a digit
        expression = expression.replaceAll("\\)\\(", ")*(");    // Insert * between ) and (
        return expression;
    }

    private static double evaluateAdditionSubtraction(String expression) {
        String[] tokens = expression.split(" ");
        double result = 0.0;
        char operation = '+';

//        // System.out.println("Tokens: " + Arrays.toString(tokens));
//        // System.out.println("Tokens length = " + tokens.length);

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
        shownError = false;
        expression = insertImplicitMultiplication(expression);

        while (expression.contains("(")) {
            // System.out.println("Handling parenthesis: " + expression);
            int open = expression.lastIndexOf('(');
            int close = expression.indexOf(')', open);

            if (close == -1) {
                shownError = true;
                String errorMessage = "Syntax error: Opening parenthesis with no closing parenthesis";
                printSyntaxError(expression, open, errorMessage);
            }
            double result = evaluate(expression.substring(open + 1, close));
            expression = expression.substring(0, open) + result + expression.substring(close + 1);
            // System.out.println("Expression after handling parenthesis: " + expression);
        }

        // System.out.println("Expression before initial operations: " + expression);
        expression = initialOperations(expression);

        // System.out.println("Expression before addition/subtraction: " + expression);
        finalExpression = evaluateAdditionSubtraction(expression);

        // System.out.println("Final Expression: " + finalExpression);
        return finalExpression;
    }
}
