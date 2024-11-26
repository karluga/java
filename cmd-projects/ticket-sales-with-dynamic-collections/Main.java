import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    // Using a generic `ArrayList` to store ticket prices
    // Generic types allow type safety and reusability
    private static final ArrayList<Double> ticketPrices = new ArrayList<>();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\nTicket Sales Management System:");
            System.out.println("1. Add Ticket Price");
            System.out.println("2. Update Ticket Price");
            System.out.println("3. Delete Ticket Price");
            System.out.println("4. List Ticket Prices");
            System.out.println("5. Calculate Total Sales");
            System.out.println("6. Calculate Average Price");
            System.out.println("7. Find Maximum Price");
            System.out.println("8. Find Minimum Price");
            System.out.println("9. Apply Discount to Specific Index");
            System.out.println("10. Exit");
            System.out.print("Choose an option: ");

            try {
                int option = scanner.nextInt();
                switch (option) {
                    case 1 -> addTicketPrice();
                    case 2 -> updateTicketPrice();
                    case 3 -> deleteTicketPrice();
                    case 4 -> listTicketPrices();
                    case 5 -> calculateTotalSales();
                    case 6 -> calculateAveragePrice();
                    case 7 -> findMaxPrice();
                    case 8 -> findMinPrice();
                    case 9 -> applyDiscountToIndex();
                    case 10 -> {
                        System.out.println("Exiting...");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Try again.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input! Please try again.");
                scanner.next(); // Clear invalid input
            }
        }
    }

    // Add a ticket price
    // Demonstrates autoboxing: the primitive `double` is automatically converted to `Double`
    private static void addTicketPrice() {
        try {
            System.out.print("Enter ticket price to add: ");
            double price = scanner.nextDouble();
            if (price < 0) throw new IllegalArgumentException("Price cannot be negative.");
            ticketPrices.add(price); // Autoboxing occurs here
            System.out.println("Ticket price added.");
        } catch (Exception e) {
            System.out.println("Invalid input! Please enter a valid number.");
            scanner.next(); // Clear invalid input
        }
    }

    // Update a ticket price
    private static void updateTicketPrice() {
        try {
            System.out.print("Enter the index of the ticket price to update: ");
            int index = scanner.nextInt();
            validateIndex(index);
            System.out.print("Enter the new ticket price: ");
            double newPrice = scanner.nextDouble();
            if (newPrice < 0) throw new IllegalArgumentException("Price cannot be negative.");
            ticketPrices.set(index, newPrice); // Replacing value at index
            System.out.println("Ticket price updated.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Delete a ticket price
    private static void deleteTicketPrice() {
        try {
            System.out.print("Enter the index of the ticket price to delete: ");
            int index = scanner.nextInt();
            validateIndex(index);
            ticketPrices.remove(index); // Removes the element at the specified index
            System.out.println("Ticket price deleted.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // List all ticket prices
    private static void listTicketPrices() {
        if (ticketPrices.isEmpty()) {
            System.out.println("No ticket prices available.");
        } else {
            System.out.println("Ticket Prices:");
            for (int i = 0; i < ticketPrices.size(); i++) {
                System.out.println(i + ": $" + ticketPrices.get(i)); // Unboxing occurs here
            }
        }
    }

    // Calculate total sales
    // Stream API processes data efficiently by converting `Double` to primitive `double` (unboxing)
    private static void calculateTotalSales() {
        double total = ticketPrices.stream()
            .mapToDouble(Double::doubleValue) // Unboxing for numeric operations
            .sum();
        System.out.println("Total Sales: $" + total);
    }

    // Calculate average price
    private static void calculateAveragePrice() {
        if (ticketPrices.isEmpty()) {
            System.out.println("No ticket prices available to calculate average.");
        } else {
            double average = ticketPrices.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0); // Default value if no elements
            System.out.println("Average Ticket Price: $" + average);
        }
    }

    // Find maximum price
    private static void findMaxPrice() {
        if (ticketPrices.isEmpty()) {
            System.out.println("No ticket prices available to find maximum.");
        } else {
            double max = ticketPrices.stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0.0);
            System.out.println("Maximum Ticket Price: $" + max);
        }
    }

    // Find minimum price
    private static void findMinPrice() {
        if (ticketPrices.isEmpty()) {
            System.out.println("No ticket prices available to find minimum.");
        } else {
            double min = ticketPrices.stream()
                .mapToDouble(Double::doubleValue)
                .min()
                .orElse(0.0);
            System.out.println("Minimum Ticket Price: $" + min);
        }
    }

    // Apply a discount to a specific ticket price
    private static void applyDiscountToIndex() {
        try {
            System.out.print("Enter the index of the ticket price to apply a discount: ");
            int index = scanner.nextInt();
            validateIndex(index);

            System.out.print("Enter discount percentage (e.g., 10 for 10%): ");
            double discount = scanner.nextDouble();
            if (discount < 0 || discount > 100) throw new IllegalArgumentException("Discount must be between 0 and 100.");

            double originalPrice = ticketPrices.get(index); // Unboxing
            double discountedPrice = originalPrice * (1 - discount / 100);

            System.out.println("Original Price: $" + originalPrice);
            System.out.println("Discounted Price: $" + discountedPrice);

            System.out.print("Do you want to keep the changes? (y/n): ");
            String choice = scanner.next();
            if (choice.equalsIgnoreCase("y")) {
                ticketPrices.set(index, discountedPrice); // Replacing with discounted value
                System.out.println("Discount applied and changes saved.");
            } else {
                System.out.println("Changes discarded.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            scanner.next(); // Clear invalid input
        }
    }

    // Helper method to validate an index before accessing the list
    private static void validateIndex(int index) {
        if (index < 0 || index >= ticketPrices.size()) {
            throw new IllegalArgumentException("Invalid index! Please choose a valid index.");
        }
    }
}
