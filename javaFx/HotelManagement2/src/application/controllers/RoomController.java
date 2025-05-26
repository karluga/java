package application.controllers;

import application.Main;
import application.models.Room;
import application.models.Reservation;
import application.models.User;
import application.services.RoomService;
import application.services.ReservationService;
import application.services.UserService;
import application.services.PaymentService;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.geometry.Orientation;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class RoomController {
    private final RoomService roomService = new RoomService();
    private final ReservationService reservationService = new ReservationService();
    private final UserService userService = new UserService();
    private final PaymentService paymentService = new PaymentService();

    public static ObservableList<Room> roomList = FXCollections.observableArrayList();
    public ObservableList<Reservation> reservationList = FXCollections.observableArrayList();
    public ObservableList<User> userList = FXCollections.observableArrayList();

    @FXML private TableView<Room> roomTable;
    @FXML private TableColumn<Room, String> roomNameCol;
    @FXML private TableColumn<Room, Double> roomPriceCol;
    @FXML private TableColumn<Room, Integer> roomMaxPeopleCol;
    @FXML private TableView<Reservation> reservationsTable;
    @FXML private TableColumn<Reservation, String> reservedNameCol;
    @FXML private TableColumn<Reservation, String> reservedByCol;
    @FXML private TableColumn<Reservation, String> fromCol;
    @FXML private TableColumn<Reservation, String> toCol;
    @FXML private TableColumn<Reservation, Boolean> paidCol;
    @FXML private ComboBox<User> userComboBox;
    @FXML private TextField searchField;
    @FXML private Label statusLabel;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Spinner<Integer> numberOfPeopleSpinner;
    @FXML private Slider numberOfPeopleSlider;
    @FXML private Button reserveButton;
    @FXML private TextField newRoomMaxPeopleField;
    @FXML private TextField newRoomNameField;
    @FXML private TextField newRoomPriceField;

    private static final int PAGE_SIZE = 10;
    private int roomOffset = 0;
    private int reservationOffset = 0;

    @FXML
    public void initialize() {
        Main.roomController = this;

        setupRoomTable();
        setupReservationTable();
        setupPaidColumn();
        setupUserComboBox();

        // Bind handleReservation to reserveButton
        reserveButton.setOnAction(event -> handleReservation());

        // Add listeners for scrolling
        setupRoomTableScrollListener();
        setupReservationsTableScrollListener();

        // Add listener to update slider when a room is selected
        roomTable.getSelectionModel().selectedItemProperty().addListener((obs, oldRoom, newRoom) -> {
            if (newRoom != null) {
                numberOfPeopleSlider.setMax(newRoom.getMaxPeople());
                numberOfPeopleSlider.setValue(1); // Default to 1 person
            }
        });

        loadInitialReservations();
        loadUsers();
    }

    private void setupRoomTable() {
        roomNameCol.setCellValueFactory(new PropertyValueFactory<>("roomName"));
        roomPriceCol.setCellValueFactory(new PropertyValueFactory<>("pricePerNight"));
        roomMaxPeopleCol.setCellValueFactory(new PropertyValueFactory<>("maxPeople")); // Bind max people column
        roomTable.setItems(roomList);
    }

    private void setupReservationTable() {
        reservedNameCol.setCellValueFactory(new PropertyValueFactory<>("roomName"));
        reservedByCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        fromCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        toCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        paidCol.setCellValueFactory(new PropertyValueFactory<>("paid"));

        reservationsTable.setItems(reservationList);
    }

    // Updated method to set up the paid column logic
    private void setupPaidColumn() {
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
                    partialPayButton.setOnAction(event -> paymentService.handlePartialPayment(reservation, reservationsTable, statusLabel));

                    Button markPaidButton = new Button("Mark as Paid");
                    markPaidButton.setOnAction(event -> paymentService.markAsPaid(reservation, true));

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
                            cancelLink.setOnAction(event -> {
                                boolean success = roomService.cancelReservation(reservation.getId());
                                if (success) {
                                    reservationList.remove(reservation);
                                    statusLabel.setText("Reservation canceled successfully.");
                                } else {
                                    statusLabel.setText("Failed to cancel reservation.");
                                }
                            });
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
    }

    @FXML
    public void handleCancellation(Reservation reservation) {
        if (reservation == null) {
            statusLabel.setText("Please select a reservation to cancel.");
            System.out.println("Debug: No reservation selected for cancellation.");
            return;
        }

        System.out.println("Debug: Attempting to cancel reservation with ID: " + reservation.getId());
        System.out.println("Debug: Reservation details - Room: " + reservation.getRoomName() +
                           ", User: " + reservation.getCustomerName() +
                           ", From: " + reservation.getStartDate() +
                           ", To: " + reservation.getEndDate());

        boolean success = roomService.cancelReservation(reservation.getId());
        if (success) {
            statusLabel.setText("Reservation canceled successfully.");
            reservationList.remove(reservation);
            System.out.println("Debug: Reservation canceled successfully.");
        } else {
            statusLabel.setText("Failed to cancel reservation.");
            System.out.println("Debug: Failed to cancel reservation with ID: " + reservation.getId());
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

    private void loadInitialRooms() {
        roomList.clear();
        roomOffset = 0;
        List<Room> rooms = roomService.fetchRooms(Main.currentUserId, "", PAGE_SIZE, roomOffset);
        roomList.addAll(rooms);
    }

    private void loadInitialReservations() {
        reservationList.clear();
        reservationOffset = 0;
        List<Reservation> reservations = reservationService.fetchReservations(PAGE_SIZE, reservationOffset);
        reservationList.addAll(reservations);
        reservationOffset += reservations.size();
    }

    private void loadMoreReservations() {
        List<Reservation> moreReservations = reservationService.fetchReservations(PAGE_SIZE, reservationOffset);
        if (!moreReservations.isEmpty()) {
            reservationList.addAll(moreReservations);
            reservationOffset += moreReservations.size();
        }
    }

    private void setupRoomTableScrollListener() {
        Platform.runLater(() -> {
            ScrollBar scrollBar = getVerticalScrollbar(roomTable);
            if (scrollBar != null) {
                scrollBar.valueProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal.doubleValue() >= 0.7 && // Trigger at 30% from the bottom
                        roomTable.getItems().size() % PAGE_SIZE == 0) {
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
                        reservationsTable.getItems().size() % PAGE_SIZE == 0) {
                        showLoadingIndicator(true);
                        loadMoreReservations();
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

    private void loadRooms(boolean loadMore, String searchQuery) {
        if (!loadMore) {
            roomList.clear();
            roomOffset = 0; // Reset offset
        }

        List<Room> rooms = roomService.fetchRooms(Main.currentUserId, searchQuery, PAGE_SIZE, roomOffset);
        roomList.addAll(rooms);
        roomOffset += rooms.size();
    }

    private void highlightNewReservation(Reservation reservation) {
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
                    row.setStyle(""); // Reset style
                }).start();
            }
        });
    }

    private void loadUsers() {
        userList.clear();
        List<User> users = userService.fetchUsers();
        if (users != null && !users.isEmpty()) {
            userList.addAll(users);
        } else {
            statusLabel.setText("Error: No users found.");
            System.err.println("Error: No users found or fetchUsers() returned null.");
        }
    }

    @FXML
    public void handleSearch() {
        String searchQuery = searchField.getText().trim();
        roomList.setAll(roomService.filterRooms(searchQuery));
    }

    @FXML
    public void handleReservation() {
        Room selectedRoom = roomTable.getSelectionModel().getSelectedItem();
        User selectedUser = userComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        int numberOfPeople = (int) numberOfPeopleSlider.getValue();

        if (selectedRoom == null) {
            statusLabel.setText("Please select a room.");
            return;
        }

        if (selectedUser == null) {
            statusLabel.setText("Please select a user.");
            return;
        }

        if (startDate == null || endDate == null) {
            statusLabel.setText("Please select valid start and end dates.");
            return;
        }

        if (endDate.isBefore(startDate)) {
            statusLabel.setText("End date cannot be before start date.");
            return;
        }

        if (numberOfPeople > selectedRoom.getMaxPeople()) {
            statusLabel.setText("Number of people exceeds the room's capacity.");
            return;
        }

        int userId = selectedUser.getId();
        Optional<Integer> result = roomService.reserveRoom(userId, selectedRoom, startDate, endDate, numberOfPeople);

        if (result.isEmpty()) {
            statusLabel.setText("Reservation failed: The selected dates are already booked or an error occurred.");
        } else {
            int reservationId = result.get();
            System.out.println("Debug: Successfully retrieved reservation ID: " + reservationId);

            Reservation newReservation = new Reservation(
                reservationId,
                selectedRoom.getId(), // Use roomId
                selectedRoom.getRoomName(),
                selectedUser.getUsername(),
                startDate,
                endDate,
                false,
                selectedRoom.getPricePerNight() * (endDate.toEpochDay() - startDate.toEpochDay()),
                0.0
            );
            reservationList.add(0, newReservation);
            highlightNewReservation(newReservation);
            statusLabel.setText("Reservation successful for room: " + selectedRoom.getRoomName());
        }
    }

    @FXML
    public void handleMarkAsPaid() {
        Reservation selectedReservation = reservationsTable.getSelectionModel().getSelectedItem();
        if (selectedReservation == null) {
            statusLabel.setText("Please select a reservation to mark as paid.");
            return;
        }

        boolean success = paymentService.markAsPaid(selectedReservation, true);
        if (success) {
            statusLabel.setText("Reservation marked as paid.");
            loadInitialReservations();
        } else {
            statusLabel.setText("Failed to mark reservation as paid.");
        }
    }

    @FXML
    public void handleAddRoom() {
        try {
            String roomName = newRoomNameField.getText().trim();
            double pricePerNight = Double.parseDouble(newRoomPriceField.getText().trim());
            int maxPeople = Integer.parseInt(newRoomMaxPeopleField.getText().trim());

            if (roomName.isEmpty() || pricePerNight <= 0 || maxPeople <= 0) {
                statusLabel.setText("Please provide valid room details.");
                return;
            }

            Room newRoom = new Room(roomName);
            newRoom.setPricePerNight(pricePerNight);
            newRoom.setMaxPeople(maxPeople); // Set max people

            boolean success = roomService.addRoom(newRoom);
            if (success) {
                statusLabel.setText("Room added successfully.");
                loadInitialRooms();
            } else {
                statusLabel.setText("Failed to add room.");
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid input. Please enter valid numbers for price and max people.");
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("An error occurred while adding the room.");
        }
    }

    @FXML
    public void handleViewMyReservations() {
        try {
            if (Main.myReservationsStage == null) {
                // Dynamically load booking.fxml for regular user
                FXMLLoader bookingLoader = new FXMLLoader(getClass().getResource("/application/views/booking.fxml"));
                Parent bookingRoot = bookingLoader.load();
                Main.bookingController = bookingLoader.getController();
                Main.myReservationsStage = new Stage();
                Main.myReservationsStage.setScene(new Scene(bookingRoot));
                Main.myReservationsStage.setTitle("My Reservations");

                // Load rooms for the current user
                Main.bookingController.loadInitialRooms(Main.currentUserId);
            }

            // Show the "My Reservations" stage without closing the current window
            Main.myReservationsStage.show();
            Main.myReservationsStage.toFront(); // Bring the stage to the front
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleBackToLogin() {
        try {
            Main.mainStage.hide();
            Main.loginStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Failed to navigate to login.");
        }
    }

    @FXML
    public void handleFilterReservations() {
        String searchQuery = searchField.getText().trim();
        reservationList.setAll(reservationService.filterReservations(searchQuery));
        statusLabel.setText("Reservations filtered.");
    }

    public void onBookingsViewShown() {
        loadInitialRooms(); // Reload the rooms when the bookings view is shown
        loadInitialReservations(); // Reload the reservations
        statusLabel.setText("Bookings view loaded.");
    }
}