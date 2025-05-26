package application.controllers;

import application.Main;
import application.models.Room;
import application.models.Reservation;
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

        // Fetch cancellable reservations to get room IDs
        List<Reservation> cancellableReservations = roomService.getAllCancellableReservations(userId);

        // Map room IDs from cancellable reservations
        List<Integer> reservedRoomIds = cancellableReservations.stream()
            .map(Reservation::getRoomId) // Use room_id instead of booking_id
            .toList();

        // Fetch all rooms
        List<Room> allRooms = roomService.fetchRooms(userId, "", PAGE_SIZE, currentPage * PAGE_SIZE);
        roomService.setAllRooms(allRooms); // Store all rooms in the service

        // Create cards for all rooms, marking reserved ones
        allRooms.forEach(room -> {
            boolean isReserved = reservedRoomIds.contains(room.getId());
            System.out.println("Debug: Creating room card for room ID: " + room.getId() + ", Reserved: " + isReserved);
            Reservation reservation = cancellableReservations.stream()
                .filter(res -> res.getRoomId() == room.getId()) // Match room_id
                .findFirst()
                .orElse(null);
            roomCardFactory.createRoomCard(room, roomFlowPane, isReserved, reservation);
        });
    }

    private void loadMoreRooms() {
        isLoading = true;
        currentPage++;
        loadRooms(searchField.getText().trim());
        isLoading = false;
    }

    private void loadRooms(String searchQuery) {
        List<Room> availableRooms = roomService.fetchRooms(Main.currentUserId, searchQuery, PAGE_SIZE, currentPage * PAGE_SIZE);
        availableRooms.forEach(room -> roomCardFactory.createRoomCard(room, roomFlowPane, false, null));
    }

    private void filterRooms(String searchQuery) {
        roomFlowPane.getChildren().clear();

        // Fetch cancellable reservations to get reservation IDs
        List<Reservation> cancellableReservations = roomService.getAllCancellableReservations(Main.currentUserId);

        // Filter all rooms
        List<Room> filteredRooms = roomService.filterRooms(searchQuery);

        // Map reservation IDs to their corresponding rooms
        List<Integer> reservedRoomIds = cancellableReservations.stream()
            .map(Reservation::getRoomId) // Use room_id
            .toList();

        // Create cards for filtered rooms, marking reserved ones
        filteredRooms.forEach(room -> {
            boolean isReserved = reservedRoomIds.contains(room.getId());
            Reservation reservation = cancellableReservations.stream()
                .filter(res -> res.getRoomId() == room.getId()) // Match room_id
                .findFirst()
                .orElse(null);
            roomCardFactory.createRoomCard(room, roomFlowPane, isReserved, reservation);
        });
    }

    public void updateStatusLabel(String message) {
        statusLabel.setText(message);
    }

    public void handleReservation(Room room, LocalDate startDate, LocalDate endDate, int numberOfPeople) {
        if (startDate == null || endDate == null) {
            updateStatusLabel("Please select start and end dates.");
            return;
        }

        if (endDate.isBefore(startDate)) {
            updateStatusLabel("End date cannot be before start date.");
            return;
        }

        if (numberOfPeople > room.getMaxPeople()) {
            updateStatusLabel("Selected room cannot accommodate " + numberOfPeople + " people.");
            return;
        }

        Optional<Integer> result = roomService.reserveRoom(Main.currentUserId, room, startDate, endDate, numberOfPeople);

        if (result.isEmpty()) {
            updateStatusLabel("Booking failed: The selected dates are already booked or an error occurred.");
        } else {
            int reservationId = result.get();
            System.out.println("Debug: Successfully retrieved reservation ID: " + reservationId);
            updateStatusLabel("Booking successful for " + room.getRoomName() + ".");
        }
    }

    public void handleCancellation(Room room, int reservationId) {
        if (reservationId <= 0) {
            updateStatusLabel("Invalid reservation ID.");
            return;
        }

        boolean success = roomService.cancelReservation(reservationId);
        if (success) {
            updateStatusLabel("Reservation for " + room.getRoomName() + " has been canceled.");
        } else {
            updateStatusLabel("Failed to cancel the reservation.");
        }
    }
}