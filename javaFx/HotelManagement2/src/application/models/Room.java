package application.models;

import application.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class Room {
    private int id;
    private String name;
    private int maxPeople;
    private double pricePerNight;

    public Room(int id, String name, int maxPeople) {
        this.id = id;
        this.name = name;
        this.maxPeople = maxPeople;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomName() {
        return name;
    }

    public void setRoomName(String name) {
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

    public boolean isAvailable(LocalDate startDate, LocalDate endDate) {
        String query = """
            SELECT COUNT(*) FROM bookings WHERE room_id = ? AND
            (start_date <= ? AND end_date >= ?)        
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.setString(2, endDate.toString());
            stmt.setString(3, startDate.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean addReservation(LocalDate startDate, LocalDate endDate, String customerName, int numberOfPeople, int userId) {
        if (!isAvailable(startDate, endDate)) {
            return false;
        }
        String query = "INSERT INTO bookings (room_id, customer_name, start_date, end_date, number_of_people, user_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, this.id);
                stmt.setString(2, customerName);
                stmt.setString(3, startDate.toString());
                stmt.setString(4, endDate.toString());
                stmt.setInt(5, numberOfPeople);
                stmt.setInt(6, userId);
                stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}