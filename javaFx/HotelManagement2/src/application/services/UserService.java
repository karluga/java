package application.services;

import application.DBConnection;
import application.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    public List<User> fetchUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT id, username FROM users ORDER BY username";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // Create User objects and add them to the list
                User user = new User(rs.getInt("id"), rs.getString("username"));
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }
}
