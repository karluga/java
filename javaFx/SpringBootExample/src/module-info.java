module testwbu {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.base;
	requires java.xml;
	requires java.sql;
	
	opens application to javafx.graphics, javafx.fxml, javafx.base;
}
