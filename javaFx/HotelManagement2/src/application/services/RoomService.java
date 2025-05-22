package application.services;

import application.DBConnection;
import application.models.Room;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RoomService {
    private List<Room> allRooms; // Store all rooms loaded from the database

    public List<Room> fetchRooms(int userId, String searchQuery, int pageSize, int offset) {
        List<Room> rooms = new ArrayList<>();
        String query = searchQuery.isEmpty() ? """
            SELECT r.id, r.name, r.max_people, r.price_per_night, b.end_date
            FROM rooms r LEFT JOIN bookings b ON r.id = b.room_id AND b.user_id = ?
            WHERE b.end_date IS NULL OR b.end_date > CURRENT_DATE
            ORDER BY b.end_date DESC, r.id LIMIT ? OFFSET ?
        """ : """
            SELECT r.id, r.name, r.max_people, r.price_per_night, b.end_date
            FROM rooms r LEFT JOIN bookings b ON r.id = b.room_id AND b.user_id = ?
            WHERE (b.end_date IS NULL OR b.end_date > CURRENT_DATE) AND r.name LIKE ?
            ORDER BY b.end_date DESC, r.id LIMIT ? OFFSET ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            int paramIndex = 1;
            stmt.setInt(paramIndex++, userId);
            if (!searchQuery.isEmpty()) {
                stmt.setString(paramIndex++, "%" + searchQuery + "%");
            }
            stmt.setInt(paramIndex++, pageSize);
            stmt.setInt(paramIndex, offset);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Room room = new Room(rs.getInt("id"), rs.getString("name"), rs.getInt("max_people"));
                room.setPricePerNight(rs.getDouble("price_per_night"));
                rooms.add(room);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public int getActiveBookingsCount(int userId) {
        String query = "SELECT COUNT(*) FROM bookings WHERE user_id = ? AND end_date > CURRENT_DATE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalBookingsCount(int userId) {
        String query = "SELECT COUNT(*) FROM bookings WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Room> filterRooms(String searchQuery) {
        if (searchQuery.isEmpty()) {
            return allRooms;
        }
        return allRooms.stream()
                .filter(room -> room.getRoomName().toLowerCase().contains(searchQuery.toLowerCase()))
                .collect(Collectors.toList());
    }

    public void setAllRooms(List<Room> rooms) {
        this.allRooms = rooms; // Allow setting the list of all rooms
    }

    public Optional<Integer> reserveRoom(int userId, Room room, LocalDate startDate, LocalDate endDate, int numberOfPeople) {
        String checkQuery = """
            SELECT COUNT(*) AS count
            FROM bookings
            WHERE room_id = ? AND (
                (start_date <= ? AND end_date >= ?) OR
                (start_date <= ? AND end_date >= ?) OR
                (start_date >= ? AND end_date <= ?)
            )
        """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {

            // Check for overlapping reservations
            checkStmt.setInt(1, room.getId());
            checkStmt.setString(2, endDate.toString());
            checkStmt.setString(3, startDate.toString());
            checkStmt.setString(4, startDate.toString());
            checkStmt.setString(5, endDate.toString());
            checkStmt.setString(6, startDate.toString());
            checkStmt.setString(7, endDate.toString());

            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt("count") > 0) {
                System.out.println("Debug: Overlapping reservation found.");
                return Optional.empty(); // Overlapping reservation
            }

            // Insert the reservation if no overlap
            String insertQuery = """
                INSERT INTO bookings (room_id, user_id, start_date, end_date, number_of_people, is_paid, total_price)
                VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

            try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                System.out.println("Debug: Preparing to execute INSERT query.");
                insertStmt.setInt(1, room.getId());
                insertStmt.setInt(2, userId);
                insertStmt.setString(3, startDate.toString());
                insertStmt.setString(4, endDate.toString());
                insertStmt.setInt(5, numberOfPeople);
                insertStmt.setNull(6, java.sql.Types.BOOLEAN); // is_paid defaults to NULL
                long nights = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
                double totalPrice = nights * room.getPricePerNight();
                insertStmt.setDouble(7, totalPrice);

                System.out.println("Debug: INSERT query values - Room ID: " + room.getId() +
                                   ", User ID: " + userId +
                                   ", Start Date: " + startDate +
                                   ", End Date: " + endDate +
                                   ", Number of People: " + numberOfPeople +
                                   ", Total Price: " + totalPrice);

                int rowsAffected = insertStmt.executeUpdate();
                System.out.println("Debug: Rows affected by INSERT query: " + rowsAffected);

                if (rowsAffected > 0) {
                    // Retrieve the generated reservation ID
                    ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        System.out.println("Debug: Generated reservation ID: " + generatedId);
                        return Optional.of(generatedId); // Return the generated ID
                    } else {
                        System.out.println("Debug: Failed to retrieve the generated reservation ID.");
                        return Optional.empty();
                    }
                } else {
                    System.out.println("Debug: INSERT query did not affect any rows.");
                    return Optional.empty();
                }
            }
        } catch (Exception e) {
            System.out.println("Debug: Exception occurred during reservation: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public boolean cancelReservation(int userId, Room room) {
        String query = "DELETE FROM bookings WHERE room_id = ? AND user_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, room.getId());
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelReservation(int reservationId) {
        System.out.println("Debug: Preparing to cancel reservation with ID: " + reservationId);
        String query = "DELETE FROM bookings WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, reservationId);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Debug: Rows affected by cancellation query: " + rowsAffected);
            return rowsAffected > 0;
        } catch (Exception e) {
            System.out.println("Debug: Exception occurred while canceling reservation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean addRoom(Room room) {
        String query = """
            INSERT INTO rooms (name, max_people, price_per_night)
            VALUES (?, ?, ?)
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, room.getRoomName());
            stmt.setInt(2, room.getMaxPeople());
            stmt.setDouble(3, room.getPricePerNight());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getLastInsertedReservationId() {
        String query = "SELECT LAST_INSERT_ID() AS id"; // Ensure this works with your database
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if no ID is found
    }
}