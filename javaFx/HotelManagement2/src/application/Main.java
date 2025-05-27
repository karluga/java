package application;

import java.io.IOException;
import java.sql.SQLException;

import application.controllers.BookingController;
import application.controllers.RoomController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static Stage loginStage;
    public static Stage mainStage;
    public static Stage myReservationsStage; // Add a stage for "My Reservations"
    public static RoomController roomController; // Global reference
    public static BookingController bookingController; // Global reference
    public static Integer currentUserId = null;
    public static String currentUsername = null;
    public static Integer currentUserRole = null; // 1 for admin, 0 for regular user

    public static void setCurrentUser(int userId, String username, int role) {
        currentUserId = userId;
        currentUsername = username;
        currentUserRole = role;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        loginStage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("/application/views/login.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Hotel Management System - Login");
        primaryStage.show();
    }

    public static void main(String[] args) throws IOException, SQLException {
        boolean runMigrations = Boolean.parseBoolean(Config.get("RUN_MIGRATIONS"));

        if (runMigrations) {
            DBConnection.runMigrations();
            DBConnection.runSeeders();

            System.out.println("Migrations applied.");
            Config.set("RUN_MIGRATIONS", "false");
        }

        launch(args);
    }
}