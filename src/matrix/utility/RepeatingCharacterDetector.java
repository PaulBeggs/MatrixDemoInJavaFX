package matrix.utility;

import matrix.gui.MatrixApp;

import java.math.BigDecimal;
import java.math.BigInteger;

public class RepeatingCharacterDetector {

    public static void main(String[] args) {
//        String input1 = "aabbbccccddddd";
        String input2 = "13.88888853";
        String input3 = "13.00000053";
        String input4 = "0.333333333";

//        System.out.println(processInput(input1));
        System.out.println(processInput(input2));
        System.out.println(processInput(input3));
        System.out.println(processInput(input4));
    }

    // Method to check if the input has repeating characters
    private static boolean hasRepeatingCharacters(String input) {
        return input.matches(".*(.)(\\1{3,}).*");
    }

    // Method to process the input string based on repeating characters
    private static String processRepeatingCharacters(String input) {
        String repeatingCharacter = input.replaceAll(".*(.)\\1{3,}.*", "$1");
        String substringLeftOfDecimal = input.split("\\.")[0];

        if (repeatingCharacter.equals("9") && input.contains(".")) {
            return incrementSubstring(substringLeftOfDecimal);
        } else if (repeatingCharacter.equals("0") && input.contains(".")) {
            return substringLeftOfDecimal;
        }
        return input;
    }

    // Method to increment a substring
    private static String incrementSubstring(String substring) {
        int incrementedValue = Integer.parseInt(substring) + 1;
        return String.valueOf(incrementedValue);
    }

    // Method to handle fraction mode
    private static String handleFractionMode(String input) {
        BigDecimal stringAsDecimal = convertStringToBigDecimal(input);
        BigInteger num = stringAsDecimal.movePointRight(0).toBigInteger();
        return "Fraction Mode: " + input; // Placeholder return
    }

    // Method to handle decimal mode
    private static String handleDecimalMode(String input) {
        // Implement logic for decimal mode handling
        // This should include rounding of decimals and handling of repeating patterns
        // Placeholder for actual implementation
        return "Decimal Mode: " + input; // Placeholder return
    }

    // Main method to process the input based on the mode
    public static String processInput(String input) {
        if (hasRepeatingCharacters(input)) {
            input = processRepeatingCharacters(input);
        }

        if (getFractionMode()) {
            return handleFractionMode(input);
        } else {
            return handleDecimalMode(input);
        }
    }

    private static BigDecimal convertStringToBigDecimal(String input) {
        return BigDecimal.valueOf(Double.parseDouble(input));
    }

    private static boolean getFractionMode() {return MatrixApp.isFractionMode();}
}

