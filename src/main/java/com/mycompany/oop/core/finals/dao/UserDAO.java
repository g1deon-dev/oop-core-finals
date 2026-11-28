package com.mycompany.oop.core.finals.dao;

import com.mycompany.oop.core.finals.model.User;
import com.mycompany.oop.core.finals.model.UserRole;
import com.mycompany.oop.core.finals.util.CsvStorage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UserDAO {

    private static final String FILE = "users.csv";
    private static final String[] HEADERS = {
        "user_id", "username", "full_name", "password_hash", "password_salt", "role", "is_active"
    };

    public void createUser(
            String username,
            String fullName,
            String passwordHash,
            String passwordSalt,
            UserRole role) throws SQLException {
        List<String[]> rows = CsvStorage.readAllRows(FILE, HEADERS);
        int id = CsvStorage.nextIntId(rows, 0);
        rows.add(new String[]{
            String.valueOf(id),
            username,
            fullName,
            passwordHash,
            passwordSalt,
            role.name(),
            "true"
        });
        CsvStorage.writeAllRows(FILE, HEADERS, rows);
    }

    public User findByUsername(String username) throws SQLException {
        List<String[]> rows = CsvStorage.readAllRows(FILE, HEADERS);
        for (String[] r : rows) {
            if (r[1].equals(username)) {
                return new User(
                    Integer.parseInt(r[0]),
                    r[1],
                    r[2],
                    UserRole.valueOf(r[5]),
                    Boolean.parseBoolean(r[6]),
                    r[3],
                    r[4]
                );
            }
        }
        return null;
    }

    public List<User> getAllUsers() throws SQLException {
        List<String[]> rows = CsvStorage.readAllRows(FILE, HEADERS);
        List<User> users = new ArrayList<>();
        for (String[] r : rows) {
            users.add(new User(
                Integer.parseInt(r[0]),
                r[1],
                r[2],
                UserRole.valueOf(r[5]),
                Boolean.parseBoolean(r[6])
            ));
        }
        users.sort(Comparator.comparingInt(User::getUserId));
        return users;
    }

    public void setUserActive(int userId, boolean active) throws SQLException {
        List<String[]> rows = CsvStorage.readAllRows(FILE, HEADERS);
        boolean found = false;
        for (String[] r : rows) {
            if (Integer.parseInt(r[0]) == userId) {
                r[6] = String.valueOf(active);
                found = true;
                break;
            }
        }
        if (!found) {
            throw new SQLException("User not found.");
        }
        CsvStorage.writeAllRows(FILE, HEADERS, rows);
    }
}
