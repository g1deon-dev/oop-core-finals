package com.mycompany.oop.core.finals.dao;

import com.mycompany.oop.core.finals.model.PreOrder;
import com.mycompany.oop.core.finals.model.PreOrderStatus;
import com.mycompany.oop.core.finals.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data-access object for the pre_orders table.
 *
 * All writes are transactional where the caller passes a Connection;
 * read-only helpers open their own connection for convenience.
 */
public class PreOrderDAO {

    // ── Write ────────────────────────────────────────────────────────────────

    /**
     * Inserts a new pre-order and returns the generated order_id.
     * Runs inside the caller-supplied transaction so the caller
     * can commit/rollback together with any stock deduction.
     */
    public int createOrder(Connection conn, PreOrder order) throws SQLException {
        String sql = """
            INSERT INTO pre_orders
              (customer_name, contact_number, bundle_id, product_id,
               quantity, total_centavos, status, notes, created_by)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps = conn.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, order.getCustomerName());
            ps.setString(2, order.getContactNumber());

            if (order.getBundleId() != null) {
                ps.setInt(3, order.getBundleId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            if (order.getProductId() != null) {
                ps.setInt(4, order.getProductId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            ps.setInt(5, order.getQuantity());
            ps.setLong(6, order.getTotalCentavos());
            ps.setString(7, order.getStatus().name());
            ps.setString(8, order.getNotes());
            ps.setInt(9, order.getCreatedBy());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
                throw new SQLException("Insert succeeded but no order_id was returned.");
            }
        }
    }

    /** Convenience overload – opens its own connection (auto-commit). */
    public int createOrder(PreOrder order) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return createOrder(conn, order);
        }
    }

    // ── Status transitions ───────────────────────────────────────────────────

    /**
     * Updates the status of a pre-order.
     * @return true if a row was updated, false if orderId not found.
     */
    public boolean updateStatus(Connection conn, int orderId, PreOrderStatus newStatus)
            throws SQLException {
        String sql = "UPDATE pre_orders SET status = ? WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus.name());
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        }
    }

    /** Convenience overload – opens its own connection (auto-commit). */
    public boolean updateStatus(int orderId, PreOrderStatus newStatus)
            throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return updateStatus(conn, orderId, newStatus);
        }
    }

    // ── Read ─────────────────────────────────────────────────────────────────

    public PreOrder getOrderById(int orderId) throws SQLException {
        String sql = "SELECT * FROM pre_orders WHERE order_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<PreOrder> getAllOrders() throws SQLException {
        return queryList("SELECT * FROM pre_orders ORDER BY created_at DESC");
    }

    public List<PreOrder> getOrdersByStatus(PreOrderStatus status) throws SQLException {
        String sql = "SELECT * FROM pre_orders WHERE status = ? ORDER BY created_at DESC";
        List<PreOrder> result = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
        }
        return result;
    }

    // ── Internal helpers ─────────────────────────────────────────────────────

    private List<PreOrder> queryList(String sql) throws SQLException {
        List<PreOrder> result = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) result.add(mapRow(rs));
        }
        return result;
    }

    /** Maps the current ResultSet row to a PreOrder object. */
    private PreOrder mapRow(ResultSet rs) throws SQLException {
        // Read nullable ints safely: getInt returns 0 for SQL NULL, wasNull() confirms it
        int bundleIdRaw = rs.getInt("bundle_id");
        Integer bundleId = rs.wasNull() ? null : bundleIdRaw;

        int productIdRaw = rs.getInt("product_id");
        Integer productId = rs.wasNull() ? null : productIdRaw;

        Timestamp ts = rs.getTimestamp("created_at");

        return new PreOrder(
            rs.getInt("order_id"),
            rs.getString("customer_name"),
            rs.getString("contact_number"),
            bundleId,
            productId,
            rs.getInt("quantity"),
            rs.getLong("total_centavos"),
            PreOrderStatus.valueOf(rs.getString("status")),
            rs.getString("notes"),
            rs.getInt("created_by"),
            ts != null ? ts.toLocalDateTime() : null
        );
    }
}

