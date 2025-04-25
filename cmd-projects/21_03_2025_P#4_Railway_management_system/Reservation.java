class Reservation {
    private String routeId;
    private String departure;
    private String destination;
    private int passengers;
    private double fare;

    public Reservation(String routeId, String departure, String destination, int passengers, double fare) {
        this.routeId = routeId;
        this.departure = departure;
        this.destination = destination;
        this.passengers = passengers;
        this.fare = fare;
    }

    public String toCSV() {
        return String.join(",", routeId, departure, destination, String.valueOf(passengers), String.valueOf(fare));
    }
}
