import java.util.*;

// Functional interface (requires input and returns output)
interface FareCalculator {
    abstract double calculate(double baseFare, int passengers);
}

public class Main {
    public static void main(String[] args) {
        RailwayManagement rm = new RailwayManagement();
        rm.loadRoutesFromCSV();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("""
                1 - Show all routes
                2 - Book a ticket
                3 - Exit
                """);
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                rm.displayRoutes();
            } else if (choice == 2) {
                System.out.print("Enter departure stop: ");
                String departure = scanner.nextLine();
                System.out.print("Enter destination stop: ");
                String destination = scanner.nextLine();

                List<Route> possibleRoutes = rm.findRoutes(departure, destination);
                if (!possibleRoutes.isEmpty()) {
                    System.out.println("Available routes:");
                    for (int i = 0; i < possibleRoutes.size(); i++) {
                        Route route = possibleRoutes.get(i);
                        double baseFare = route.calculateCost(departure, destination);
                        System.out.println((i + 1) + " - Route ID: " + route.getId() +
                                           " Stops: " + route.getFormattedStops() +
                                           " Price: " + baseFare + " EUR");
                    }

                    int selectedIndex;
                    do {
                        System.out.print("Enter number to choose route: ");
                        selectedIndex = scanner.nextInt() - 1;
                    } while (selectedIndex < 0 || selectedIndex >= possibleRoutes.size());

                    Route chosenRoute = possibleRoutes.get(selectedIndex);
                    scanner.nextLine();

                    System.out.print("Enter number of people (default is 1): ");
                    String input = scanner.nextLine();
                    int passengers = input.isEmpty() ? 1 : Integer.parseInt(input);

                    // Lambda expression for fare calculation
                    FareCalculator fareCalculator = (baseFare, numPassengers) -> baseFare * numPassengers;

                    double baseFare = chosenRoute.calculateCost(departure, destination);
                    double fare = fareCalculator.calculate(baseFare, passengers);

                    rm.saveReservation(new Reservation(chosenRoute.getId(), departure, destination, passengers, fare));
                    System.out.println("Ticket booked. Total Price: " + fare + " EUR");
                } else {
                    System.out.println("No matching routes found.");
                }
            } else if (choice == 3) {
                break;
            } else {
                System.out.println("Invalid choice. Try again.");
            }
        }
        scanner.close();
    }
}
