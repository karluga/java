package application.ui;

import application.controllers.BookingController;
import application.models.Room;

import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.time.LocalDate;

public class RoomCardFactory {
    private final BookingController controller;

    public RoomCardFactory(BookingController controller) {
        this.controller = controller;
    }

    public void createRoomCard(Room room, FlowPane roomFlowPane) {
        VBox card = new VBox(5);
        card.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: #f0f0f0;");
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
            controller.handleReservation(room, startDate, endDate, room.getMaxPeople());
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(event -> controller.handleCancellation(room));

        card.getChildren().addAll(nameText, priceText, maxPeopleText, startDatePicker, endDatePicker, reserveButton, cancelButton);
        roomFlowPane.getChildren().add(card);
    }
}
