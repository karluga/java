package application.models;

import java.time.LocalDate;

public class Reservation {
    private String roomName;
    private String customerName;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isPaid;
    private double totalPrice;

    // Constructor, getters, and setters
    public Reservation(String roomName, String customerName, LocalDate startDate, LocalDate endDate, boolean isPaid, double totalPrice) {
        this.roomName = roomName;
        this.customerName = customerName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isPaid = isPaid;
        this.totalPrice = totalPrice;
    }

    public String getRoomName() { return roomName; }
    public String getCustomerName() { return customerName; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public boolean isPaid() { return isPaid; }
    public double getTotalPrice() { return totalPrice; }
}