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

    public void handleLogin(ActionEvent event) {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT password, role FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, user);
            ResultSet rs = stmt.executeQuery();

            if (rs.next() && PasswordUtil.verifyPassword(pass, rs.getString("password"))) {
                int role = rs.getInt("role");
                Main.currentUserId = rs.getInt("id");
                Main.currentUsername = usernameField.getText();

                Main.loginStage.close();

                if (role == 1) { // Admin
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/views/rooms.fxml"));
                    Parent root = loader.load();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Room Management");
                    stage.show();
                } else if (role == 0) { // Regular user
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/views/booking.fxml"));
                    Parent root = loader.load();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Booking Management");
                    stage.show();
                }
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