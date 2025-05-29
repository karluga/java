package com.may.informatic.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "max_people")
    private int maxPeople;

    @Column(name = "price_per_night")
    private double pricePerNight;

    @Column(name = "reserved")
    private Boolean reserved;

    public Room() {}

    public Room(String name, int maxPeople, double pricePerNight) {
        this.name = name;
        this.maxPeople = maxPeople;
        this.pricePerNight = pricePerNight;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxPeople() {
        return maxPeople;
    }

    public void setMaxPeople(int maxPeople) {
        this.maxPeople = maxPeople;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public Boolean isReserved() {
        return reserved;
    }

    public void setReserved(Boolean reserved) {
        this.reserved = reserved;
    }
}