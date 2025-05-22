module HotelManagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.sql;
    requires flyway.core;

    opens application to javafx.graphics, javafx.fxml, javafx.base;
    opens application.controllers to javafx.fxml;
    opens application.models to javafx.base;
    opens application.ui to javafx.fxml;
    opens application.services to javafx.base;
}
