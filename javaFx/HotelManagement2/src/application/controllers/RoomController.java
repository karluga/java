package application.controllers;

import application.DBConnection;
import application.Main;
import application.models.Room;
import application.models.Reservation;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
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
    private ObservableList<User> userList = FXCollections.observableArrayList();

    // Constants for pagination
    private static final int ROOM_PAGE_SIZE = 10;
    private static final int RESERVATION_PAGE_SIZE = 10;

    // Offsets for pagination
    private int roomOffset = 0;
    private int reservationOffset = 0;

    @FXML public TableView<Room> roomTable;
    @FXML private TableColumn<Room, String> roomNameCol;
    @FXML private TableColumn<Room, Double> roomPriceCol; // Add this line to bind the price column
    @FXML public TableView<Reservation> reservationsTable;
    @FXML private TableColumn<Reservation, String> reservedNameCol;
    @FXML private TableColumn<Reservation, String> reservedByCol;
    @FXML private TableColumn<Reservation, String> fromCol;
    @FXML private TableColumn<Reservation, String> toCol;

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField newRoomField;
    @FXML private ComboBox<User> userComboBox;
    @FXML private Label statusLabel;
    @FXML private Button addToReservationBtn;
    @FXML private TextField searchField;

    @FXML
    public void initialize() {
        Main.roomController = this;

        roomNameCol.setCellValueFactory(new PropertyValueFactory<>("roomName"));
        roomPriceCol.setCellValueFactory(new PropertyValueFactory<>("pricePerNight")); // Bind price column
        loadRooms(false, ""); // Load initial rooms

        reservedNameCol.setCellValueFactory(new PropertyValueFactory<>("roomName"));
        reservedByCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        fromCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        toCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        reservationsTable.setItems(FXCollections.observableArrayList());

        loadUsers();
        setupUserComboBox();

        // Load reservations from the database
        loadReservations(false); // Load initial reservations

        addToReservationBtn.setOnAction(event -> {
            Room selectedRoom = roomTable.getSelectionModel().getSelectedItem();
            LocalDate start = startDatePicker.getValue();
            LocalDate end = endDatePicker.getValue();
            User selectedUser = userComboBox.getValue();
            int numberOfPeople = 1;

            if (selectedRoom == null || start == null || end == null || selectedUser == null) {
                statusLabel.setText("Please select a room, user, and valid dates.");
                return;
            }

            if (start.isBefore(LocalDate.now()) || end.isBefore(LocalDate.now())) {
                statusLabel.setText("Reservation dates cannot be in the past.");
                return;
            }

            if (end.isBefore(start)) {
                statusLabel.setText("End date cannot be before start date.");
                return;
            }

            String customerName = selectedUser.getUsername();
            int userId = selectedUser.getId();
            double totalPrice = selectedRoom.getPricePerNight() * (end.toEpochDay() - start.toEpochDay() + 1);
            boolean booked = selectedRoom.addReservation(start, end, customerName, numberOfPeople, userId);
            if (booked) {
                Reservation newReservation = new Reservation(
                    selectedRoom.getRoomName(),
                    customerName,
                    start,
                    end,
                    false, // Default to unpaid
                    totalPrice
                );
                reservationsTable.getItems().add(0, newReservation); // Add to the top of the table
                highlightNewReservation(newReservation); // Highlight the new reservation
                reservationsTable.refresh();
                statusLabel.setText("Room reserved successfully.");
            } else {
                statusLabel.setText("Reservation failed: Time slot unavailable.");
            }
        });

        setupRoomTableScrollListener();
        setupReservationsTableScrollListener();
    }

private void setupRoomTableScrollListener() {
    Platform.runLater(() -> {
        ScrollBar scrollBar = getVerticalScrollbar(roomTable);
        if (scrollBar != null) {
            scrollBar.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.doubleValue() >= 0.7 && // Trigger at 30% from the bottom
                    roomTable.getItems().size() % ROOM_PAGE_SIZE == 0) {
                    showLoadingIndicator(true);
                    String searchQuery = searchField.getText().trim(); // Get current search query
                    loadRooms(true, searchQuery); // Pass search query to loadRooms
                    showLoadingIndicator(false);
                }
            });
        }
    });
}

private void setupReservationsTableScrollListener() {
    Platform.runLater(() -> {
        ScrollBar scrollBar = getVerticalScrollbar(reservationsTable);
        if (scrollBar != null) {
            scrollBar.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.doubleValue() >= 0.7 && // Trigger at 30% from the bottom
                    reservationsTable.getItems().size() % RESERVATION_PAGE_SIZE == 0) {
                    showLoadingIndicator(true);
                    loadReservations(true);
                    showLoadingIndicator(false);
                }
            });
        }
    });
}

private ScrollBar getVerticalScrollbar(TableView<?> table) {
    for (Node node : table.lookupAll(".scroll-bar")) {
        if (node instanceof ScrollBar) {
            ScrollBar bar = (ScrollBar) node;
            if (bar.getOrientation() == Orientation.VERTICAL) {
                return bar;
            }
        }
    }
    return null;
}

