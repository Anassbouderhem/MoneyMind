package com.MoneyMind.projet_javafx.db;

import com.MoneyMind.projet_javafx.controllers.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public static User getUserByCredentials(String username, String password) {
        String sql = "SELECT user_id, username, password, total_limit FROM users WHERE username = ? AND password = ?";

        try (Connection conn = SQliteConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("user_id");
                    String uname = rs.getString("username");
                    String pwd = rs.getString("password");
                    double totalLimit = rs.getDouble("total_limit");

                    // Créer et retourner l'objet User avec total_limit
                    return new User(id, uname, pwd, totalLimit);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'utilisateur : " + e.getMessage());
        }

        return null;
    }
}
