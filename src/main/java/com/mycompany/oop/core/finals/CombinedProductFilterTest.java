package com.mycompany.oop.core.finals;

import com.mycompany.oop.core.finals.model.*;
import com.mycompany.oop.core.finals.security.*;
import com.mycompany.oop.core.finals.service.*;

import java.util.List;

public class CombinedProductFilterTest {

    public static void main(String[] args) {
        UserSession session = new UserSession();

        session.startSession(new User(
            0,
            "combined_test",
            "Combined Test",
            UserRole.CHAIRPERSON,
            true
        ));

        ProductService service =
            new ProductService(new AuthorizationService(session));

        try {
            System.out.println("=== COMBINED FILTER AND SORT TEST ===");

            testCombination(
                service,
                StockStatus.LOW_STOCK,
                ProductSortField.QUANTITY,
                SortDirection.ASCENDING
            );

            testCombination(
                service,
                StockStatus.IN_STOCK,
                ProductSortField.PRICE,
                SortDirection.DESCENDING
            );

            testCombination(
                service,
                StockStatus.OUT_OF_STOCK,
                ProductSortField.NAME,
                SortDirection.ASCENDING
            );

            System.out.println("\nALL COMBINED TESTS PASSED.");

        } catch (Exception e) {
            System.out.println("\nTEST FAILED: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } finally {
            session.endSession();
        }
    }

    private static void testCombination(
            ProductService service,
            StockStatus status,
            ProductSortField field,
            SortDirection direction) throws Exception {

        System.out.println("\n--- " + status + " | "
            + field + " | " + direction + " ---");

        List<Product> products =
            service.getProductsByStockStatusSorted(
                status, field, direction
            );

        for (Product product : products) {
            int quantity = product.getQuantity();
            int threshold = product.getLowStockThreshold();

            boolean validStatus = switch (status) {
                case IN_STOCK -> quantity > threshold;
                case LOW_STOCK ->
                    quantity > 0 && quantity <= threshold;
                case OUT_OF_STOCK -> quantity == 0;
            };

            if (!validStatus) {
                throw new AssertionError(
                    "Incorrect stock status for: "
                    + product.getProductName()
                );
            }

            System.out.printf(
                "ID: %d | Name: %s | Quantity: %d | Price: %d%n",
                product.getProductId(),
                product.getProductName(),
                quantity,
                product.getPriceCentavos()
            );
        }

        for (int i = 1; i < products.size(); i++) {
            Product previous = products.get(i - 1);
            Product current = products.get(i);

            int comparison = switch (field) {
                case ID -> Integer.compare(
                    previous.getProductId(), current.getProductId()
                );
                case NAME -> previous.getProductName().compareToIgnoreCase(
                    current.getProductName()
                );
                case QUANTITY -> Integer.compare(
                    previous.getQuantity(), current.getQuantity()
                );
                case PRICE -> Integer.compare(
                    previous.getPriceCentavos(), current.getPriceCentavos()
                );
            };

            if (direction == SortDirection.DESCENDING) {
                comparison = -comparison;
            }

            if (comparison > 0) {
                throw new AssertionError("Incorrect sorting order.");
            }
        }

        System.out.println("Products found: " + products.size());
        System.out.println("Status and sorting: PASSED");
    }
}