private void showLoadingIndicator(boolean show) {
    Platform.runLater(() -> {
        if (show) {
            statusLabel.setText("Loading...");
        } else {
            statusLabel.setText("");
        }
    });
}

    private void loadReservations(boolean loadMore) {
        if (!loadMore) {
            reservationsTable.getItems().clear();
            reservationOffset = 0; // Reset offset if not loading more
        }

        String query = """
            SELECT r.name AS room_name, b.customer_name, b.start_date, b.end_date, b.is_paid, b.total_price
            FROM bookings b
            JOIN rooms r ON b.room_id = r.id
            ORDER BY b.start_date ASC
            LIMIT ? OFFSET ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, RESERVATION_PAGE_SIZE);
            stmt.setInt(2, reservationOffset);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Reservation reservation = new Reservation(
                    rs.getString("room_name"),
                    rs.getString("customer_name"),
                    LocalDate.parse(rs.getString("start_date")),
                    LocalDate.parse(rs.getString("end_date")),
                    rs.getBoolean("is_paid"),
                    rs.getDouble("total_price")
                );
                reservationsTable.getItems().add(reservation);
            }
            reservationOffset += RESERVATION_PAGE_SIZE; // Increment offset for the next page
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Failed to load reservations.");
        }
    }

    private void loadUsers() {
        userList.clear();
        String query = "SELECT id, username FROM users ORDER BY username";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                userList.add(new User(rs.getInt("id"), rs.getString("username")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Failed to load users.");
        }
    }

    private void setupUserComboBox() {
        userComboBox.setItems(userList);
        userComboBox.setEditable(true);
        userComboBox.setPromptText("Select User");

        // Display username and ID in the dropdown, username in the button
        userComboBox.setCellFactory(lv -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                setText(empty ? null : user.getUsername() + " (ID: " + user.getId() + ")");
            }
        });
        userComboBox.setButtonCell(new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                setText(empty ? null : user.getUsername());
            }
        });

        // Synchronize editor text with selected User
        userComboBox.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (userComboBox.getValue() == null || !userComboBox.getValue().getUsername().equals(newValue)) {
                userComboBox.setValue(null); // Clear selection if text doesn't match a User
            }
        });

        // Handle selection and conversion from String to User
        userComboBox.setOnAction(event -> {
            String input = userComboBox.getEditor().getText().trim();
            User matchedUser = null;
            for (User user : userList) {
                if (user.getUsername().equalsIgnoreCase(input) || 
                    String.valueOf(user.getId()).equals(input)) {
                    matchedUser = user;
                    break;
                }
            }
            if (matchedUser != null) {
                userComboBox.setValue(matchedUser);
            } else {
                userComboBox.setValue(null); // Clear selection if no match
                statusLabel.setText("No matching user found.");
            }
        });

        // Clear selection if editor is cleared
        userComboBox.getEditor().setOnKeyReleased(event -> {
            if (userComboBox.getEditor().getText().isEmpty()) {
                userComboBox.setValue(null);
                userComboBox.setItems(userList);
            }
        });
    }

    @FXML
    public void handleSearch(ActionEvent event) {
        String searchQuery = searchField.getText().trim();
        loadRooms(false, searchQuery); // Pass search query to loadRooms
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
            loadRooms(false, ""); // Reload rooms without search query
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Failed to add room.");
        }
    }

    private void loadRooms(boolean loadMore, String searchQuery) {
        if (!loadMore) {
            roomTable.getItems().clear();
            roomOffset = 0; // Reset offset
        }

        String query = """
            SELECT * FROM rooms
            WHERE name LIKE ?
            LIMIT ? OFFSET ?
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + searchQuery + "%");
            stmt.setInt(2, ROOM_PAGE_SIZE);
            stmt.setInt(3, roomOffset);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Room room = new Room(rs.getInt("id"), rs.getString("name"), rs.getInt("max_people"));
                room.setPricePerNight(rs.getDouble("price_per_night")); // Set the price per night
                roomTable.getItems().add(room);
            }
            roomOffset += ROOM_PAGE_SIZE; // Increment offset
        } catch (SQLException e) {
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

    private void highlightNewReservation(Reservation reservation) {
        Platform.runLater(() -> {
            reservationsTable.getSelectionModel().select(reservation);
            reservationsTable.scrollTo(reservation);

            // Apply a temporary style to the row
            reservationsTable.lookupAll(".table-row-cell").forEach(node -> {
                if (node instanceof TableRow<?> row && row.getItem() == reservation) {
                    row.setStyle("-fx-background-color: yellow; -fx-text-fill: black;");
                    new Thread(() -> {
                        try {
                            Thread.sleep(1000); // Highlight for 1 second
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Platform.runLater(() -> row.setStyle("")); // Reset style
                    }).start();
                }
            });

            reservationsTable.getSelectionModel().clearSelection();
        });
    }

    // Inner class to represent a user in the ComboBox
    private static class User {
        private final int id;
        private final String username;

        public User(int id, String username) {
            this.id = id;
            this.username = username;
        }

        public int getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        @Override
        public String toString() {
            return username;
        }
    }
}