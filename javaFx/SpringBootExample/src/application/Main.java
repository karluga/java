package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Main extends Application {
	
	public static Stage stage1;
	public static Stage stage2;
	
    @Override
    public void start(Stage primaryStage) {
        try {
        	FXMLLoader loader1 = new FXMLLoader(getClass().getResource("window3.fxml"));
        	Parent root1 = loader1.load();
        	Scene scene1 = new Scene(root1);
        	stage1 = primaryStage;
        	stage1.setScene(scene1);
        	stage1.show();
        	
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
	
	public static void main(String[] args) {
		launch(args);
	}
}
