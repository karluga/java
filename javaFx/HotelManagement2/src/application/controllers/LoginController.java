package application.controllers;

import application.DBConnection;
import application.Main;
import application.utils.PasswordUtil;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;
    @FXML private TextField registerUsernameField;
    @FXML private PasswordField registerPasswordField;
    @FXML private Label registerStatusLabel;

    private void handleLoginSuccess(int userId, String username, int role) {
        Main.setCurrentUser(userId, username, role);
        Main.currentUserId = userId; // Set the current user ID in Main
        System.out.println("Logged in as user ID: " + Main.currentUserId); // Log the user ID to the console

        try {
            if (role == 1) { // Assuming role 1 is admin
                // Dynamically load rooms.fxml for admin
                FXMLLoader roomLoader = new FXMLLoader(getClass().getResource("/application/views/rooms.fxml"));
                Parent roomRoot = roomLoader.load();
                Main.roomController = roomLoader.getController();
                Main.mainStage = new Stage();
                Main.mainStage.setScene(new Scene(roomRoot));
                Main.mainStage.setTitle("Room Management");

                Main.loginStage.hide();
                Main.mainStage.show();
                Main.roomController.onBookingsViewShown(); // Load rooms for admin
            } else {
                // Dynamically load booking.fxml for regular user
                FXMLLoader bookingLoader = new FXMLLoader(getClass().getResource("/application/views/booking.fxml"));
                Parent bookingRoot = bookingLoader.load();
                Main.bookingController = bookingLoader.getController();
                Main.myReservationsStage = new Stage();
                Main.myReservationsStage.setScene(new Scene(bookingRoot));
                Main.myReservationsStage.setTitle("My Reservations");

                Main.bookingController.loadInitialRooms(Main.currentUserId); // Load rooms for the user
                Main.loginStage.hide();
                Main.myReservationsStage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleLogin(ActionEvent event) {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT id, password, role FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, user);
            ResultSet rs = stmt.executeQuery();

            if (rs.next() && PasswordUtil.verifyPassword(pass, rs.getString("password"))) {
                int role = rs.getInt("role");
                int userId = rs.getInt("id");
                handleLoginSuccess(userId, user, role);
            } else {
                statusLabel.setText("Invalid credentials.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error connecting to database.");
        }
    }

    public void handleRegister(ActionEvent event) {
        String user = registerUsernameField.getText();
        String pass = PasswordUtil.hashPassword(registerPasswordField.getText());

        try (Connection conn = DBConnection.getConnection()) {
            // Check if the username already exists
            String checkQuery = "SELECT COUNT(*) FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, user);
            ResultSet checkRs = checkStmt.executeQuery();
            if (checkRs.next() && checkRs.getInt(1) > 0) {
                registerStatusLabel.setText("Username already exists.");
                return;
            }

            // Insert the new user
            String query = "INSERT INTO users (username, password, role) VALUES (?, ?, 0)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, user);
            stmt.setString(2, pass);
            stmt.executeUpdate();
            registerStatusLabel.setText("Registration successful.");
        } catch (Exception e) {
            e.printStackTrace();
            registerStatusLabel.setText("Error during registration.");
        }
    }

    @FXML
    private void handleNewBooking() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/views/booking.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("New Booking");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}