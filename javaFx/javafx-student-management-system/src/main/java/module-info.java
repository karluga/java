module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    requires com.google.gson;

    opens com.example.demo to javafx.fxml, com.google.gson;
    exports com.example.demo;
}
