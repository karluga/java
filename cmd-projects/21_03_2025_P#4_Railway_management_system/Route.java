import java.util.*;
import java.util.stream.Collectors;

// Functional interface for route validation (predicate)
interface RouteValidator {
    boolean validate(Route route, String departure, String destination);
}

class Route {
    private String id;
    // One-to-many relationship
    private List<Stop> stops;

    public Route(String id, List<Stop> stops) {
        this.id = id;
        this.stops = stops;
    }

    public String getId() {
        return id;
    }

    public String getFormattedStops() {
        return stops.stream()
                .map(stop -> stop.toString())
                .collect(Collectors.joining(" -> "));
    }

    public boolean hasStop(String stop) {
        return stops.stream()
                .anyMatch(s -> s.getName().equalsIgnoreCase(stop));
    }

    public boolean isValidRoute(String departure, String destination) {
        // Use the RouteValidator interface to validate the route
        RouteValidator validator = (route, dep, dest) -> {
            int depIndex = route.getStopIndex(dep);
            int destIndex = route.getStopIndex(dest);
            // Returns false if the destination is before the departure
            return depIndex >= 0 && destIndex > depIndex;
        };
        return validator.validate(this, departure, destination);
    }

    public double calculateCost(String departure, String destination) {
        int depIndex = getStopIndex(departure);
        int destIndex = getStopIndex(destination);
        return (destIndex - depIndex) * 5.0; // Base fare of 5.0 per stop
    }

    private int getStopIndex(String stopName) {
        for (int i = 0; i < stops.size(); i++) {
            if (stops.get(i).getName().equalsIgnoreCase(stopName)) {
                return i;
            }
        }
        return -1; // -1 if stop not found
    }
}
