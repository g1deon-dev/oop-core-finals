package com.mycompany.oop.core.finals.dao;

import com.mycompany.oop.core.finals.model.StockMovement;
import com.mycompany.oop.core.finals.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StockMovementDAO {

    public void recordMovement(Connection conn, StockMovement movement) throws SQLException {
        String sql = """
            INSERT INTO stock_movements (product_id, change_amount, reason, reference_id, created_by)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, movement.getProductId());
            stmt.setInt(2, movement.getChangeAmount());
            stmt.setString(3, movement.getReason());

            if (movement.getReferenceId() != null) {
                stmt.setLong(4, movement.getReferenceId());
            } else {
                stmt.setNull(4, Types.BIGINT);
            }

            stmt.setInt(5, movement.getCreatedBy());

            stmt.executeUpdate();
        }
    }

    public void recordMovement(StockMovement movement) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            recordMovement(conn, movement);
        }
    }

    public List<StockMovement> getMovementsByProductId(int productId) throws SQLException {
        List<StockMovement> movements = new ArrayList<>();

        String sql = """
            SELECT movement_id, product_id, change_amount, reason, reference_id, created_by, created_at
            FROM stock_movements
            WHERE product_id = ?
            ORDER BY created_at DESC, movement_id DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    movements.add(mapResultSetToMovement(rs));
                }
            }
        }

        return movements;
    }

    public List<StockMovement> getAllMovements() throws SQLException {
        List<StockMovement> movements = new ArrayList<>();

        String sql = """
            SELECT movement_id, product_id, change_amount, reason, reference_id, created_by, created_at
            FROM stock_movements
            ORDER BY created_at DESC, movement_id DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                movements.add(mapResultSetToMovement(rs));
            }
        }

        return movements;
    }

    private StockMovement mapResultSetToMovement(ResultSet rs) throws SQLException {
        int movementId = rs.getInt("movement_id");
        int productId = rs.getInt("product_id");
        int changeAmount = rs.getInt("change_amount");
        String reason = rs.getString("reason");

        Long referenceId = rs.getLong("reference_id");
        if (rs.wasNull()) {
            referenceId = null;
        }

        int createdBy = rs.getInt("created_by");

        Timestamp ts = rs.getTimestamp("created_at");
        LocalDateTime createdAt = ts != null ? ts.toLocalDateTime() : null;

        return new StockMovement(movementId, productId, changeAmount, reason, referenceId, createdBy, createdAt);
    }
}
