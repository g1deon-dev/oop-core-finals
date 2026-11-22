
package com.mycompany.oop.core.finals.dao;

import com.mycompany.oop.core.finals.model.Bundle;
import com.mycompany.oop.core.finals.model.BundleItem;
import com.mycompany.oop.core.finals.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BundleDAO {

    public void addBundle(Bundle bundle) throws SQLException {

        validateBundle(bundle);

        String bundleSql = """
            INSERT INTO bundles (bundle_name, price_centavos)
            VALUES (?, ?)
            """;

        String itemSql = """
            INSERT INTO bundle_items (bundle_id, product_id, quantity)
            VALUES (?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection()) {

            conn.setAutoCommit(false);

            try {
                validateProducts(conn, bundle.getItems());

                int bundleId;

                try (PreparedStatement stmt = conn.prepareStatement(
                        bundleSql, Statement.RETURN_GENERATED_KEYS)) {

                    stmt.setString(1, bundle.getBundleName().trim());
                    stmt.setLong(2, bundle.getPriceCentavos());

                    stmt.executeUpdate();

                    try (ResultSet keys = stmt.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException(
                                "Creating bundle failed. No ID generated."
                            );
                        }

                        bundleId = keys.getInt(1);
                    }
                }

                insertBundleItems(conn, itemSql,
                                  bundleId, bundle.getItems());

                conn.commit();

            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public List<Bundle> getAllBundles() throws SQLException {

        List<Bundle> bundles = new ArrayList<>();

        String sql = """
            SELECT bundle_id, bundle_name, price_centavos, is_active
            FROM bundles
            WHERE is_active = TRUE
            ORDER BY bundle_name
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                bundles.add(buildBundle(conn, rs));
            }
        }

        return bundles;
    }

    public Bundle getBundleById(int bundleId) throws SQLException {

        String sql = """
            SELECT bundle_id, bundle_name, price_centavos, is_active
            FROM bundles
            WHERE bundle_id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bundleId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return buildBundle(conn, rs);
                }
            }
        }

        return null;
    }

    public void updateBundle(Bundle bundle) throws SQLException {

        validateBundle(bundle);

        if (bundle.getBundleId() <= 0) {
            throw new IllegalArgumentException("Invalid bundle ID.");
        }

        String updateSql = """
            UPDATE bundles
            SET bundle_name = ?, price_centavos = ?
            WHERE bundle_id = ?
            """;

        String deleteItemsSql = """
            DELETE FROM bundle_items
            WHERE bundle_id = ?
            """;

        String insertItemsSql = """
            INSERT INTO bundle_items (bundle_id, product_id, quantity)
            VALUES (?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection()) {

            conn.setAutoCommit(false);

            try {
                validateProducts(conn, bundle.getItems());

                try (PreparedStatement stmt =
                        conn.prepareStatement(updateSql)) {

                    stmt.setString(1, bundle.getBundleName().trim());
                    stmt.setLong(2, bundle.getPriceCentavos());
                    stmt.setInt(3, bundle.getBundleId());

                    if (stmt.executeUpdate() == 0) {
                        throw new SQLException("Bundle not found.");
                    }
                }

                try (PreparedStatement stmt =
                        conn.prepareStatement(deleteItemsSql)) {

                    stmt.setInt(1, bundle.getBundleId());
                    stmt.executeUpdate();
                }

                insertBundleItems(conn, insertItemsSql,
                                  bundle.getBundleId(), bundle.getItems());

                conn.commit();

            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void deactivateBundle(int bundleId) throws SQLException {

        updateBundleStatus(bundleId, false);
    }

    public void reactivateBundle(int bundleId) throws SQLException {

        updateBundleStatus(bundleId, true);
    }

    private void updateBundleStatus(
            int bundleId, boolean active) throws SQLException {

        String sql = """
            UPDATE bundles
            SET is_active = ?
            WHERE bundle_id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, active);
            stmt.setInt(2, bundleId);

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Bundle not found.");
            }
        }
    }

    public boolean bundleNameExists(String bundleName)
            throws SQLException {

        String sql = """
            SELECT COUNT(*)
            FROM bundles
            WHERE LOWER(TRIM(bundle_name)) = LOWER(TRIM(?))
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, bundleName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    private Bundle buildBundle(
            Connection conn, ResultSet rs) throws SQLException {

        int bundleId = rs.getInt("bundle_id");

        String itemSql = """
            SELECT bi.bundle_item_id, bi.bundle_id,
                   bi.product_id, p.product_name, bi.quantity
            FROM bundle_items bi
            JOIN products p ON bi.product_id = p.product_id
            WHERE bi.bundle_id = ?
            ORDER BY bi.bundle_item_id
            """;

        List<BundleItem> items = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(itemSql)) {

            stmt.setInt(1, bundleId);

            try (ResultSet itemRs = stmt.executeQuery()) {
                while (itemRs.next()) {

                    items.add(new BundleItem(
                        itemRs.getInt("bundle_item_id"),
                        itemRs.getInt("bundle_id"),
                        itemRs.getInt("product_id"),
                        itemRs.getString("product_name"),
                        itemRs.getInt("quantity")
                    ));
                }
            }
        }

        return new Bundle(
            bundleId,
            rs.getString("bundle_name"),
            rs.getLong("price_centavos"),
            rs.getBoolean("is_active"),
            items
        );
    }

    private void validateBundle(Bundle bundle) {

        if (bundle == null) {
            throw new IllegalArgumentException("Bundle cannot be null.");
        }

        if (bundle.getBundleName() == null
                || bundle.getBundleName().trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Bundle name cannot be empty."
            );
        }

        if (bundle.getPriceCentavos() < 0) {
            throw new IllegalArgumentException(
                "Bundle price cannot be negative."
            );
        }

        if (bundle.getItems() == null || bundle.getItems().isEmpty()) {
            throw new IllegalArgumentException(
                "A bundle must contain at least one product."
            );
        }

        Set<Integer> productIds = new HashSet<>();

        for (BundleItem item : bundle.getItems()) {

            if (item == null || item.getProductId() <= 0) {
                throw new IllegalArgumentException(
                    "Invalid product in bundle."
                );
            }

            if (item.getQuantity() <= 0) {
                throw new IllegalArgumentException(
                    "Component quantity must be greater than zero."
                );
            }

            if (!productIds.add(item.getProductId())) {
                throw new IllegalArgumentException(
                    "A product cannot appear twice in the same bundle."
                );
            }
        }
    }

    private void validateProducts(
            Connection conn, List<BundleItem> items)
            throws SQLException {

        String sql = """
            SELECT is_active
            FROM products
            WHERE product_id = ?
            FOR UPDATE
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (BundleItem item : items) {

                stmt.setInt(1, item.getProductId());

                try (ResultSet rs = stmt.executeQuery()) {

                    if (!rs.next()) {
                        throw new SQLException(
                            "Product ID " + item.getProductId()
                            + " does not exist."
                        );
                    }

                    if (!rs.getBoolean("is_active")) {
                        throw new SQLException(
                            "Product ID " + item.getProductId()
                            + " is inactive."
                        );
                    }
                }
            }
        }
    }

    private void insertBundleItems(
            Connection conn, String sql,
            int bundleId, List<BundleItem> items)
            throws SQLException {

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (BundleItem item : items) {

                stmt.setInt(1, bundleId);
                stmt.setInt(2, item.getProductId());
                stmt.setInt(3, item.getQuantity());

                stmt.addBatch();
            }

            stmt.executeBatch();
        }
    }
}
