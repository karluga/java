import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

class RailwayManagement {
    private List<Route> routes = new ArrayList<>();
    private List<Reservation> reservations = new ArrayList<>();
    private static final String ROUTE_CSV = "routes.csv";
    private static final String RESERVATION_CSV = "reservations.csv";

    public void loadRoutesFromCSV() {
        try (BufferedReader br = new BufferedReader(new FileReader(ROUTE_CSV))) {
            routes = br.lines()
                    .map(this::createRouteFromLine) // Use helper method to create Route
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("Error loading routes CSV: " + e.getMessage());
        }
    }

    Route createRouteFromLine(String line) {
        String[] data = line.split(","); // Split the line into Route ID and stops
        List<Stop> stops = Arrays.stream(Arrays.copyOfRange(data, 1, data.length)) // Stream stop data
                .map(this::createStopFromData) // Use helper method to create Stop
                .collect(Collectors.toList());
        return new Route(data[0], stops); // Create and return Route
    }

    Stop createStopFromData(String stopData) {
        String[] stopParts = stopData.split(";"); // Split stop data into name and additional info
        return new Stop(stopParts[0], stopParts[1]); // Create and return Stop
    }

    public void saveReservation(Reservation reservation) {
        reservations.add(reservation);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RESERVATION_CSV, true))) {
            bw.write(reservation.toCSV());
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error saving reservation: " + e.getMessage());
        }
    }

    public void displayRoutes() {
        for (Route route : routes) {
            System.out.println("Route ID: " + route.getId() + " Stops: " + route.getFormattedStops());
        }
    }

    public List<Route> findRoutes(String departure, String destination) {
        return routes.stream()
                .filter(route -> route.hasStop(departure) && route.hasStop(destination) && route.isValidRoute(departure, destination))
                .collect(Collectors.toList()); // Collecting multiple valid routes
    }
}
