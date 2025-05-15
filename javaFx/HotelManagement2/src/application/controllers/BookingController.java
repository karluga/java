package application.controllers;

import java.time.LocalDate;
import application.Main;
import application.models.Room;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import application.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BookingController {
    @FXML private TextField searchField;
    @FXML private ScrollPane scrollPane;
    @FXML private FlowPane roomFlowPane;
    @FXML private Label statusLabel;
    @FXML private Label welcomeLabel;
    @FXML private Label bookingStatsLabel;

    private List<Room> allRooms = new ArrayList<>();
    private int currentPage = 0;
    private final int PAGE_SIZE = 10;
    private boolean isLoading = false;

    @FXML
    public void initialize() {
        Main.bookingController = this;
        setupScrollPane();
        loadInitialRooms();
        setupSearchField();

        // Welcome message
        welcomeLabel.setText("Welcome, " + Main.currentUsername);

        // Fetch and display booking statistics (dummy values for now)
        int activeBookings = getActiveBookingsCount(Main.currentUserId);
        int totalBookings = getTotalBookingsCount(Main.currentUserId);
        bookingStatsLabel.setText("Active Bookings: " + activeBookings + " | Total Bookings: " + totalBookings);
    }

    private void setupScrollPane() {
        roomFlowPane.setHgap(10);
        roomFlowPane.setVgap(10);
        scrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() > 0.9 && !isLoading) {
                loadMoreRooms();
            }
        });
    }

    private void setupSearchField() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterRooms(newVal.trim());
        });
    }

    private void loadInitialRooms() {
        allRooms.clear();
        roomFlowPane.getChildren().clear();
        currentPage = 0;
        loadRooms("");
    }

    private void loadMoreRooms() {
        isLoading = true;
        currentPage++;
        loadRooms(searchField.getText().trim());
        isLoading = false;
    }

    private void loadRooms(String searchQuery) {
        String query;
        if (searchQuery.isEmpty()) {
            query = """
                SELECT r.id, r.name, r.max_people, r.price_per_night, b.end_date
                FROM rooms r LEFT JOIN bookings b ON r.id = b.room_id AND b.user_id = ?
                WHERE b.end_date IS NULL OR b.end_date > CURRENT_DATE
                ORDER BY b.end_date DESC, r.id LIMIT ? OFFSET ?
            """;
        } else {
            query = """
                SELECT r.id, r.name, r.max_people, r.price_per_night, b.end_date
                FROM rooms r LEFT JOIN bookings b ON r.id = b.room_id AND b.user_id = ?
                WHERE (b.end_date IS NULL OR b.end_date > CURRENT_DATE) AND r.name LIKE ?
                ORDER BY b.end_date DESC, r.id LIMIT ? OFFSET ?
            """;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            int paramIndex = 1;
            stmt.setInt(paramIndex++, Main.currentUserId);
            if (!searchQuery.isEmpty()) {
                stmt.setString(paramIndex++, "%" + searchQuery + "%");
            }
            stmt.setInt(paramIndex++, PAGE_SIZE);
            stmt.setInt(paramIndex, currentPage * PAGE_SIZE);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Room room = new Room(rs.getInt("id"), rs.getString("name"), rs.getInt("max_people"));
                room.setPricePerNight(rs.getDouble("price_per_night"));
                allRooms.add(room);
                addRoomCard(room, rs.getDate("end_date") != null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error loading rooms.");
        }
    }

    private void filterRooms(String searchQuery) {
        roomFlowPane.getChildren().clear();
        if (searchQuery.isEmpty()) {
            allRooms.forEach(room -> addRoomCard(room, false));
        } else {
            allRooms.stream()
                    .filter(room -> room.getRoomName().toLowerCase().contains(searchQuery.toLowerCase()))
                    .forEach(room -> addRoomCard(room, false));
        }
    }

    private void addRoomCard(Room room, boolean isPreviouslyReserved) {
        VBox card = new VBox(5);
        card.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: #f0f0f0;");
        card.setPrefSize(150, 200);

        if (isPreviouslyReserved) {
            card.setStyle("-fx-background-color: #d4edda; -fx-border-color: #155724; -fx-padding: 10;");
        }

        Text nameText = new Text("Room: " + room.getRoomName());
        Text priceText = new Text("Price: $" + String.format("%.2f", room.getPricePerNight()) + "/night");
        Text maxPeopleText = new Text("Max People: " + room.getMaxPeople());

        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Start Date");
        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("End Date");

        Button reserveButton = new Button("Reserve");
        reserveButton.setOnAction(event -> handleReservation(room, reserveButton, startDatePicker, endDatePicker));

        if (room.getMaxPeople() > 1) {
            Slider peopleSlider = new Slider(1, room.getMaxPeople(), 1);
            peopleSlider.setMajorTickUnit(1);
            peopleSlider.setMinorTickCount(0);
            peopleSlider.setSnapToTicks(true);
            Text peopleText = new Text("People: 1");
            peopleSlider.valueProperty().addListener((obs, oldVal, newVal) -> 
                peopleText.setText("People: " + newVal.intValue()));
            card.getChildren().addAll(peopleSlider, peopleText);
        }

        card.getChildren().addAll(nameText, priceText, maxPeopleText, startDatePicker, endDatePicker, reserveButton);
        roomFlowPane.getChildren().add(card);
    }

    private void handleReservation(Room room, Button reserveButton, DatePicker startDatePicker, DatePicker endDatePicker) {
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();
        int numberOfPeople = getNumberOfPeopleFromCard(reserveButton.getParent());

        if (start == null || end == null) {
            statusLabel.setText("Please select start and end dates.");
            return;
        }

        if (numberOfPeople > room.getMaxPeople()) {
            statusLabel.setText("Selected room cannot accommodate " + numberOfPeople + " people.");
            return;
        }

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                 "INSERT INTO bookings (room_id, user_id, start_date, end_date, number_of_people, is_paid, total_price) VALUES (?, ?, ?, ?, ?, ?, ?)")) {

            stmt.setInt(1, room.getId());
            stmt.setInt(2, Main.currentUserId);
            stmt.setString(3, start.toString());
            stmt.setString(4, end.toString());
            stmt.setInt(5, numberOfPeople);
            stmt.setNull(6, java.sql.Types.BOOLEAN); // is_paid defaults to NULL
            long nights = java.time.temporal.ChronoUnit.DAYS.between(start, end);
            double totalPrice = nights * room.getPricePerNight();
            stmt.setDouble(7, totalPrice);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                statusLabel.setText("Booking successful for " + room.getRoomName() + ".");
                reserveButton.setText("Reserved");
                reserveButton.setDisable(true);

                // Highlight
                Parent card = reserveButton.getParent();
                card.setStyle("-fx-background-color: #d4edda; -fx-border-color: #155724; -fx-padding: 10;");
            } else {
                statusLabel.setText("Booking failed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("An error occurred while booking.");
        }
    }

    private int getNumberOfPeopleFromCard(Parent card) {
        for (javafx.scene.Node node : card.getChildrenUnmodifiable()) {
            if (node instanceof Slider) {
                return (int) ((Slider) node).getValue();
            }
        }
        return 1;
    }

    private int getActiveBookingsCount(Integer userId) {
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

    private int getTotalBookingsCount(Integer userId) {
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
}