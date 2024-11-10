import java.util.ArrayList;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class SupermarketInventory {
    private static ArrayList<Product> inventory = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            System.out.println("\nSupermarket Inventory System");
            System.out.println("1. Add Perishable Product");
            System.out.println("2. Add Non-Perishable Product");
            System.out.println("3. Display Product Info");
            System.out.println("4. Restock Product");
            System.out.println("5. Exit");
            System.out.print("Enter command (1-5): ");

            int command = getIntInput(1, 5);

            switch (command) {
                case 1 -> addPerishableProduct();
                case 2 -> addNonPerishableProduct();
                case 3 -> displayProductInfo();
                case 4 -> restockProduct();
                case 5 -> running = false;
                default -> System.out.println("Invalid command.");
            }
        }

        scanner.close();
        System.out.println("Exiting system. Goodbye!");
    }

    private static void addPerishableProduct() {
        String productId = getUniqueProductId();
        System.out.print("Enter Product Name: ");
        String name = scanner.nextLine();
        double price = getValidatedPrice("Enter Price: ");
        int quantity = getIntInput("Enter Quantity: ");

        String expiryDate;
        while (true) {
            System.out.print("Enter Expiry Date (YYYY-MM-DD): ");
            expiryDate = scanner.nextLine();
            if (isValidFutureDate(expiryDate)) {
                break;
            } else {
                System.out.println("Invalid date. Expiry date must be in the future in YYYY-MM-DD format.");
            }
        }

        inventory.add(new PerishableProduct(productId, name, price, quantity, expiryDate));
        System.out.println("Perishable product added.");
    }

    private static void addNonPerishableProduct() {
        String productId = getUniqueProductId();
        System.out.print("Enter Product Name: ");
        String name = scanner.nextLine();
        double price = getValidatedPrice("Enter Price: ");
        int quantity = getIntInput("Enter Quantity: ");

        String warrantyMonths;
        while (true) {
            int months = getIntInput("Enter Warranty Period (months): ");
            warrantyMonths = LocalDate.now().plusMonths(months).toString();
            if (LocalDate.parse(warrantyMonths).isAfter(LocalDate.now())) {
                break;
            } else {
                System.out.println("Invalid warranty period. Warranty must be valid for future use.");
            }
        }

        inventory.add(new NonPerishableProduct(productId, name, price, quantity, warrantyMonths + " months"));
        System.out.println("Non-perishable product added.");
    }

    private static String getUniqueProductId() {
        String productId;
        while (true) {
            System.out.print("Enter Product ID: ");
            productId = scanner.nextLine().trim();
            if (!isDuplicateId(productId)) {
                break;
            } else {
                System.out.println("Product ID already exists. Please enter a unique Product ID.");
            }
        }
        return productId;
    }

    private static boolean isDuplicateId(String productId) {
        for (Product product : inventory) {
            if (product.getProductId().equalsIgnoreCase(productId)) {
                return true;
            }
        }
        return false;
    }

    private static void displayProductInfo() {
        if (inventory.isEmpty()) {
            System.out.println("No products in inventory.");
            return;
        }
        for (Product product : inventory) {
            System.out.println(product.getProductInfo());
        }
    }

    private static void restockProduct() {
        System.out.print("Enter Product ID to restock: ");
        String productId = scanner.nextLine().trim();

        for (Product product : inventory) {
            if (product.getProductId().equalsIgnoreCase(productId)) {
                int amount = getIntInput("Enter Restock Amount: ");
                // Check if the entered amount is negative and prompt again
                while (amount < 0) {
                    System.out.println("Restock amount cannot be negative. Please enter a valid restock amount.");
                    amount = getIntInput("Enter Restock Amount: ");
                }
                product.restock(amount);
                return;
            }
        }
        System.out.println("Product not found.");
    }

    private static boolean isValidFutureDate(String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr);
            return date.isAfter(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private static double getValidatedPrice(String message) {
        System.out.print(message);
        while (true) {
            String input = scanner.nextLine().trim().replace(",", ".");

            if (input.contains(".") && input.split("\\.")[1].length() > 2) {
                System.out.println("Price cannot have more than two decimal places. Please enter a valid price.");
                continue;
            }

            try {
                double price = Double.parseDouble(input);
                if (price <= 0) {
                    System.out.print("Price must be positive. " + message);
                    continue;
                }
                return price;
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid price: ");
            }
        }
    }

    private static int getIntInput(int min, int max) {
        int input;
        while (true) {
            try {
                input = Integer.parseInt(scanner.nextLine().trim());
                if (input >= min && input <= max) {
                    return input;
                } else {
                    System.out.print("Invalid input. Please enter a number between " + min + " and " + max + ": ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    private static int getIntInput(String message) {
        System.out.print(message);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid number: ");
            }
        }
    }
}
