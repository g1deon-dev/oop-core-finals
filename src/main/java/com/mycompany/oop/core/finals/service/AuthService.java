/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oop.core.finals.service;

/**
 *
 * @author g1deon-dev
 */
import com.mycompany.oop.core.finals.dao.UserDAO;
import com.mycompany.oop.core.finals.model.User;
import com.mycompany.oop.core.finals.model.UserRole;
import com.mycompany.oop.core.finals.security.UserSession;
import com.mycompany.oop.core.finals.util.PasswordUtil;

import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.List;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();
    private final UserSession session;

    public AuthService(UserSession session) {
        if (session == null) {
            throw new IllegalArgumentException(
                "User session cannot be null."
            );
        }

        this.session = session;
    }

    // Authenticate an existing user
    public User login(String username, String password)
            throws SQLException, GeneralSecurityException {

        if (username == null || username.isBlank()
                || password == null || password.isEmpty()) {
            throw new IllegalArgumentException(
                "Username and password are required."
            );
        }

        User user = userDAO.findByUsername(username.trim());

        if (user == null || !user.isActive()) {
            throw new IllegalArgumentException(
                "Invalid username or password."
            );
        }

        boolean valid = PasswordUtil.verifyPassword(
            password,
            user.getPasswordSalt(),
            user.getPasswordHash()
        );

        if (!valid) {
            throw new IllegalArgumentException(
                "Invalid username or password."
            );
        }

        session.startSession(user);

        return user;
    }

    // Log out the current user
    public void logout() {
        session.endSession();
    }

    // Create a user account
    // Only the Chairperson can create accounts through this method.
    public void createUser(String username, String fullName,
                           String password, UserRole role)
            throws SQLException, GeneralSecurityException {

        User currentUser = session.getCurrentUser();

        if (currentUser == null
                || currentUser.getRole() != UserRole.CHAIRPERSON) {
            throw new SecurityException(
                "Only the Chairperson can create user accounts."
            );
        }

        createAccount(username, fullName, password, role);
    }

    // One-time setup for the first Chairperson account
    public void createInitialChairperson(
            String username, String fullName, String password)
            throws SQLException, GeneralSecurityException {

        if (!userDAO.getAllUsers().isEmpty()) {
            throw new SecurityException(
                "Initial account setup has already been completed."
            );
        }

        createAccount(
            username,
            fullName,
            password,
            UserRole.CHAIRPERSON
        );
    }

    private void createAccount(String username, String fullName,
                               String password, UserRole role)
            throws SQLException, GeneralSecurityException {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                "Username is required."
            );
        }

        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException(
                "Full name is required."
            );
        }

        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException(
                "Password must contain at least 8 characters."
            );
        }

        if (role == null) {
            throw new IllegalArgumentException(
                "User role is required."
            );
        }

        String cleanUsername = username.trim();
        String cleanFullName = fullName.trim();

        if (userDAO.findByUsername(cleanUsername) != null) {
            throw new IllegalArgumentException(
                "Username is already taken."
            );
        }

        String salt = PasswordUtil.generateSalt();

        String hash = PasswordUtil.hashPassword(password, salt);

        userDAO.createUser(
            cleanUsername,
            cleanFullName,
            hash,
            salt,
            role
        );
    }

    public List<User> getAllUsers() throws SQLException {
        User currentUser = session.getCurrentUser();

        if (currentUser == null
                || currentUser.getRole() != UserRole.CHAIRPERSON) {
            throw new SecurityException(
                "Access denied."
            );
        }

        return userDAO.getAllUsers();
    }

    public void setUserActive(int userId, boolean active)
            throws SQLException {

        User currentUser = session.getCurrentUser();

        if (currentUser == null
                || currentUser.getRole() != UserRole.CHAIRPERSON) {
            throw new SecurityException(
                "Access denied."
            );
        }

        if (currentUser.getUserId() == userId && !active) {
            throw new IllegalArgumentException(
                "You cannot deactivate your own account."
            );
        }

        userDAO.setUserActive(userId, active);
    }
}
