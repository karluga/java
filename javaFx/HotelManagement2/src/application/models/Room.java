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

    // New constructor to accept only roomName
    public Room(String name) {
        this.name = name;
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
    
    public boolean addReservation(LocalDate start, LocalDate end, String customerName, int numberOfPeople, int userId) {
        String query = """
            SELECT COUNT(*) AS count
            FROM bookings
            WHERE room_id = ? AND (
                (start_date <= ? AND end_date >= ?) OR
                (start_date <= ? AND end_date >= ?) OR
                (start_date >= ? AND end_date <= ?)
            )
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, this.id); // Room ID
            stmt.setObject(2, start);
            stmt.setObject(3, start);
            stmt.setObject(4, end);
            stmt.setObject(5, end);
            stmt.setObject(6, start);
            stmt.setObject(7, end);

            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt("count") > 0) {
                return false; // Time slot unavailable
            }

            // Insert the reservation
            String insertQuery = """
                INSERT INTO bookings (room_id, customer_name, start_date, end_date, number_of_people, user_id, is_paid, total_price)
                VALUES (?, ?, ?, ?, ?, ?, FALSE, ?)
            """;
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setInt(1, this.id);
                insertStmt.setString(2, customerName);
                insertStmt.setObject(3, start);
                insertStmt.setObject(4, end);
                insertStmt.setInt(5, numberOfPeople);
                insertStmt.setInt(6, userId);
                insertStmt.setDouble(7, this.pricePerNight * (end.toEpochDay() - start.toEpochDay() + 1));
                insertStmt.executeUpdate();
            }

            return true; // Reservation successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Reservation failed due to an error
        }
    }
}