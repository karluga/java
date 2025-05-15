package application.models;

import application.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Reservation {
    private String roomName;
    private String customerName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isPaid; // Changed from boolean to Boolean to allow null
    private double totalPrice;
    private double paidAmount; // Store pre-fetched paid amount

    // Updated constructor to include isPaid as Boolean
    public Reservation(String roomName, String customerName, LocalDate startDate, LocalDate endDate, Boolean isPaid, double totalPrice, double paidAmount) {
        this.roomName = roomName;
        this.customerName = customerName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isPaid = isPaid;
        this.totalPrice = totalPrice;
        this.paidAmount = paidAmount;
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