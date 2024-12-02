// Utility Class for Input Validation
// Uses DRY: Reuses validation logic in many places
class InputValidator {
    // Ensures string inputs are not null or empty.
    public static boolean isStringValid(String input) {
        return input != null && !input.trim().isEmpty();
    }

    // Ensures numeric inputs are positive.
    public static boolean isPositiveNumber(int number) {
        return number > 0;
    }
}