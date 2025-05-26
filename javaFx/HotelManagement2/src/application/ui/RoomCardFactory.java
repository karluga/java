package application.ui;

import application.Main;
import application.controllers.BookingController;
import application.models.Room;
import application.models.Reservation;
import application.services.RoomService;

import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.util.List;

public class RoomCardFactory {
    private final BookingController controller;
    private final RoomService roomService;

    public RoomCardFactory(BookingController controller) {
        this.controller = controller;
        this.roomService = new RoomService();
    }

    public void createRoomCard(Room room, FlowPane roomFlowPane, boolean isReserved, Reservation reservation) {
        System.out.println("Creating room card for: " + room.getRoomName() + ", Reserved: " + isReserved);
        VBox card = new VBox(5);
        card.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: " + (isReserved ? "lightgreen" : "#f0f0f0") + ";");
        card.setPrefSize(150, 200);

        Text nameText = new Text("Room: " + room.getRoomName());
        Text priceText = new Text("Price: $" + String.format("%.2f", room.getPricePerNight()) + "/night");
        Text maxPeopleText = new Text("Max People: " + room.getMaxPeople());

        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Start Date");
        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("End Date");

        Button reserveButton = new Button("Reserve");
        reserveButton.setOnAction(event -> {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            if (startDate == null || endDate == null || startDate.isBefore(LocalDate.now())) {
                controller.updateStatusLabel("Invalid reservation dates.");
                return;
            }

            int numberOfPeople = room.getMaxPeople();
            var result = roomService.reserveRoom(Main.currentUserId, room, startDate, endDate, numberOfPeople);

            if (result.isEmpty()) {
                controller.updateStatusLabel("Reservation failed: The selected dates are already booked or an error occurred.");
            } else {
                int reservationId = result.get();
                controller.updateStatusLabel("Reservation successful for " + room.getRoomName() + ".");
                card.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: lightgreen;");

                // Add a cancel button for the newly reserved room
                Button cancelButton = new Button("Cancel");
                cancelButton.setOnAction(cancelEvent -> controller.handleCancellation(room, reservationId));
                card.getChildren().add(cancelButton);
            }
        });

        card.getChildren().addAll(nameText, priceText, maxPeopleText, startDatePicker, endDatePicker, reserveButton);

        // If the room is already reserved, add a cancel button if the reservation meets the criteria
        if (isReserved && reservation != null && reservation.getStartDate().isAfter(LocalDate.now())) {
            Button cancelButton = new Button("Cancel");
            cancelButton.setOnAction(event -> controller.handleCancellation(room, reservation.getId()));
            card.getChildren().add(cancelButton);
        }

        roomFlowPane.getChildren().add(card);
    }

    public void loadReservedRooms(List<Reservation> reservations, FlowPane roomFlowPane) {
        for (Reservation reservation : reservations) {
            Room room = new Room(reservation.getRoomName());
            room.setPricePerNight(reservation.getTotalPrice() / reservation.getNumberOfNights());
            room.setMaxPeople(1); // Assuming 1 person for simplicity
            createRoomCard(room, roomFlowPane, true, reservation);
        }
    }

    public void loadAvailableRooms(List<Room> rooms, FlowPane roomFlowPane) {
        for (Room room : rooms) {
            createRoomCard(room, roomFlowPane, false, null);
        }
    }
}
