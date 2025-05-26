package application.services;

import application.DBConnection;
import application.models.Reservation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationService {
    public List<Reservation> fetchReservations(int pageSize, int offset) {
        List<Reservation> reservations = new ArrayList<>();
        String query = """
            SELECT b.id, b.room_id, r.name AS room_name, b.customer_name, b.start_date, b.end_date, b.is_paid, b.total_price,
                   COALESCE(SUM(p.amount_paid), 0) AS total_paid
            FROM bookings b
            JOIN rooms r ON b.room_id = r.id
            LEFT JOIN payments p ON b.id = p.reservation_id
            GROUP BY b.id
            ORDER BY b.start_date ASC
            LIMIT ? OFFSET ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, pageSize);
            stmt.setInt(2, offset);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                reservations.add(new Reservation(
                    rs.getInt("id"),
                    rs.getInt("room_id"),
                    rs.getString("room_name"),
                    rs.getString("customer_name"),
                    LocalDate.parse(rs.getString("start_date")),
                    LocalDate.parse(rs.getString("end_date")),
                    rs.getBoolean("is_paid"),
                    rs.getDouble("total_price"),
                    rs.getDouble("total_paid")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return reservations;
    }

    public List<Reservation> filterReservations(String searchQuery) {
        List<Reservation> filteredReservations = new ArrayList<>();
        String query = """
            SELECT b.id, b.room_id, r.name AS room_name, b.customer_name, b.start_date, b.end_date, b.is_paid, b.total_price,
                   COALESCE(SUM(p.amount_paid), 0) AS total_paid
            FROM bookings b
            JOIN rooms r ON b.room_id = r.id
            LEFT JOIN payments p ON b.id = p.reservation_id
            WHERE r.name LIKE ? OR b.customer_name LIKE ?
            GROUP BY b.id
            ORDER BY b.start_date ASC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            String searchPattern = "%" + searchQuery + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                filteredReservations.add(new Reservation(
                    rs.getInt("id"), // Reservation ID
                    rs.getInt("room_id"), // Room ID
                    rs.getString("room_name"),
                    rs.getString("customer_name"),
                    LocalDate.parse(rs.getString("start_date")),
                    LocalDate.parse(rs.getString("end_date")),
                    rs.getBoolean("is_paid"),
                    rs.getDouble("total_price"),
                    rs.getDouble("total_paid")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return filteredReservations;
    }
}