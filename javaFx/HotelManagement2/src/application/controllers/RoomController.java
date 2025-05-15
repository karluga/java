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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
    @FXML private TableColumn<Room, Double> roomPriceCol;
    @FXML public TableView<Reservation> reservationsTable;
    @FXML private TableColumn<Reservation, String> reservedNameCol;
    @FXML private TableColumn<Reservation, String> reservedByCol;
    @FXML private TableColumn<Reservation, String> fromCol;
    @FXML private TableColumn<Reservation, String> toCol;
    @FXML private TableColumn<Reservation, Boolean> paidCol;

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField newRoomField;
    @FXML private TextField newRoomPriceField;
    @FXML private ComboBox<User> userComboBox;
    @FXML private Label statusLabel;
    @FXML private Button addToReservationBtn;
    @FXML private TextField searchField;

    @FXML private TextField filterNameField;
    @FXML private DatePicker filterStartDatePicker;
    @FXML private DatePicker filterEndDatePicker;
    @FXML private CheckBox filterUnpaidCheckBox;

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
        paidCol.setCellValueFactory(new PropertyValueFactory<>("paid"));
        reservationsTable.setItems(FXCollections.observableArrayList());

        paidCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean isPaid, boolean empty) {
                super.updateItem(isPaid, empty);
                if (empty || getTableRow().getItem() == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    Reservation reservation = (Reservation) getTableRow().getItem();
                    double progress = reservation.getPaidAmount() / reservation.getTotalPrice();
                    LocalDate today = LocalDate.now();
                    LocalDate overdueDate = reservation.getEndDate().plusDays(1);

                    ProgressBar progressBar = new ProgressBar(progress);
                    progressBar.setPrefWidth(80);

                    Label amountLabel = new Label("Paid: $" + reservation.getPaidAmount() + " / $" + reservation.getTotalPrice());

                    Button partialPayButton = new Button("Partial Pay");
                    partialPayButton.setOnAction(event -> handlePartialPayment(reservation));

                    Button markPaidButton = new Button("Mark as Paid");
                    markPaidButton.setOnAction(event -> markAsPaid(reservation, true));

                    HBox cellContent;

                    if (isPaid == null || isPaid == false) {
                        // Unpaid or partially paid
                        if (reservation.getPaidAmount() > 0) {
                            // Show progress bar for partially paid
                            cellContent = new HBox(10, progressBar, amountLabel, partialPayButton, markPaidButton);
                        } else {
                            // No progress bar for unpaid
                            cellContent = new HBox(10, amountLabel, partialPayButton, markPaidButton);
                        }

                        if (today.isAfter(overdueDate)) {
                            // Red background for very late payments
                            setStyle("-fx-background-color: red; -fx-text-fill: white;");
                        } else {
                            setStyle("-fx-background-color: orange; -fx-text-fill: black;");
                        }

                        // Cancel button for unpaid reservations with a future start date
                        if (reservation.getStartDate().isAfter(today)) {
                            Hyperlink cancelLink = new Hyperlink("Cancel");
                            cancelLink.setOnAction(event -> cancelReservation(reservation));
                            cellContent.getChildren().add(cancelLink);
                        }
                    } else {
                        // Fully paid
                        Label fullyPaidLabel = new Label("Paid: $" + reservation.getTotalPrice());
                        cellContent = new HBox(10, fullyPaidLabel);
                        setStyle("-fx-background-color: green; -fx-text-fill: white;");
                    }

                    setGraphic(cellContent);
                    setText(null);
                }
            }
        });

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
                    null, // Default to unpaid
                    totalPrice,
                    0.0 // Initial paid amount is 0
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
            SELECT r.name AS room_name, b.customer_name, b.start_date, b.end_date, b.is_paid, b.total_price,
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
                    rs.getDouble("total_price"),
                    rs.getDouble("total_paid") // Include pre-fetched paid amount
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
        String newRoomPriceText = newRoomPriceField.getText().trim();

        if (newRoomName.isEmpty()) {
            statusLabel.setText("Room name cannot be empty.");
            return;
        }

        double newRoomPrice;
        try {
            newRoomPrice = Double.parseDouble(newRoomPriceText);
            if (newRoomPrice <= 0) {
                statusLabel.setText("Price must be greater than zero.");
                return;
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid price format.");
            return;
        }

        String insertQuery = "INSERT INTO rooms (name, price_per_night) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.setString(1, newRoomName);
            stmt.setDouble(2, newRoomPrice);
            stmt.executeUpdate();
            statusLabel.setText("Room added successfully.");
            newRoomField.clear();
            newRoomPriceField.clear();
            loadRooms(false, ""); // Reload rooms without search query
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Failed to add room.");
        }
    }

    @FXML
    public void handleFilterReservations(ActionEvent event) {
        String nameFilter = filterNameField.getText().trim();
        LocalDate startDate = filterStartDatePicker.getValue();
        LocalDate endDate = filterEndDatePicker.getValue();
        boolean unpaidOnly = filterUnpaidCheckBox.isSelected();

        reservationsTable.getItems().clear();
        reservationOffset = 0;

        String query = """
            SELECT r.name AS room_name, b.customer_name, b.start_date, b.end_date, b.is_paid, b.total_price
            FROM bookings b
            JOIN rooms r ON b.room_id = r.id
            WHERE r.name LIKE ? AND (? IS NULL OR b.start_date >= ?) AND (? IS NULL OR b.end_date <= ?) AND (? = FALSE OR b.is_paid = FALSE)
            ORDER BY b.start_date ASC
            LIMIT ? OFFSET ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + nameFilter + "%");
            stmt.setObject(2, startDate, java.sql.Types.DATE);
            stmt.setObject(3, startDate, java.sql.Types.DATE);
            stmt.setObject(4, endDate, java.sql.Types.DATE);
            stmt.setObject(5, endDate, java.sql.Types.DATE);
            stmt.setBoolean(6, unpaidOnly);
            stmt.setInt(7, RESERVATION_PAGE_SIZE);
            stmt.setInt(8, reservationOffset);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Reservation reservation = new Reservation(
                    rs.getString("room_name"),
                    rs.getString("customer_name"),
                    LocalDate.parse(rs.getString("start_date")),
                    LocalDate.parse(rs.getString("end_date")),
                    rs.getBoolean("is_paid"),
                    rs.getDouble("total_price"),
                    0.0 // Default paid amount as 0 for filtered reservations
                );
                reservationsTable.getItems().add(reservation);
            }
            reservationOffset += RESERVATION_PAGE_SIZE;
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Failed to filter reservations.");
        }
    }

    private void markAsPaid(Reservation reservation, boolean fullyPaid) {
        // Show confirmation dialog
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Payment");
        confirmationAlert.setHeaderText("Are you sure you want to mark this reservation as paid?");
        confirmationAlert.setContentText("Room: " + reservation.getRoomName() + "\nCustomer: " + reservation.getCustomerName());

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String updateQuery = """
                    UPDATE bookings
                    SET is_paid = ?, total_price = total_price
                    WHERE room_id = (SELECT id FROM rooms WHERE name = ?) AND customer_name = ? AND start_date = ? AND end_date = ?
                """;
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                    stmt.setObject(1, fullyPaid ? 1 : 0); // 1 for fully paid, 0 for partially paid
                    stmt.setString(2, reservation.getRoomName());
                    stmt.setString(3, reservation.getCustomerName());
                    stmt.setObject(4, reservation.getStartDate());
                    stmt.setObject(5, reservation.getEndDate());
                    stmt.executeUpdate();

                    reservation.setPaid(fullyPaid);
                    reservationsTable.refresh();
                    statusLabel.setText(fullyPaid ? "Reservation marked as fully paid." : "Reservation marked as partially paid.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    statusLabel.setText("Failed to update payment status.");
                }
            }
        });
    }

    private void handlePartialPayment(Reservation reservation) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Partial Payment");
        dialog.setHeaderText("Enter payment details for " + reservation.getRoomName());
    
        // Calculate price per night
        double pricePerNight = reservation.getTotalPrice() / reservation.getNumberOfNights();
    
        // Create UI components for the dialog
        Label pricePerNightLabel = new Label("Price per night: $" + pricePerNight);
        Label totalAmountLabel = new Label("Total: $" + reservation.getTotalPrice());
        Label remainingBalanceLabel = new Label("Remaining: $" + reservation.getRemainingBalance());
        Label nightsToPayLabel = new Label("Nights to pay:");
        TextField nightsToPayField = new TextField("1");
        nightsToPayField.setPrefWidth(50);
        Button plusButton = new Button("+");
    
        // Layout for the dialog
        HBox inputBox = new HBox(10, nightsToPayLabel, nightsToPayField, plusButton);
        VBox dialogContent = new VBox(10, pricePerNightLabel, totalAmountLabel, remainingBalanceLabel, inputBox);
        dialog.getDialogPane().setContent(dialogContent);
    
        // Add OK and Cancel buttons
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    
        // Handle the plus button to increment the nights to pay
        plusButton.setOnAction(event -> {
            try {
                int currentNights = Integer.parseInt(nightsToPayField.getText());
                int maxNights = (int) Math.ceil(reservation.getRemainingBalance() / pricePerNight);
                if (currentNights < maxNights) {
                    nightsToPayField.setText(String.valueOf(currentNights + 1));
                }
            } catch (NumberFormatException e) {
                nightsToPayField.setText("1");
            }
        });
    
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    int nightsToPay = Integer.parseInt(nightsToPayField.getText());
                    double payment = nightsToPay * pricePerNight;
    
                    if (payment <= 0 || payment > reservation.getRemainingBalance()) {
                        statusLabel.setText("Invalid payment amount.");
                        return;
                    }
    
                    String insertQuery = """
                        INSERT INTO payments (reservation_id, amount_paid)
                        VALUES ((SELECT id FROM bookings WHERE room_id = (SELECT id FROM rooms WHERE name = ?) AND customer_name = ? AND start_date = ? AND end_date = ?), ?)
                    """;
                    String updateQuery = """
                        UPDATE bookings
                        SET is_paid = CASE WHEN ? >= total_price THEN 1 ELSE 0 END
                        WHERE room_id = (SELECT id FROM rooms WHERE name = ?) AND customer_name = ? AND start_date = ? AND end_date = ?
                    """;
    
                    try (Connection conn = DBConnection.getConnection();
                         PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                         PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        // Insert payment record
                        insertStmt.setString(1, reservation.getRoomName());
                        insertStmt.setString(2, reservation.getCustomerName());
                        insertStmt.setObject(3, reservation.getStartDate());
                        insertStmt.setObject(4, reservation.getEndDate());
                        insertStmt.setDouble(5, payment);
                        insertStmt.executeUpdate();
    
                        // Update is_paid column
                        updateStmt.setDouble(1, reservation.getPaidAmount() + payment);
                        updateStmt.setString(2, reservation.getRoomName());
                        updateStmt.setString(3, reservation.getCustomerName());
                        updateStmt.setObject(4, reservation.getStartDate());
                        updateStmt.setObject(5, reservation.getEndDate());
                        updateStmt.executeUpdate();
    
                        reservation.setPaidAmount(reservation.getPaidAmount() + payment);
                        if (reservation.getPaidAmount() >= reservation.getTotalPrice()) {
                            reservation.setPaid(true);
                        }
                        reservationsTable.refresh();
                        statusLabel.setText("Partial payment recorded.");
                    }
                } catch (NumberFormatException | SQLException e) {
                    e.printStackTrace();
                    statusLabel.setText("Failed to record payment.");
                }
            }
        });
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

    private void cancelReservation(Reservation reservation) {
        String deleteQuery = "DELETE FROM bookings WHERE room_id = (SELECT id FROM rooms WHERE name = ?) AND customer_name = ? AND start_date = ? AND end_date = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
            stmt.setString(1, reservation.getRoomName());
            stmt.setString(2, reservation.getCustomerName());
            stmt.setObject(3, reservation.getStartDate());
            stmt.setObject(4, reservation.getEndDate());
            stmt.executeUpdate();

            reservationsTable.getItems().remove(reservation); // Remove from table
            reservationsTable.refresh();
            statusLabel.setText("Reservation canceled successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Failed to cancel reservation.");
        }
    }

    @FXML
    public void handleBackToLogin(ActionEvent event) {
        try {
            Main.mainStage.hide();
            Main.loginStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Failed to navigate to login.");
        }
    }

    @FXML
    public void handleViewMyReservations(ActionEvent event) {
        try {
            Main.mainStage.hide();
            Main.myReservationsStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Failed to navigate to My Reservations.");
        }
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