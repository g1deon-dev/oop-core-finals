/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oop.core.finals.dao;

/**
 *
 * @author g1deon-dev
 */
import com.mycompany.oop.core.finals.model.User;
import com.mycompany.oop.core.finals.model.UserRole;
import com.mycompany.oop.core.finals.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // Create a new user account
    public void createUser(
            String username,
            String fullName,
            String passwordHash,
            String passwordSalt,
            UserRole role) throws SQLException {

        String sql = """
            INSERT INTO users
            (username, full_name, password_hash, password_salt, role)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, fullName);
            stmt.setString(3, passwordHash);
            stmt.setString(4, passwordSalt);
            stmt.setString(5, role.name());

            stmt.executeUpdate();
        }
    }

    // Find a user using their username
    public User findByUsername(String username) throws SQLException {

        String sql = """
            SELECT * FROM users
            WHERE username = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("full_name"),
                        UserRole.valueOf(rs.getString("role")),
                        rs.getBoolean("is_active"),
                        rs.getString("password_hash"),
                        rs.getString("password_salt")
                    );
                }
            }
        }

        return null;
    }

    // Retrieve all registered users
    public List<User> getAllUsers() throws SQLException {

        List<User> users = new ArrayList<>();

        String sql = """
            SELECT user_id, username, full_name, role, is_active
            FROM users
            ORDER BY user_id
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("full_name"),
                    UserRole.valueOf(rs.getString("role")),
                    rs.getBoolean("is_active")
                ));
            }
        }

        return users;
    }

    // Activate or deactivate an account
    public void setUserActive(int userId, boolean active)
            throws SQLException {

        String sql = """
            UPDATE users
            SET is_active = ?
            WHERE user_id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, active);
            stmt.setInt(2, userId);

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated == 0) {
                throw new SQLException("User not found.");
            }
        }
    }
}