package application.controllers;

import application.Main;
import application.models.Room;
import application.services.RoomService;
import application.ui.RoomCardFactory;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class BookingController {
    @FXML private TextField searchField;
    @FXML private ScrollPane scrollPane;
    @FXML private FlowPane roomFlowPane;
    @FXML private Label statusLabel;
    @FXML private Label welcomeLabel;
    @FXML private Label bookingStatsLabel;

    private final RoomService roomService = new RoomService();
    private final RoomCardFactory roomCardFactory = new RoomCardFactory(this);
    private int currentPage = 0;
    private final int PAGE_SIZE = 10;
    private boolean isLoading = false;

    @FXML
    public void initialize() {
        Main.bookingController = this;
        setupScrollPane();
        setupSearchField();

        // Welcome message
        welcomeLabel.setText("Welcome, " + Main.currentUsername);

        // Fetch and display booking statistics
        int activeBookings = roomService.getActiveBookingsCount(Main.currentUserId);
        int totalBookings = roomService.getTotalBookingsCount(Main.currentUserId);
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

    public void loadInitialRooms(int userId) {
        roomFlowPane.getChildren().clear();
        currentPage = 0;
        List<Room> rooms = roomService.fetchRooms(userId, "", PAGE_SIZE, currentPage * PAGE_SIZE);
        roomService.setAllRooms(rooms); // Store all rooms in the service
        rooms.forEach(room -> roomCardFactory.createRoomCard(room, roomFlowPane));
    }

    private void loadMoreRooms() {
        isLoading = true;
        currentPage++;
        loadRooms(searchField.getText().trim());
        isLoading = false;
    }

    private void loadRooms(String searchQuery) {
        List<Room> rooms = roomService.fetchRooms(Main.currentUserId, searchQuery, PAGE_SIZE, currentPage * PAGE_SIZE);
        rooms.forEach(room -> roomCardFactory.createRoomCard(room, roomFlowPane));
    }

    private void filterRooms(String searchQuery) {
        roomFlowPane.getChildren().clear();
        List<Room> filteredRooms = roomService.filterRooms(searchQuery);
        filteredRooms.forEach(room -> roomCardFactory.createRoomCard(room, roomFlowPane));
    }

    public void updateStatusLabel(String message) {
        statusLabel.setText(message);
    }

    public void handleReservation(Room room, LocalDate startDate, LocalDate endDate, int numberOfPeople) {
        if (startDate == null || endDate == null) {
            updateStatusLabel("Please select start and end dates.");
            return;
        }

        if (numberOfPeople > room.getMaxPeople()) {
            updateStatusLabel("Selected room cannot accommodate " + numberOfPeople + " people.");
            return;
        }

        Optional<String> result = roomService.reserveRoom(Main.currentUserId, room, startDate, endDate, numberOfPeople);
        if (result.isEmpty()) {
            updateStatusLabel("Booking successful for " + room.getRoomName() + ".");
        } else {
            updateStatusLabel("Booking failed: " + result.get());
        }
    }

    public void handleCancellation(Room room, int reservationId) {
        boolean success = roomService.cancelReservation(reservationId); // Use reservation ID
        if (success) {
            updateStatusLabel("Reservation for " + room.getRoomName() + " has been canceled.");
        } else {
            updateStatusLabel("Failed to cancel the reservation.");
        }
    }
}