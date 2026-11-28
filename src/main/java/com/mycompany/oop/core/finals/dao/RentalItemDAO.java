package com.mycompany.oop.core.finals.dao;

import com.mycompany.oop.core.finals.model.RentalItem;
import com.mycompany.oop.core.finals.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data-access object for the rental_items table.
 */
public class RentalItemDAO {

    public int addRentalItem(Connection conn, RentalItem item) throws SQLException {
        String sql = """
            INSERT INTO rental_items
              (rental_id, product_id, product_name, quantity, rental_price_centavos)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, item.getRentalId());
            if (item.getProductId() > 0) {
                ps.setInt(2, item.getProductId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setString(3, item.getProductName());
            ps.setInt(4, item.getQuantity());
            ps.setInt(5, item.getRentalPriceCentavos());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    item.setRentalItemId(id);
                    return id;
                }
                throw new SQLException("Insert succeeded but no rental_item_id was returned.");
            }
        }
    }

    public int addRentalItem(RentalItem item) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return addRentalItem(conn, item);
        }
    }

    public List<RentalItem> getItemsByRentalId(int rentalId) throws SQLException {
        String sql = "SELECT * FROM rental_items WHERE rental_id = ? ORDER BY rental_item_id";
        List<RentalItem> items = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rentalId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int productId = rs.getInt("product_id");
                    if (rs.wasNull()) {
                        productId = 0;
                    }
                    items.add(new RentalItem(
                            rs.getInt("rental_item_id"),
                            rs.getInt("rental_id"),
                            productId,
                            rs.getString("product_name"),
                            rs.getInt("quantity"),
                            rs.getInt("rental_price_centavos")
                    ));
                }
            }
        }
        return items;
    }

    public boolean deleteItemsByRentalId(Connection conn, int rentalId) throws SQLException {
        String sql = "DELETE FROM rental_items WHERE rental_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rentalId);
            return ps.executeUpdate() > 0;
        }
    }
}
