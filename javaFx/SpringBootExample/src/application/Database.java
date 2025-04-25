package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

	public static Connection methodConnect() throws SQLException {
		
		String databasename = "table2025";
		String username     = "root";
		String password     = "root";
		String url          = "jdbc:mysql://localhost:3306/" + databasename;
		
		return DriverManager.getConnection(url, username, password);
	}
}
