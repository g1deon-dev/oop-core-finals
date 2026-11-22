package com.mycompany.oop.core.finals;

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
import java.util.List;
import java.util.Scanner;

public class BundleSaleFailureTest {

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
                    throw new Exception("Product not found: " + productId);
                }

                return rs.getInt("quantity");
            }
        }
    }

    private static long getSalesCount() throws Exception {
        String sql = "SELECT COUNT(*) FROM sales";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            rs.next();
            return rs.getLong(1);
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
            System.out.println("=== INSUFFICIENT STOCK TEST ===");

            System.out.print("Chairperson username: ");
            String username = scanner.nextLine();

            System.out.print("Password: ");
            String password = scanner.nextLine();

            User user = authService.login(username, password);

            System.out.println("\nLogin successful!");
            System.out.println("Logged in as: " + user.getFullName());

            // Record the database state before the attempt.
            long salesBefore = getSalesCount();
            int shirtBefore = getStock(1);
            int penBefore = getStock(7);

            System.out.println("\n=== INVENTORY BEFORE ===");
            System.out.println("Recorded sales: " + salesBefore);
            System.out.println("Org Shirt: " + shirtBefore);
            System.out.println("Org Pen - Blue: " + penBefore);

            // One bundle needs 2 shirts and 3 pens.
            // 100 bundles will exceed the available inventory.
            System.out.println("\n=== ATTEMPTING INVALID SALE ===");
            System.out.println("Attempting to sell 100 bundles...");

            boolean rejected = false;

            try {
                salesService.recordSale(
                    List.of(
                        SaleItem.forBundle(1, "", 0, 100, 0)
                    )
                );

            } catch (Exception e) {
                rejected = true;
                System.out.println("Sale correctly rejected.");
                System.out.println("Reason: " + e.getMessage());
            }

            check(rejected, "Insufficient-stock sale was not rejected.");

            // Read the database state after the attempt.
            long salesAfter = getSalesCount();
            int shirtAfter = getStock(1);
            int penAfter = getStock(7);

            System.out.println("\n=== INVENTORY AFTER ===");
            System.out.println("Recorded sales: " + salesAfter);
            System.out.println("Org Shirt: " + shirtAfter);
            System.out.println("Org Pen - Blue: " + penAfter);

            // Verify that nothing changed.
            check(
                salesAfter == salesBefore,
                "A sale record was created despite insufficient stock."
            );

            check(
                shirtAfter == shirtBefore,
                "Org Shirt stock changed after the rejected sale."
            );

            check(
                penAfter == penBefore,
                "Org Pen - Blue stock changed after the rejected sale."
            );

            authService.logout();

            System.out.println(
                "\n=== ALL INSUFFICIENT STOCK TESTS PASSED ==="
            );

        } catch (Exception e) {
            System.out.println("\nTEST FAILED!");
            System.out.println("Reason: " + e.getMessage());
            e.printStackTrace();

        } finally {
            scanner.close();
        }
    }
}
