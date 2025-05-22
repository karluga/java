package application.services;

import application.DBConnection;
import application.models.Reservation;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class PaymentService {
    public boolean markAsPaid(Reservation reservation, boolean fullyPaid) {
        String query = """
            UPDATE bookings
            SET is_paid = ?
            WHERE room_id = (SELECT id FROM rooms WHERE name = ?) AND customer_name = ? AND start_date = ? AND end_date = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBoolean(1, fullyPaid);
            stmt.setString(2, reservation.getRoomName());
            stmt.setString(3, reservation.getCustomerName());
            stmt.setObject(4, reservation.getStartDate());
            stmt.setObject(5, reservation.getEndDate());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean recordPartialPayment(Reservation reservation, double paymentAmount) {
        String insertQuery = """
            INSERT INTO payments (reservation_id, amount_paid)
            VALUES ((SELECT id FROM bookings WHERE room_id = (SELECT id FROM rooms WHERE name = ?) AND customer_name = ? AND start_date = ? AND end_date = ?), ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.setString(1, reservation.getRoomName());
            stmt.setString(2, reservation.getCustomerName());
            stmt.setObject(3, reservation.getStartDate());
            stmt.setObject(4, reservation.getEndDate());
            stmt.setDouble(5, paymentAmount);

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
