package com.mycompany.oop.core.finals;

import com.mycompany.oop.core.finals.model.Sale;
import com.mycompany.oop.core.finals.model.SaleItem;
import com.mycompany.oop.core.finals.model.User;
import com.mycompany.oop.core.finals.security.AuthorizationService;
import com.mycompany.oop.core.finals.security.UserSession;
import com.mycompany.oop.core.finals.service.AuthService;
import com.mycompany.oop.core.finals.service.SalesService;
import com.mycompany.oop.core.finals.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class BundleSaleTest {

    private static int getStock(int productId) throws Exception {
        String sql = """
            SELECT quantity
            FROM products
            WHERE product_id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new Exception(
                        "Product not found: " + productId
                    );
                }

                return rs.getInt("quantity");
            }
        }
    }

    private static void check(boolean condition, String message)
            throws Exception {

        if (!condition) {
            throw new Exception("TEST FAILED: " + message);
        }
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        UserSession session = new UserSession();
        AuthService authService = new AuthService(session);

        AuthorizationService authorizationService =
                new AuthorizationService(session);

        SalesService salesService =
                new SalesService(authorizationService, session);

        try {
            System.out.println("=== BUNDLE SALES TEST ===");

            System.out.print("Chairperson username: ");
            String username = scanner.nextLine();

            System.out.print("Password: ");
            String password = scanner.nextLine();

            User user = authService.login(username, password);

            System.out.println("\nLogin successful!");
            System.out.println("Logged in as: " + user.getFullName());
            System.out.println("Role: " + user.getRole());

            // Verify the expected bundle before making a sale.
            System.out.println("\n=== CHECKING PRECONDITIONS ===");

            String bundleSql = """
                SELECT bundle_name, price_centavos, is_active
                FROM bundles
                WHERE bundle_id = 1
                """;

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt =
                         conn.prepareStatement(bundleSql);
                 ResultSet rs = stmt.executeQuery()) {

                check(rs.next(), "Bundle ID 1 does not exist.");

                check(
                    rs.getString("bundle_name").equals(
                        "Test Organization Package"
                    ),
                    "Unexpected bundle name."
                );

                check(
                    rs.getLong("price_centavos") == 55000,
                    "Unexpected bundle price."
                );

                check(
                    rs.getBoolean("is_active"),
                    "Bundle is inactive."
                );
            }

            // Verify the bundle's exact components.
            Map<Integer, Integer> expectedComponents =
                    new HashMap<>();

            expectedComponents.put(1, 2);
            expectedComponents.put(7, 3);

            Map<Integer, Integer> actualComponents =
                    new HashMap<>();

            String componentsSql = """
                SELECT product_id, quantity
                FROM bundle_items
                WHERE bundle_id = 1
                """;

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt =
                         conn.prepareStatement(componentsSql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    actualComponents.put(
                        rs.getInt("product_id"),
                        rs.getInt("quantity")
                    );
                }
            }

            check(
                actualComponents.equals(expectedComponents),
                "Bundle components do not match the expected setup."
            );

            int shirtBefore = getStock(1);
            int penBefore = getStock(7);

            check(shirtBefore >= 2, "Insufficient Org Shirt stock.");
            check(penBefore >= 3, "Insufficient Org Pen - Blue stock.");

            System.out.println("Bundle: Test Organization Package");
            System.out.println("Price: ₱550.00");
            System.out.println("Org Shirt stock before: " + shirtBefore);
            System.out.println("Org Pen - Blue stock before: " + penBefore);

            // Record exactly one bundle sale.
            System.out.println("\n=== RECORDING BUNDLE SALE ===");

            List<SaleItem> items = List.of(
                SaleItem.forBundle(1, "", 0, 1, 0)
            );

            long saleId = salesService.recordSale(items);

            System.out.println("Sale recorded. Sale ID: " + saleId);

            // Retrieve and verify the sale.
            System.out.println("\n=== VERIFYING SALE RECORD ===");

            Sale sale = salesService.getSaleById(saleId);

            check(sale != null, "Sale could not be retrieved.");
            check(
                sale.getTotalCentavos() == 55000,
                "Incorrect sale total."
            );
            check(
                sale.getItems().size() == 1,
                "Expected exactly one sale item."
            );

            SaleItem soldItem = sale.getItems().get(0);

            check(soldItem.isBundle(), "Sale item is not marked as a bundle.");
            check(
                soldItem.getBundleId() == 1,
                "Incorrect bundle ID in sale item."
            );
            check(
                soldItem.getProductId() == 0,
                "Bundle sale item unexpectedly has a product ID."
            );
            check(
                soldItem.getProductName().equals(
                    "Test Organization Package"
                ),
                "Incorrect bundle name in sale item."
            );
            check(
                soldItem.getUnitPriceCentavos() == 55000,
                "Incorrect bundle unit price."
            );
            check(
                soldItem.getQuantity() == 1,
                "Incorrect bundle quantity."
            );
            check(
                soldItem.getSubtotalCentavos() == 55000,
                "Incorrect bundle subtotal."
            );

            System.out.println("Sale total: ₱"
                    + sale.getTotalCentavos() / 100.0);
            System.out.println("Sale item: " + soldItem.getProductName());
            System.out.println("Bundle ID: " + soldItem.getBundleId());

            // Verify the historical component snapshot.
            System.out.println("\n=== VERIFYING COMPONENT SNAPSHOT ===");

            Map<Integer, Integer> expectedDeductions =
                    new HashMap<>();

            expectedDeductions.put(1, 2);
            expectedDeductions.put(7, 3);

            Map<Integer, Integer> actualDeductions =
                    new HashMap<>();

            String snapshotSql = """
                SELECT product_id, product_name,
                       quantity_per_bundle, total_quantity_deducted
                FROM sale_bundle_components
                WHERE sale_item_id = ?
                """;

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt =
                         conn.prepareStatement(snapshotSql)) {

                stmt.setLong(1, soldItem.getSaleItemId());

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {

                        int productId = rs.getInt("product_id");
                        int quantityPerBundle =
                                rs.getInt("quantity_per_bundle");
                        int totalDeducted =
                                rs.getInt("total_quantity_deducted");

                        check(
                            quantityPerBundle == totalDeducted,
                            "Incorrect component deduction for product "
                                    + productId
                        );

                        actualDeductions.put(
                            productId, totalDeducted
                        );

                        System.out.println(
                            rs.getString("product_name")
                            + " | Per bundle: " + quantityPerBundle
                            + " | Deducted: " + totalDeducted
                        );
                    }
                }
            }

            check(
                actualDeductions.equals(expectedDeductions),
                "Component snapshot does not match expected deductions."
            );

            // Verify inventory deductions.
            System.out.println("\n=== VERIFYING INVENTORY ===");

            int shirtAfter = getStock(1);
            int penAfter = getStock(7);

            check(
                shirtAfter == shirtBefore - 2,
                "Org Shirt stock was not deducted correctly."
            );

            check(
                penAfter == penBefore - 3,
                "Org Pen - Blue stock was not deducted correctly."
            );

            System.out.println(
                "Org Shirt: " + shirtBefore + " -> " + shirtAfter
            );

            System.out.println(
                "Org Pen - Blue: " + penBefore + " -> " + penAfter
            );

            authService.logout();

            System.out.println("\n=== ALL BUNDLE SALE TESTS PASSED ===");

        } catch (Exception e) {
            System.out.println("\nTEST FAILED!");
            System.out.println("Reason: " + e.getMessage());
            e.printStackTrace();

        } finally {
            scanner.close();
        }
    }
}
