import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ArrayList<Double> ticketPrices = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        do {
            System.out.println("\nTicket Sales Management System:");
            System.out.println("1. Add Ticket Price");
            System.out.println("2. Update Ticket Price");
            System.out.println("3. Delete Ticket Price");
            System.out.println("4. List Ticket Prices");
            System.out.println("5. Calculate Total Sales");
            System.out.println("6. Calculate Average Price");
            System.out.println("7. Find Maximum Price");
            System.out.println("8. Find Minimum Price");
            System.out.println("9. Exit");
            System.out.print("Choose an option: ");

            try {
                choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        try {
                            System.out.print("Enter ticket price to add: ");
                            double priceToAdd = scanner.nextDouble().replace(',', '.'); // Replace comma with dot;
                            if (priceToAdd < 0) throw new IllegalArgumentException("Price cannot be negative.");
                            ticketPrices.add(priceToAdd);
                            System.out.println("Ticket price added.");
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input! Please enter a valid number.");
                            scanner.next(); // Clear the invalid input
                        } catch (IllegalArgumentException e) {
                            System.out.println(e.getMessage());
                        }
                        break;

                    case 2:
                        try {
                            System.out.print("Enter the index of the ticket price to update: ");
                            int indexToUpdate = scanner.nextInt();
                            if (indexToUpdate < 0 || indexToUpdate >= ticketPrices.size())
                                throw new IndexOutOfBoundsException("Invalid index. Please enter a valid index.");
                            System.out.print("Enter the new ticket price: ");
                            double newPrice = scanner.nextDouble().replace(',', '.'); // Replace comma with dot;
                            if (newPrice < 0) throw new IllegalArgumentException("Price cannot be negative.");
                            ticketPrices.set(indexToUpdate, newPrice);
                            System.out.println("Ticket price updated.");
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input! Please enter a valid number.");
                            scanner.next(); // Clear the invalid input
                        } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
                            System.out.println(e.getMessage());
                        }
                        break;

                    case 3:
                        try {
                            System.out.print("Enter the index of the ticket price to delete: ");
                            int indexToDelete = scanner.nextInt();
                            if (indexToDelete < 0 || indexToDelete >= ticketPrices.size())
                                throw new IndexOutOfBoundsException("Invalid index. Please enter a valid index.");
                            ticketPrices.remove(indexToDelete);
                            System.out.println("Ticket price deleted.");
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input! Please enter a valid number.");
                            scanner.next(); // Clear the invalid input
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println(e.getMessage());
                        }
                        break;

                    case 4:
                        if (ticketPrices.isEmpty()) {
                            System.out.println("No ticket prices available.");
                        } else {
                            System.out.println("Ticket Prices:");
                            for (int i = 0; i < ticketPrices.size(); i++) {
                                System.out.println(i + ": $" + ticketPrices.get(i));
                            }
                        }
                        break;

                    case 5:
                        if (ticketPrices.isEmpty()) {
                            System.out.println("No ticket prices available to calculate total sales.");
                        } else {
                            double totalSales = ticketPrices.stream().mapToDouble(Double::doubleValue).sum();
                            System.out.println("Total Sales: $" + totalSales);
                        }
                        break;

                    case 6:
                        if (ticketPrices.isEmpty()) {
                            System.out.println("No ticket prices available to calculate average price.");
                        } else {
                            double averagePrice = ticketPrices.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                            System.out.println("Average Ticket Price: $" + averagePrice);
                        }
                        break;

                    case 7:
                        if (ticketPrices.isEmpty()) {
                            System.out.println("No ticket prices available to find maximum price.");
                        } else {
                            double maxPrice = ticketPrices.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
                            System.out.println("Maximum Ticket Price: $" + maxPrice);
                        }
                        break;

                    case 8:
                        if (ticketPrices.isEmpty()) {
                            System.out.println("No ticket prices available to find minimum price.");
                        } else {
                            double minPrice = ticketPrices.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
                            System.out.println("Minimum Ticket Price: $" + minPrice);
                        }
                        break;

                    case 9:
                        System.out.println("Exiting the system. Goodbye!");
                        break;

                    default:
                        System.out.println("Invalid option. Please choose a number between 1 and 9.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a valid option number.");
                scanner.next(); // Clear the invalid input
            }
        } while (choice != 9);

        scanner.close();
    }
}
