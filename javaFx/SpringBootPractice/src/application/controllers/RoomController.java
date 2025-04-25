package application.controllers;

import application.DBConnection;
import application.Main;
import application.models.Room;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class RoomController {
    public static ObservableList<Room> roomList = FXCollections.observableArrayList();
    public ObservableList<Room> selectedRooms = FXCollections.observableArrayList();

    @FXML public TableView<Room> roomTable;
    @FXML private TableColumn<Room, String> roomNameCol;
    @FXML public TableView<Room> reservationsTable;
    @FXML private TableColumn<Room, String> reservedNameCol;
    @FXML private TableColumn<Room, String> reservedByCol;
    @FXML private TableColumn<Room, String> fromCol;
    @FXML private TableColumn<Room, String> toCol;

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField newRoomField;
    @FXML private TextField customerNameField;
    @FXML private Label statusLabel;
    @FXML private Button addToReservationBtn;
    @FXML private TextField searchField;

    @FXML
    public void initialize() {
        Main.roomController = this;

        roomNameCol.setCellValueFactory(new PropertyValueFactory<>("roomName"));
        loadRooms("");

        reservedNameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getRoomName()));
        reservedByCol.setCellValueFactory(data -> {
            Room room = data.getValue();
            return new javafx.beans.property.SimpleStringProperty(getLastReservationCustomer(room.getId()));
        });
        fromCol.setCellValueFactory(data -> {
            Room room = data.getValue();
            return new javafx.beans.property.SimpleStringProperty(getLastReservationStartDate(room.getId()));
        });
        toCol.setCellValueFactory(data -> {
            Room room = data.getValue();
            return new javafx.beans.property.SimpleStringProperty(getLastReservationEndDate(room.getId()));
        });
        reservationsTable.setItems(selectedRooms);

        addToReservationBtn.setOnAction(event -> {
            Room selectedRoom = roomTable.getSelectionModel().getSelectedItem();
            LocalDate start = startDatePicker.getValue();
            LocalDate end = endDatePicker.getValue();
            String customerName = getCustomerNameById(Main.currentUserId); // Retrieve customer name using ID
            int numberOfPeople = 1; // Default to 1 person for this context

            boolean booked = selectedRoom.addReservation(start, end, customerName, numberOfPeople, Main.currentUserId); // Pass all required parameters
            if (booked) {
                selectedRooms.add(selectedRoom);
                reservationsTable.refresh();
                statusLabel.setText("Room reserved successfully.");
            } else {
                statusLabel.setText("Reservation failed: Time slot unavailable.");
            }
        });
    }

    @FXML
    public void handleSearch(ActionEvent event) {
        String searchQuery = searchField.getText().trim();
        loadRooms(searchQuery); // Use the search query to filter rooms
        System.out.println("Search completed for query: " + searchQuery);
    }

    @FXML
    public void handleAddRoom(ActionEvent event) {
        String newRoomName = newRoomField.getText().trim();
        if (newRoomName.isEmpty()) {
            statusLabel.setText("Room name cannot be empty.");
            return;
        }

        String insertQuery = "INSERT INTO rooms (name) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.setString(1, newRoomName);
            stmt.executeUpdate();
            statusLabel.setText("Room added successfully.");
            newRoomField.clear();
            loadRooms(""); // Reload the room list
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Failed to add room.");
        }
    }

    public void loadRooms(String searchQuery) {
        roomList.clear();
        String query = searchQuery.isEmpty() 
            ? "SELECT id, name, max_people FROM rooms ORDER BY id DESC LIMIT 30" 
            : "SELECT id, name, max_people FROM rooms WHERE name LIKE ? ORDER BY id DESC LIMIT 30";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            if (!searchQuery.isEmpty()) {
                stmt.setString(1, "%" + searchQuery + "%");
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                roomList.add(new Room(rs.getInt("id"), rs.getString("name"), rs.getInt("max_people")));
            }
            roomTable.setItems(roomList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String getLastReservationCustomer(int roomId) {
        String query = "SELECT customer_name FROM bookings WHERE room_id = ? ORDER BY id DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("customer_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }
    
    private String getLastReservationStartDate(int roomId) {
        String query = "SELECT start_date FROM bookings WHERE room_id = ? ORDER BY id DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("start_date");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "-";
    }
    
    private String getLastReservationEndDate(int roomId) {
        String query = "SELECT end_date FROM bookings WHERE room_id = ? ORDER BY id DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("end_date");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "-";
    }

    private String getCustomerNameById(int userId) {
        String customerName = "Unknown Customer";
        String query = "SELECT name FROM users WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                customerName = resultSet.getString("name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customerName;
    }
}