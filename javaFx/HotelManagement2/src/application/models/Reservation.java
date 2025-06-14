package application.models;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Reservation {
    private int id;
    private int roomId;
    private String roomName;
    private String customerName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isPaid; // Changed from boolean to Boolean to allow null
    private double totalPrice;
    private double paidAmount; // Store pre-fetched paid amount

    // Updated constructor to include roomId
    public Reservation(int id, int roomId, String roomName, String customerName, LocalDate startDate, LocalDate endDate, Boolean isPaid, double totalPrice, double paidAmount) {
        this.id = id;
        this.roomId = roomId;
        this.roomName = roomName;
        this.customerName = customerName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isPaid = isPaid;
        this.totalPrice = totalPrice;
        this.paidAmount = paidAmount;
    }

    public int getId() {
        return id;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getRoomName() { return roomName; }
    public String getCustomerName() { return customerName; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public Boolean isPaid() { return isPaid; }
    public double getTotalPrice() { return totalPrice; }

    public double getPaidAmount() {
        return paidAmount; // Return pre-fetched value
    }

    public void setPaid(Boolean isPaid) {
        this.isPaid = isPaid;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public double getRemainingBalance() {
        return totalPrice - paidAmount;
    }

    public long getNumberOfNights() {
        return ChronoUnit.DAYS.between(startDate, endDate) + 1; // Include the last day
    }
}