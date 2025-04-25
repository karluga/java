module test2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    opens test2 to javafx.fxml;
    exports test2;
}