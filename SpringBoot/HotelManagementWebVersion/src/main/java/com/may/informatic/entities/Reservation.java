package com.may.informatic.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "bookings")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int userId;

    @Column(name = "room_id")
    private int roomId;

    @Column(name = "room_name")
    private String roomName;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_paid")
    private Boolean isPaid;

    @Column(name = "total_price")
    private double totalPrice;

    @Column(name = "number_of_people")
    private int numberOfPeople;

    @Transient
    private double paidAmount;

    public Reservation() {}

    public Reservation(int id, int userId, int roomId, String roomName, String customerName, LocalDate startDate, LocalDate endDate, Boolean isPaid, double totalPrice, int numberOfPeople, double paidAmount) {
        this.id = id;
        this.userId = userId;
        this.roomId = roomId;
        this.roomName = roomName;
        this.customerName = customerName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isPaid = isPaid;
        this.totalPrice = totalPrice;
        this.numberOfPeople = numberOfPeople;
        this.paidAmount = paidAmount;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Boolean getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public double getRemainingBalance() {
        return totalPrice - paidAmount;
    }

    public long getNumberOfNights() {
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    @Transient
    public String getStatusClass() {
        if (Boolean.TRUE.equals(isPaid)) {
            return "paid";
        } else if (Boolean.FALSE.equals(isPaid)) {
            return "unpaid";
        } else if (LocalDate.now().isAfter(endDate.plusDays(1))) {
            return "overdue";
        } else {
            return "unpaid";
        }
    }

    @Transient
    public boolean isCancelable() {
        return LocalDate.now().isBefore(startDate);
    }
}