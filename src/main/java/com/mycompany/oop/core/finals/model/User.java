/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oop.core.finals.model;

/**
 *
 * @author g1deon-dev
 */

public class User {
    private int userId;
    private String username;
    private String fullName;
    private UserRole role;
    private boolean active;
    private String passwordHash;
    private String passwordSalt;

    public User(int userId, String username, String fullName,
                UserRole role, boolean active) {
        this(userId, username, fullName, role, active, null, null);
    }

    public User(int userId, String username, String fullName,
                UserRole role, boolean active,
                String passwordHash, String passwordSalt) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.active = active;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public UserRole getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }
}