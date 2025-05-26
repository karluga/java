package application.services;

import application.DBConnection;
import application.models.Reservation;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PaymentService {
    public boolean markAsPaid(Reservation reservation, boolean fullyPaid) {
        String query = """
            UPDATE bookings
            SET is_paid = ?
            WHERE room_id = (SELECT id FROM rooms WHERE name = ?) AND customer_name = ? AND start_date = ? AND end_date = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBoolean(1, fullyPaid);
            stmt.setString(2, reservation.getRoomName());
            stmt.setString(3, reservation.getCustomerName());
            stmt.setObject(4, reservation.getStartDate());
            stmt.setObject(5, reservation.getEndDate());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void handlePartialPayment(Reservation reservation, TableView<Reservation> reservationsTable, Label statusLabel) {
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
                        VALUES (?, ?)
                    """;
                    String updateQuery = """
                        UPDATE bookings
                        SET is_paid = CASE WHEN ? >= total_price THEN 1 ELSE 0 END
                        WHERE id = ?
                    """;

                    try (Connection conn = DBConnection.getConnection();
                         PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                         PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {

                        // Use reservation_id directly from the Reservation object
                        int reservationId = reservation.getId();

                        // Insert payment record
                        insertStmt.setInt(1, reservationId);
                        insertStmt.setDouble(2, payment);
                        insertStmt.executeUpdate();

                        // Update is_paid column
                        updateStmt.setDouble(1, reservation.getPaidAmount() + payment);
                        updateStmt.setInt(2, reservationId);
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
}
