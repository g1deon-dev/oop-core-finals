package com.mycompany.oop.core.finals.dao;

import com.mycompany.oop.core.finals.model.RentalPayment;
import com.mycompany.oop.core.finals.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data-access object for the rental_payments table.
 */
public class RentalPaymentDAO {

    public int createPayment(Connection conn, RentalPayment payment) throws SQLException {
        String sql = """
            INSERT INTO rental_payments
              (rental_id, amount_centavos, payment_method, payment_date, payment_status)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, payment.getRentalId());
            ps.setInt(2, payment.getAmountCentavos());
            ps.setString(3, payment.getPaymentMethod());
            if (payment.getPaymentDate() != null) {
                ps.setTimestamp(4, Timestamp.valueOf(payment.getPaymentDate()));
            } else {
                ps.setNull(4, Types.TIMESTAMP);
            }
            ps.setString(5, payment.getPaymentStatus());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    payment.setPaymentId(id);
                    return id;
                }
                throw new SQLException("Insert succeeded but no payment_id was returned.");
            }
        }
    }

    public int createPayment(RentalPayment payment) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return createPayment(conn, payment);
        }
    }

    public boolean markPaymentAsPaid(int paymentId) throws SQLException {
        String sql = "UPDATE rental_payments SET payment_status = 'PAID', payment_date = ? WHERE payment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, paymentId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<RentalPayment> getPaymentsByRentalId(int rentalId) throws SQLException {
        String sql = "SELECT * FROM rental_payments WHERE rental_id = ? ORDER BY payment_id DESC";
        List<RentalPayment> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rentalId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public RentalPayment getPaymentById(int paymentId) throws SQLException {
        String sql = "SELECT * FROM rental_payments WHERE payment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, paymentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    private RentalPayment mapRow(ResultSet rs) throws SQLException {
        Timestamp payTs = rs.getTimestamp("payment_date");
        return new RentalPayment(
                rs.getInt("payment_id"),
                rs.getInt("rental_id"),
                rs.getInt("amount_centavos"),
                rs.getString("payment_method"),
                payTs != null ? payTs.toLocalDateTime() : null,
                rs.getString("payment_status")
        );
    }
}
