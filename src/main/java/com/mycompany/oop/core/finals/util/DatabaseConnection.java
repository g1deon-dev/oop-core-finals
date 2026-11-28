package com.mycompany.oop.core.finals.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static String url;
    private static String username;
    private static String password;

    static {
        loadConfiguration();
    }

    private static void loadConfiguration() {
        Properties props = new Properties();

        File propFile = new File("db.properties");
        if (propFile.exists()) {
            try (InputStream in = new FileInputStream(propFile)) {
                props.load(in);
            } catch (Exception ignored) {
            }
        }

        url = props.getProperty("db.url", System.getenv("OOP_DB_URL"));
        username = props.getProperty("db.user", System.getenv("OOP_DB_USER"));
        password = props.getProperty("db.password", System.getenv("OOP_DB_PASSWORD"));
    }

    public static Connection getConnection() throws SQLException {
        if (url == null || username == null || password == null) {
            loadConfiguration();
        }

        if (url == null || username == null || password == null) {
            throw new IllegalStateException(
                "Database credentials are not configured. Please create a 'db.properties' file or set OOP_DB_URL, OOP_DB_USER, OOP_DB_PASSWORD."
            );
        }

        return DriverManager.getConnection(url, username, password);
    }
}
