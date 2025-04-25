package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class K {

    @FXML
    private Button add;

    @FXML
    private TableColumn<Employee, Integer> clmage;

    @FXML
    private TableColumn<Employee, String> clmname;

    @FXML
    private TableColumn<Employee, String> clmposition;

    @FXML
    private Button delete;

    @FXML
    private Label lblid;

    @FXML
    private TableView<Employee> tblEmployee;

    @FXML
    private TextField txtage;

    @FXML
    private TextField txtname;

    @FXML
    private TextField txtposition;

    @FXML
    private Button update;
    
    ObservableList<Employee> listofemployees = FXCollections.observableArrayList();
    
    // CRUD (Read - Create)
    
    public void initialize() throws SQLException {
    	
    	clmname.setCellValueFactory(new PropertyValueFactory<Employee, String>("name"));
    	clmage.setCellValueFactory(new PropertyValueFactory<Employee, Integer>("age"));
    	clmposition.setCellValueFactory(new PropertyValueFactory<Employee, String>("position"));
    	
    	populate();
    	
    	add.setOnAction(event->{
    		try {
				Connection conn = Database.methodConnect();
				PreparedStatement stprep = conn.prepareStatement("insert into employee (name, age, position) values (?, ?, ?)");
				stprep.setString(1, txtname.getText());
				stprep.setString(2, txtage.getText());
				stprep.setString(3, txtposition.getText());
				stprep.executeUpdate();
				populate();
				clear();
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	});
    	
    	delete.setOnAction(event->{
    		try {
				Connection conn = Database.methodConnect();
				PreparedStatement stprep = conn.prepareStatement("delete from employee where id = ?");
				stprep.setString(1, lblid.getText());
				stprep.executeUpdate();
				populate();
				clear();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	});
    	
    	update.setOnAction(event->{
    		Connection conn;
			try {
				conn = Database.methodConnect();
	    		PreparedStatement stprep = conn.prepareStatement("update employee set name = ?, age = ?, position = ? where id = ?");
	    		stprep.setString(1, txtname.getText());
	    		stprep.setString(2, txtage.getText());
	    		stprep.setString(3, txtposition.getText());
	    		stprep.setString(4, lblid.getText());
	    		stprep.executeUpdate();
	    		populate();
	    		clear();
	    		
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	});
    	
    	tblEmployee.setOnMouseClicked(event->{
    		Employee emp = tblEmployee.getSelectionModel().getSelectedItem();
    		
    		txtname.setText(emp.getName());
    		txtage.setText(String.valueOf(emp.getAge()));
    		txtposition.setText(emp.getPosition());
    		lblid.setText(String.valueOf(emp.getId()));
    	});
    	
    }
    
    public void populate() throws SQLException {
    	
    	listofemployees.clear();
    	
    	Connection conn = Database.methodConnect();
    	
    	Statement st = conn.createStatement();
    	
    	ResultSet rs = st.executeQuery("select * from employee");
    	
    	while(rs.next()){
    		Employee emp = new Employee(rs.getInt("id"), rs.getString("name"), rs.getInt("age"), rs.getString("position"));
    		listofemployees.add(emp);
    	}
    	tblEmployee.setItems(listofemployees);
    }
    
    public void clear() {
    	txtname.setText("");
    	txtage.setText("");
    	txtposition.setText("");
    }
}
