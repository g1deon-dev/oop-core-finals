
package com.mycompany.oop.core.finals.dao;

import com.mycompany.oop.core.finals.model.Sale;
import com.mycompany.oop.core.finals.model.SaleItem;
import com.mycompany.oop.core.finals.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class SaleDAO {

    private record BundleData(
            String name,
            long price,
            List<BundleComponent> components) {
    }

    private record BundleComponent(
            int productId,
            int quantityPerBundle) {
    }

    private record ProductData(
            String name,
            int quantity,
            boolean active,
            long price) {
    }

    private record VerifiedComponent(
            int productId,
            String productName,
            int quantityPerBundle,
            int totalQuantityDeducted) {
    }

    private record VerifiedLine(
            int productId,
            int bundleId,
            String name,
            long price,
            int quantity,
            long subtotal,
            List<VerifiedComponent> components) {
    }

    // Creates a sale containing products, bundles, or both.
    // All stock deductions and sale records share one transaction.
    public long createSale(Sale sale) throws SQLException {

        if (sale == null || sale.getItems().isEmpty()) {
            throw new SQLException(
                "A sale must contain at least one item."
            );
        }

        if (sale.getProcessedBy() <= 0) {
            throw new SQLException("Invalid staff account.");
        }

        String bundleSql = """
            SELECT bundle_name, price_centavos, is_active
            FROM bundles
            WHERE bundle_id = ?
            FOR UPDATE
            """;

        String bundleItemsSql = """
            SELECT product_id, quantity
            FROM bundle_items
            WHERE bundle_id = ?
            ORDER BY product_id
            """;

        String productSql = """
            SELECT product_name, price_centavos,
                   quantity, is_active
            FROM products
            WHERE product_id = ?
            FOR UPDATE
            """;

        String saleSql = """
            INSERT INTO sales (processed_by, total_centavos)
            VALUES (?, ?)
            """;

        String saleItemSql = """
            INSERT INTO sale_items
            (sale_id, product_id, bundle_id, product_name,
             unit_price_centavos, quantity, subtotal_centavos)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        String componentSql = """
            INSERT INTO sale_bundle_components
            (sale_item_id, product_id, product_name,
             quantity_per_bundle, total_quantity_deducted)
            VALUES (?, ?, ?, ?, ?)
            """;

        String stockSql = """
            UPDATE products
            SET quantity = quantity - ?
            WHERE product_id = ?
            AND quantity >= ?
            """;

        try (Connection conn = DatabaseConnection.getConnection()) {

            conn.setAutoCommit(false);

            try {
                List<SaleItem> requestedItems = sale.getItems();

                TreeSet<Integer> bundleIds = new TreeSet<>();
                TreeSet<Integer> productIds = new TreeSet<>();

                // Validate sale lines and collect IDs.
                for (SaleItem item : requestedItems) {

                    if (item == null || item.getQuantity() <= 0) {
                        throw new SQLException("Invalid sale item.");
                    }

                    boolean validProduct =
                        item.getProductId() > 0
                        && item.getBundleId() == 0;

                    boolean validBundle =
                        item.getBundleId() > 0
                        && item.getProductId() == 0;

                    if (!validProduct && !validBundle) {
                        throw new SQLException(
                            "A sale item must represent exactly "
                            + "one product or bundle."
                        );
                    }

                    if (validProduct) {
                        productIds.add(item.getProductId());
                    } else {
                        bundleIds.add(item.getBundleId());
                    }
                }

                // Lock and retrieve bundles in ascending ID order.
                Map<Integer, BundleData> bundles = new HashMap<>();

                try (PreparedStatement stmt =
                         conn.prepareStatement(bundleSql)) {

                    for (int bundleId : bundleIds) {

                        stmt.setInt(1, bundleId);

                        try (ResultSet rs = stmt.executeQuery()) {

                            if (!rs.next()) {
                                throw new SQLException(
                                    "Bundle not found: " + bundleId
                                );
                            }

                            String name = rs.getString("bundle_name");
                            long price = rs.getLong("price_centavos");
                            boolean active = rs.getBoolean("is_active");

                            if (!active) {
                                throw new SQLException(
                                    "Bundle " + name + " is inactive."
                                );
                            }

                            bundles.put(
                                bundleId,
                                new BundleData(
                                    name, price, new ArrayList<>()
                                )
                            );
                        }
                    }
                }

                // Retrieve the component products of each bundle.
                try (PreparedStatement stmt =
                         conn.prepareStatement(bundleItemsSql)) {

                    for (int bundleId : bundleIds) {

                        stmt.setInt(1, bundleId);

                        List<BundleComponent> components =
                            new ArrayList<>();

                        try (ResultSet rs = stmt.executeQuery()) {

                            while (rs.next()) {

                                int productId =
                                    rs.getInt("product_id");

                                int quantity =
                                    rs.getInt("quantity");

                                if (productId <= 0 || quantity <= 0) {
                                    throw new SQLException(
                                        "Invalid component in bundle "
                                        + bundleId
                                    );
                                }

                                components.add(
                                    new BundleComponent(
                                        productId, quantity
                                    )
                                );

                                productIds.add(productId);
                            }
                        }

                        if (components.isEmpty()) {
                            throw new SQLException(
                                "Bundle " + bundleId
                                + " has no components."
                            );
                        }

                        BundleData old = bundles.get(bundleId);

                        bundles.put(
                            bundleId,
                            new BundleData(
                                old.name(),
                                old.price(),
                                components
                            )
                        );
                    }
                }

                // Calculate combined stock requirements.
                // TreeMap ensures products are locked in ascending ID order.
                Map<Integer, Long> requiredStock = new TreeMap<>();

                for (SaleItem item : requestedItems) {

                    if (!item.isBundle()) {

                        int productId = item.getProductId();

                        long required = Math.addExact(
                            requiredStock.getOrDefault(productId, 0L),
                            item.getQuantity()
                        );

                        requiredStock.put(productId, required);

                    } else {

                        BundleData bundle =
                            bundles.get(item.getBundleId());

                        for (BundleComponent component
                                : bundle.components()) {

                            long componentQuantity =
                                Math.multiplyExact(
                                    (long) component.quantityPerBundle(),
                                    item.getQuantity()
                                );

                            long required = Math.addExact(
                                requiredStock.getOrDefault(
                                    component.productId(), 0L
                                ),
                                componentQuantity
                            );

                            requiredStock.put(
                                component.productId(), required
                            );
                        }
                    }
                }

                // Lock and validate all required products.
                Map<Integer, ProductData> products = new HashMap<>();

                try (PreparedStatement stmt =
                         conn.prepareStatement(productSql)) {

                    for (Map.Entry<Integer, Long> entry
                            : requiredStock.entrySet()) {

                        int productId = entry.getKey();
                        long required = entry.getValue();

                        if (required > Integer.MAX_VALUE) {
                            throw new SQLException(
                                "Required stock quantity is too large."
                            );
                        }

                        stmt.setInt(1, productId);

                        try (ResultSet rs = stmt.executeQuery()) {

                            if (!rs.next()) {
                                throw new SQLException(
                                    "Product not found: " + productId
                                );
                            }

                            String name =
                                rs.getString("product_name");

                            long price =
                                rs.getLong("price_centavos");

                            int available =
                                rs.getInt("quantity");

                            boolean active =
                                rs.getBoolean("is_active");

                            if (!active) {
                                throw new SQLException(
                                    name + " is inactive."
                                );
                            }

                            if (required > available) {
                                throw new SQLException(
                                    "Insufficient stock for " + name
                                    + ". Required: " + required
                                    + ", Available: " + available
                                );
                            }

                            products.put(
                                productId,
                                new ProductData(
                                    name, available, active, price
                                )
                            );
                        }
                    }
                }

                // Verify sale lines using database prices and names.
                List<VerifiedLine> verifiedItems = new ArrayList<>();

                long totalCentavos = 0;

                for (SaleItem item : requestedItems) {

                    int productId = item.getProductId();
                    int bundleId = item.getBundleId();

                    String name;
                    long price;
                    List<VerifiedComponent> snapshots =
                        new ArrayList<>();

                    if (!item.isBundle()) {

                        ProductData product = products.get(productId);

                        name = product.name();
                        price = product.price();

                    } else {

                        BundleData bundle = bundles.get(bundleId);

                        name = bundle.name();
                        price = bundle.price();

                        for (BundleComponent component
                                : bundle.components()) {

                            ProductData product =
                                products.get(component.productId());

                            long totalQuantity = Math.multiplyExact(
                                (long) component.quantityPerBundle(),
                                item.getQuantity()
                            );

                            snapshots.add(
                                new VerifiedComponent(
                                    component.productId(),
                                    product.name(),
                                    component.quantityPerBundle(),
                                    Math.toIntExact(totalQuantity)
                                )
                            );
                        }
                    }

                    long subtotal = Math.multiplyExact(
                        price, item.getQuantity()
                    );

                    totalCentavos = Math.addExact(
                        totalCentavos, subtotal
                    );

                    verifiedItems.add(
                        new VerifiedLine(
                            productId,
                            bundleId,
                            name,
                            price,
                            item.getQuantity(),
                            subtotal,
                            snapshots
                        )
                    );
                }

                // Insert the sale header.
                long saleId;

                try (PreparedStatement stmt = conn.prepareStatement(
                        saleSql, Statement.RETURN_GENERATED_KEYS)) {

                    stmt.setInt(1, sale.getProcessedBy());
                    stmt.setLong(2, totalCentavos);

                    stmt.executeUpdate();

                    try (ResultSet keys = stmt.getGeneratedKeys()) {

                        if (!keys.next()) {
                            throw new SQLException(
                                "Failed to create sale record."
                            );
                        }

                        saleId = keys.getLong(1);
                    }
                }

                // Insert sale lines and bundle component snapshots.
                try (PreparedStatement itemStmt =
                         conn.prepareStatement(
                             saleItemSql,
                             Statement.RETURN_GENERATED_KEYS
                         );
                     PreparedStatement componentStmt =
                         conn.prepareStatement(componentSql)) {

                    for (VerifiedLine item : verifiedItems) {

                        itemStmt.setLong(1, saleId);

                        if (item.bundleId() > 0) {
                            itemStmt.setNull(2, Types.INTEGER);
                            itemStmt.setInt(3, item.bundleId());
                        } else {
                            itemStmt.setInt(2, item.productId());
                            itemStmt.setNull(3, Types.INTEGER);
                        }

                        itemStmt.setString(4, item.name());
                        itemStmt.setLong(5, item.price());
                        itemStmt.setInt(6, item.quantity());
                        itemStmt.setLong(7, item.subtotal());

                        itemStmt.executeUpdate();

                        if (item.bundleId() > 0) {

                            long saleItemId;

                            try (ResultSet keys =
                                     itemStmt.getGeneratedKeys()) {

                                if (!keys.next()) {
                                    throw new SQLException(
                                        "Failed to create bundle sale item."
                                    );
                                }

                                saleItemId = keys.getLong(1);
                            }

                            for (VerifiedComponent component
                                    : item.components()) {

                                componentStmt.setLong(1, saleItemId);
                                componentStmt.setInt(
                                    2, component.productId()
                                );
                                componentStmt.setString(
                                    3, component.productName()
                                );
                                componentStmt.setInt(
                                    4, component.quantityPerBundle()
                                );
                                componentStmt.setInt(
                                    5, component.totalQuantityDeducted()
                                );

                                componentStmt.executeUpdate();
                            }
                        }
                    }
                }

                // Deduct all required product stock.
                try (PreparedStatement stmt =
                         conn.prepareStatement(stockSql)) {

                    for (Map.Entry<Integer, Long> entry
                            : requiredStock.entrySet()) {

                        int quantity = Math.toIntExact(entry.getValue());
                        int productId = entry.getKey();

                        stmt.setInt(1, quantity);
                        stmt.setInt(2, productId);
                        stmt.setInt(3, quantity);

                        if (stmt.executeUpdate() != 1) {
                            throw new SQLException(
                                "Failed to update stock for product "
                                + productId
                            );
                        }
                    }
                }

                conn.commit();

                return saleId;

            } catch (SQLException | RuntimeException ex) {

                try {
                    conn.rollback();
                } catch (SQLException rollbackError) {
                    ex.addSuppressed(rollbackError);
                }

                if (ex instanceof ArithmeticException) {
                    throw new SQLException(
                        "Sale amount or quantity is too large.", ex
                    );
                }

                throw ex;
            }
        }
    }

    // Retrieves one sale and all its items.
    public Sale getSaleById(long saleId) throws SQLException {

        String sql = """
            SELECT sale_id, processed_by,
                   total_centavos, sale_date
            FROM sales
            WHERE sale_id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, saleId);

            try (ResultSet rs = stmt.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                return buildSale(conn, rs);
            }
        }
    }

    // Retrieves all sales, newest first.
    public List<Sale> getAllSales() throws SQLException {

        List<Sale> sales = new ArrayList<>();

        String sql = """
            SELECT sale_id, processed_by,
                   total_centavos, sale_date
            FROM sales
            ORDER BY sale_date DESC, sale_id DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                long id = rs.getLong("sale_id");
                int processedBy = rs.getInt("processed_by");
                long total = rs.getLong("total_centavos");

                Timestamp timestamp = rs.getTimestamp("sale_date");

                LocalDateTime date = timestamp.toLocalDateTime();

                List<SaleItem> items = getSaleItems(conn, id);

                sales.add(new Sale(
                    id, processedBy, total, date, items
                ));
            }
        }

        return sales;
    }

    // Retrieves all items belonging to a particular sale.
    private List<SaleItem> getSaleItems(
            Connection conn, long saleId) throws SQLException {

        List<SaleItem> items = new ArrayList<>();

        String sql = """
            SELECT sale_item_id, sale_id, product_id, bundle_id,
                   product_name, unit_price_centavos,
                   quantity, subtotal_centavos
            FROM sale_items
            WHERE sale_id = ?
            ORDER BY sale_item_id
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, saleId);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    int productId = rs.getInt("product_id");
                    int bundleId = rs.getInt("bundle_id");

                    items.add(new SaleItem(
                        rs.getLong("sale_item_id"),
                        rs.getLong("sale_id"),
                        productId,
                        bundleId,
                        rs.getString("product_name"),
                        rs.getLong("unit_price_centavos"),
                        rs.getInt("quantity"),
                        rs.getLong("subtotal_centavos")
                    ));
                }
            }
        }

        return items;
    }

    // Builds a complete Sale object from a database result.
    private Sale buildSale(
            Connection conn, ResultSet rs) throws SQLException {

        long saleId = rs.getLong("sale_id");
        int processedBy = rs.getInt("processed_by");
        long total = rs.getLong("total_centavos");

        Timestamp timestamp = rs.getTimestamp("sale_date");

        LocalDateTime date = timestamp.toLocalDateTime();

        List<SaleItem> items = getSaleItems(conn, saleId);

        return new Sale(
            saleId, processedBy, total, date, items
        );
    }
}
