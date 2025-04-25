package application.controllers;

import java.time.LocalDate;
import application.Main;
import application.models.Booking;
import application.models.Room;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import application.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BookingController {
    @FXML private TextField nameField;
    @FXML private ComboBox<Room> roomComboBox;
    @FXML private Label statusLabel;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TableView<Booking> bookingTable;
    @FXML private TableColumn<Booking, String> roomNameCol;
    @FXML private TableColumn<Booking, Integer> userIdCol; // Replace customerNameCol with userIdCol
    @FXML private TableColumn<Booking, String> startDateCol;
    @FXML private TableColumn<Booking, String> endDateCol;
    @FXML private TextField searchField;
    @FXML private Slider peopleSlider;

    @FXML
    public void initialize() {
        Main.bookingController = this;
        roomComboBox.setItems(RoomController.roomList);
        roomComboBox.setOnAction(event -> updatePeopleSlider()); // Add listener for room selection
        roomNameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(getRoomNameById(data.getValue().getRoomId())));
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId")); // Set userId column
        startDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        peopleSlider.setDisable(true); // Initially disabled
        peopleSlider.setValue(1); // Default to one person
        peopleSlider.setMajorTickUnit(1);
        peopleSlider.setMinorTickCount(0);
        peopleSlider.setSnapToTicks(true);
        loadBookings("");
    }

    private void updatePeopleSlider() {
        Room selectedRoom = roomComboBox.getValue();
        if (selectedRoom != null) {
            peopleSlider.setMax(selectedRoom.getMaxPeople()); // Set max value dynamically
            peopleSlider.setDisable(false); // Enable slider
        } else {
            peopleSlider.setDisable(true); // Disable slider if no room is selected
            peopleSlider.setValue(1); // Reset to default
        }
    }

    private String getRoomNameById(int roomId) {
        for (Room room : RoomController.roomList) {
            if (room.getId() == roomId) {
                return room.getRoomName();
            }
        }
        return "Unknown Room";
    }

    public void handleBooking() {
        Room selectedRoom = roomComboBox.getValue();
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();
        int numberOfPeople = (int) peopleSlider.getValue();

        if (selectedRoom == null || start == null || end == null) {
            statusLabel.setText("Please fill in all fields.");
            return;
        }

        if (numberOfPeople > selectedRoom.getMaxPeople()) {
            statusLabel.setText("Selected room cannot accommodate more than " + selectedRoom.getMaxPeople() + " people.");
            return;
        }

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement("INSERT INTO bookings (room_id, user_id, customer_name, start_date, end_date, number_of_people) VALUES (?, ?, ?, ?, ?, ?)")) {

            if (Main.currentUserId != null && isUserView) {
                stmt.setInt(1, selectedRoom.getId());
                stmt.setInt(2, Main.currentUserId);
                stmt.setNull(3, java.sql.Types.VARCHAR); // No manual name
            } else {
                stmt.setInt(1, selectedRoom.getId());
                stmt.setNull(2, java.sql.Types.INTEGER); // No user ID
                stmt.setString(3, nameField.getText());
            }

            stmt.setString(4, start.toString());
            stmt.setString(5, end.toString());
            stmt.setInt(6, numberOfPeople);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                statusLabel.setText("Booking successful.");
                loadBookings(""); // Refresh bookings
            } else {
                statusLabel.setText("Booking failed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("An error occurred while booking.");
        }
    }

    private void loadBookings(String searchQuery) {
        bookingTable.getItems().clear();
        String query = "SELECT * FROM bookings WHERE user_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, Main.currentUserId); // Filter by logged-in user ID
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int roomId = resultSet.getInt("room_id");
                int userId = resultSet.getInt("user_id");
                String startDate = resultSet.getString("start_date");
                String endDate = resultSet.getString("end_date");
                int numberOfPeople = resultSet.getInt("number_of_people");

                Booking booking = new Booking(roomId, userId, startDate, endDate, numberOfPeople);
                bookingTable.getItems().add(booking);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
