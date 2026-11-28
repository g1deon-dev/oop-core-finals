package com.mycompany.oop.core.finals;

import com.mycompany.oop.core.finals.model.*;
import com.mycompany.oop.core.finals.security.*;
import com.mycompany.oop.core.finals.service.*;

import java.util.List;

public class StockStatusTest {

    public static void main(String[] args) {
        UserSession session = new UserSession();

        session.startSession(new User(
            0,
            "sorting_test",
            "Sorting Test",
            UserRole.CHAIRPERSON,
            true
        ));

        ProductService productService =
            new ProductService(new AuthorizationService(session));

        try {
            System.out.println("=== STOCK STATUS TEST ===");

            testStatus(productService, StockStatus.IN_STOCK);
            testStatus(productService, StockStatus.LOW_STOCK);
            testStatus(productService, StockStatus.OUT_OF_STOCK);

            System.out.println("\nALL STOCK STATUS TESTS COMPLETED.");

        } catch (Exception e) {
            System.out.println("\nTEST FAILED: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } finally {
            session.endSession();
        }
    }

    private static void testStatus(
            ProductService service,
            StockStatus status) throws Exception {

        System.out.println("\n--- " + status + " ---");

        List<Product> products =
            service.getProductsByStockStatus(status);

        for (Product product : products) {
            System.out.printf(
                "ID: %d | Name: %s | Quantity: %d | Threshold: %d%n",
                product.getProductId(),
                product.getProductName(),
                product.getQuantity(),
                product.getLowStockThreshold()
            );
        }

        System.out.println("Products found: " + products.size());
    }
}
