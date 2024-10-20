package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class LoginController {
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    private static final String ADMIN_PASSWORD = "admin";

    @FXML
    protected void onLoginButtonClick() throws IOException {
        if (passwordField.getText().equals(ADMIN_PASSWORD)) {
            // Load admin view
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("admin-view.fxml"));
            Scene adminScene = new Scene(fxmlLoader.load(), 800, 600);
            Stage stage = (Stage) passwordField.getScene().getWindow();
            stage.setScene(adminScene);
        } else {
            errorLabel.setText("Incorrect password.");
        }
    }
}
