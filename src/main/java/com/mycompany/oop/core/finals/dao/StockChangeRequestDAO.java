package com.mycompany.oop.core.finals.dao;

import com.mycompany.oop.core.finals.model.StockChangeRequest;
import com.mycompany.oop.core.finals.model.StockChangeStatus;
import com.mycompany.oop.core.finals.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StockChangeRequestDAO {

    public int createRequest(StockChangeRequest request) throws SQLException {
        String sql = """
            INSERT INTO stock_change_requests (product_id, requested_quantity, reason, status, requested_by)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, request.getProductId());
            stmt.setInt(2, request.getRequestedQuantity());
            stmt.setString(3, request.getReason());
            stmt.setString(4, request.getStatus().name());
            stmt.setInt(5, request.getRequestedBy());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating stock change request failed, no ID obtained.");
                }
            }
        }
    }

    public StockChangeRequest getRequestById(int requestId) throws SQLException {
        String sql = """
            SELECT request_id, product_id, requested_quantity, reason, status,
                   requested_by, reviewed_by, review_notes, created_at, reviewed_at
            FROM stock_change_requests
            WHERE request_id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, requestId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRequest(rs);
                }
            }
        }

        return null;
    }

    public List<StockChangeRequest> getAllRequests() throws SQLException {
        List<StockChangeRequest> requests = new ArrayList<>();

        String sql = """
            SELECT request_id, product_id, requested_quantity, reason, status,
                   requested_by, reviewed_by, review_notes, created_at, reviewed_at
            FROM stock_change_requests
            ORDER BY created_at DESC, request_id DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                requests.add(mapResultSetToRequest(rs));
            }
        }

        return requests;
    }

    public List<StockChangeRequest> getRequestsByStatus(StockChangeStatus status) throws SQLException {
        List<StockChangeRequest> requests = new ArrayList<>();

        String sql = """
            SELECT request_id, product_id, requested_quantity, reason, status,
                   requested_by, reviewed_by, review_notes, created_at, reviewed_at
            FROM stock_change_requests
            WHERE status = ?
            ORDER BY created_at DESC, request_id DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToRequest(rs));
                }
            }
        }

        return requests;
    }

    public void updateStatus(int requestId, StockChangeStatus newStatus, int reviewedBy, String reviewNotes) throws SQLException {
        String sql = """
            UPDATE stock_change_requests
            SET status = ?,
                reviewed_by = ?,
                review_notes = ?,
                reviewed_at = CURRENT_TIMESTAMP
            WHERE request_id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus.name());
            stmt.setInt(2, reviewedBy);
            stmt.setString(3, reviewNotes);
            stmt.setInt(4, requestId);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Stock change request not found: " + requestId);
            }
        }
    }

    public void cancelRequest(int requestId, int userId) throws SQLException {
        String sql = """
            UPDATE stock_change_requests
            SET status = 'CANCELLED'
            WHERE request_id = ? AND requested_by = ? AND status = 'PENDING'
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, requestId);
            stmt.setInt(2, userId);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Request could not be cancelled. It may not exist, belong to another user, or is no longer PENDING.");
            }
        }
    }

    private StockChangeRequest mapResultSetToRequest(ResultSet rs) throws SQLException {
        int requestId = rs.getInt("request_id");
        int productId = rs.getInt("product_id");
        int requestedQuantity = rs.getInt("requested_quantity");
        String reason = rs.getString("reason");
        StockChangeStatus status = StockChangeStatus.valueOf(rs.getString("status"));
        int requestedBy = rs.getInt("requested_by");

        Integer reviewedBy = rs.getInt("reviewed_by");
        if (rs.wasNull()) {
            reviewedBy = null;
        }

        String reviewNotes = rs.getString("review_notes");

        Timestamp createdTs = rs.getTimestamp("created_at");
        LocalDateTime createdAt = createdTs != null ? createdTs.toLocalDateTime() : null;

        Timestamp reviewedTs = rs.getTimestamp("reviewed_at");
        LocalDateTime reviewedAt = reviewedTs != null ? reviewedTs.toLocalDateTime() : null;

        return new StockChangeRequest(
            requestId, productId, requestedQuantity, reason, status,
            requestedBy, reviewedBy, reviewNotes, createdAt, reviewedAt
        );
    }
}
