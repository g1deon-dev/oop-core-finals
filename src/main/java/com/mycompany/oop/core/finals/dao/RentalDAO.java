package com.mycompany.oop.core.finals.dao;

import com.mycompany.oop.core.finals.model.Rental;
import com.mycompany.oop.core.finals.model.RentalStatus;
import com.mycompany.oop.core.finals.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data-access object for the rentals table.
 */
public class RentalDAO {

    public int createRental(Connection conn, Rental rental) throws SQLException {
        String sql = """
            INSERT INTO rentals
              (customer_name, contact_number, venue, rental_start, expected_return, actual_return, status)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, rental.getCustomerName());
            ps.setString(2, rental.getContactNumber());
            ps.setString(3, rental.getVenue());
            ps.setTimestamp(4, Timestamp.valueOf(rental.getRentalStart()));
            ps.setTimestamp(5, Timestamp.valueOf(rental.getExpectedReturn()));
            if (rental.getActualReturn() != null) {
                ps.setTimestamp(6, Timestamp.valueOf(rental.getActualReturn()));
            } else {
                ps.setNull(6, Types.TIMESTAMP);
            }
            ps.setString(7, rental.getStatus().name());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    rental.setRentalId(id);
                    return id;
                }
                throw new SQLException("Insert succeeded but no rental_id was returned.");
            }
        }
    }

    public int createRental(Rental rental) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return createRental(conn, rental);
        }
    }

    public boolean updateStatus(Connection conn, int rentalId, RentalStatus newStatus) throws SQLException {
        String sql = "UPDATE rentals SET status = ? WHERE rental_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus.name());
            ps.setInt(2, rentalId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateStatus(int rentalId, RentalStatus newStatus) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return updateStatus(conn, rentalId, newStatus);
        }
    }

    public boolean markReturned(Connection conn, int rentalId, LocalDateTime actualReturn) throws SQLException {
        String sql = "UPDATE rentals SET status = ?, actual_return = ? WHERE rental_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, RentalStatus.RETURNED.name());
            ps.setTimestamp(2, Timestamp.valueOf(actualReturn));
            ps.setInt(3, rentalId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean markReturned(int rentalId, LocalDateTime actualReturn) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return markReturned(conn, rentalId, actualReturn);
        }
    }

    public Rental getRentalById(int rentalId) throws SQLException {
        String sql = "SELECT * FROM rentals WHERE rental_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rentalId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public List<Rental> getAllRentals() throws SQLException {
        String sql = "SELECT * FROM rentals ORDER BY rental_id DESC";
        List<Rental> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<Rental> getRentalsByStatus(RentalStatus status) throws SQLException {
        String sql = "SELECT * FROM rentals WHERE status = ? ORDER BY rental_id DESC";
        List<Rental> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public List<Rental> searchRentals(String keyword) throws SQLException {
        String sql = """
            SELECT * FROM rentals
            WHERE LOWER(customer_name) LIKE LOWER(?)
               OR LOWER(COALESCE(venue, '')) LIKE LOWER(?)
               OR LOWER(COALESCE(contact_number, '')) LIKE LOWER(?)
            ORDER BY rental_id DESC
            """;
        List<Rental> list = new ArrayList<>();
        String pattern = "%" + (keyword == null ? "" : keyword.trim()) + "%";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    private Rental mapRow(ResultSet rs) throws SQLException {
        Timestamp startTs = rs.getTimestamp("rental_start");
        Timestamp expectedTs = rs.getTimestamp("expected_return");
        Timestamp actualTs = rs.getTimestamp("actual_return");

        Rental rental = new Rental(
                rs.getInt("rental_id"),
                rs.getString("customer_name"),
                rs.getString("contact_number"),
                rs.getString("venue"),
                startTs != null ? startTs.toLocalDateTime() : null,
                expectedTs != null ? expectedTs.toLocalDateTime() : null
        );
        rental.setActualReturn(actualTs != null ? actualTs.toLocalDateTime() : null);
        rental.setStatus(RentalStatus.valueOf(rs.getString("status")));
        return rental;
    }
}
