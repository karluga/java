import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

// string heap
/*
String str1 = "Hello";               // "Hello" stored in the string pool
String str2 = "Hello";               // Points to the same "Hello" in the pool
String str3 = new String("Hello");   // New object created in the heap

str1 == str2 is true (both reference the same pool object).
str1 == str3 is false (one is in the pool, and the other is a separate object in the heap).
 */

// pattern.matches = entire string
// pattern.find = subsequent

public class TextProcessingSystem {
    // Color codes
    public static final String ANSI_RED = "\033[31m";
    public static final String ANSI_YELLOW = "\033[33m";
    public static final String ANSI_GREEN = "\033[32m";
    public static final String ANSI_RESET = "\033[0m";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Text Processing System");
        String text = inputText(scanner, "Please input your text: ");

        while (true) {
            // Menu
            System.out.println("\nYour text: " + text);
            System.out.println("1. Count Words");
            System.out.println("2. Find Pattern");
            System.out.println("3. Replace Text");
            System.out.println("4. Validate Email");
            System.out.println("5. Enter different text");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");


            int choice = 0;
            boolean validChoice = false; // Flag

            // Loop until a valid choice is entered
            while (!validChoice) {
                System.out.print(ANSI_YELLOW);
                try {
                    choice = scanner.nextInt();
                    scanner.nextLine();
                    validChoice = true;
                } catch (Exception e) {
                    System.out.print(ANSI_RED);
                    System.out.println("Invalid choice. Please enter a number between 1 and 6.");
                    scanner.nextLine(); // Clear invalid input to avoid infinite loop
                }
            }
            System.out.print(ANSI_RESET);

            switch (choice) {
                case 1:
                    countWords(text);
                    break;

                case 2:
                    findPattern(scanner, text);
                    break;

                case 3:
                    text = replaceText(scanner, text);
                    break;

                case 4:
                    validateEmail(scanner);
                    break;

                case 5:
                    text = inputText(scanner, "Please input new text: ");
                    break;

                case 6:
                    System.out.println("Exiting...");
                    scanner.close();
                    System.exit(0);

                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    // Method to count words in the text
    public static void countWords(String text) {
        int wordCount = text.split("\\s+").length;
        System.out.println("Word count: " + ANSI_GREEN + wordCount + ANSI_RESET);
    }

    // Method to find pattern and highlight matches
    public static void findPattern(Scanner scanner, String text) {
        String pattern = "";
        Pattern compiledPattern = null;
        boolean isValidPattern = false;

        // Loop until a valid pattern is entered
        while (!isValidPattern) {
            pattern = inputText(scanner, "Enter pattern to find:");
            try {
                compiledPattern = Pattern.compile(pattern); // Try to compile the regex (case sensitive)
                isValidPattern = true;
            } catch (PatternSyntaxException e) {
                System.out.println("Invalid regex pattern: " + e.getDescription() + "\nPlease enter a different one.");
            }
        }

        Matcher matcher = compiledPattern.matcher(text);

        if (matcher.find()) {
            StringBuilder coloredText = new StringBuilder();
            int lastIndex = 0;
            do {
                coloredText.append(text, lastIndex, matcherAaart());
                coloredText.append(ANSI_GREEN).append(matcher.group()).append(ANSI_RESET);
                lastIndex = matcher.end();
            } while (matcher.find());

            coloredText.append(text.substring(lastIndex));
            System.out.println("Text with matched pattern highlighted:");
            System.out.println(coloredText.toString());
        } else {
            System.out.println("Pattern not found.");
        }
    }

    // Method to replace text
    public static String replaceText(Scanner scanner, String text) {
        String toReplace = inputText(scanner, "Enter text to replace:");
        String replacement = inputText(scanner, "Enter replacement text:");
        String updatedText = text.replace(toReplace, replacement);
        System.out.println("Updated text: " + updatedText);
        return updatedText;
    }

    // Method to validate email
    public static void validateEmail(Scanner scanner) {
        System.out.print("Enter email to validate: ");
        String email = scanner.nextLine();
    
        // Regex pattern for email validation
        String emailRegex = "^[a-z0-9.-]+@[a-z0-9.-]+\\.[a-z]{2,}$";
    
        if (Pattern.matches(emailRegex, email)) {
            System.out.println("Email is valid");
        } else {
            System.out.println("Invalid email");
        }
    }

    // Method to get user input
    public static String inputText(Scanner scanner, String prompt) {
        String input = "";
        while (input.isEmpty()) {
            System.out.print(prompt);
            System.out.print(ANSI_YELLOW);
            input = scanner.nextLine().trim();
            System.out.print(ANSI_RESET);

            if (input.isEmpty()) {
                System.out.println("Input cannot be empty. Please try again.");
            }
        }
        return input;
    }
}
