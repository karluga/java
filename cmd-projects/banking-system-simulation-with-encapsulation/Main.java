import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static Map<String, Customer> customers = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Banking System ---");
            System.out.println("1. Add New Customer");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. Check Balance");
            System.out.println("5. Transfer Money");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");

            int option = scanner.nextInt();
            scanner.nextLine(); // consume newline

            try {
                switch (option) {
                    case 1:
                        addCustomer(scanner);
                        break;
                    case 2:
                        depositMoney(scanner);
                        break;
                    case 3:
                        withdrawMoney(scanner);
                        break;
                    case 4:
                        checkBalance(scanner);
                        break;
                    case 5:
                        transferMoney(scanner);
                        break;
                    case 6:
                        System.out.println("Exiting system. Goodbye!");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void addCustomer(Scanner scanner) {
        String accountNumber = "";
        while (true) {
            System.out.print("Enter account number (Example: LV80BANK0000435195001): ");
            accountNumber = scanner.nextLine().replaceAll("\\s+", ""); // Remove spaces from input
    
            try {
                if (customers.containsKey(accountNumber)) {
                    throw new IllegalArgumentException("Account with this number already exists.");
                }
                // Validate IBAN format immediately
                new Customer(accountNumber, 0); // Test validity by attempting to create a customer
                break; // Exit loop if no exception is thrown
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    
        double initialBalance = 0;
        while (true) {
            System.out.print("Enter initial balance: ");
            String balanceInput = scanner.nextLine().replace(",", "."); // Allow comma or dot
    
            try {
                if (!balanceInput.matches("\\d+(\\.\\d{1,2})?")) { // Check for valid format
                    throw new IllegalArgumentException("Error: Too many decimal places or invalid input.");
                }
                initialBalance = Double.parseDouble(balanceInput);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid numeric balance.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    
        customers.put(accountNumber, new Customer(accountNumber, initialBalance));
        System.out.println("Account created successfully!");
    }
    

    private static void depositMoney(Scanner scanner) {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        Customer customer = customers.get(accountNumber);

        if (customer == null) {
            throw new IllegalArgumentException("Account not found.");
        }

        System.out.print("Enter deposit amount: ");
        double amount = scanner.nextDouble();
        customer.deposit(amount);
        System.out.println("Successfully deposited: $" + String.format("%.2f", amount));
    }

    private static void withdrawMoney(Scanner scanner) {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        Customer customer = customers.get(accountNumber);

        if (customer == null) {
            throw new IllegalArgumentException("Account not found.");
        }

        System.out.print("Enter withdrawal amount: ");
        double amount = scanner.nextDouble();
        customer.withdraw(amount);
        System.out.println("Successfully withdrew: $" + String.format("%.2f", amount));
    }

    private static void checkBalance(Scanner scanner) {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        Customer customer = customers.get(accountNumber);

        if (customer == null) {
            throw new IllegalArgumentException("Account not found.");
        }

        System.out.println("Current balance for account " + accountNumber + ": $" + String.format("%.2f", customer.getBalance()));
    }

    private static void transferMoney(Scanner scanner) {
        System.out.print("Enter your account number: ");
        String senderAccount = scanner.nextLine();
        Customer sender = customers.get(senderAccount);

        if (sender == null) {
            throw new IllegalArgumentException("Sender account not found.");
        }

        System.out.print("Enter recipient account number: ");
        String receiverAccount = scanner.nextLine();
        Customer receiver = customers.get(receiverAccount);

        if (receiver == null) {
            throw new IllegalArgumentException("Recipient account not found.");
        }

        while (true) {
            System.out.print("Enter amount to transfer: ");
            double amount = scanner.nextDouble();
            scanner.nextLine(); // consume newline

            try {
                sender.transfer(receiver, amount);
                System.out.println("Successfully transferred $" + String.format("%.2f", amount) + " from " + senderAccount + " to " + receiverAccount);
                break; // Exit the loop if transfer is successful
            } catch (IllegalArgumentException e) {
                if (e.getMessage().equals("Insufficient balance for transfer.")) {
                    System.out.println("Error: Insufficient balance.");
                    
                    // Ask user if they want to retry or exit
                    System.out.print("Do you want to try again with a different amount? (y/n): ");
                    String choice = scanner.nextLine().trim().toLowerCase();

                    if (!choice.equals("y")) {
                        System.out.println("Transfer canceled.");
                        break; // Exit if user does not want to retry
                    }
                }
            }
        }
    }
}
