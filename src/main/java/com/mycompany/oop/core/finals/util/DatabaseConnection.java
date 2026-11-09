/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oop.core.finals.util;

/**
 *
 * @author g1deon-dev
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://127.0.0.1:3306/oop_core_finals";

    public static Connection getConnection()
            throws SQLException {

        String username = System.getenv("OOP_DB_USER");
        String password = System.getenv("OOP_DB_PASSWORD");

        if (username == null || password == null) {
            throw new IllegalStateException(
                "Database credentials are not configured."
            );
        }

        return DriverManager.getConnection(
                URL, username, password
        );
    }
}

