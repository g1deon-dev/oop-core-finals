package com.mycompany.oop.core.finals;

import com.mycompany.oop.core.finals.dao.ProductDAO;
import com.mycompany.oop.core.finals.model.Product;
import com.mycompany.oop.core.finals.model.ProductSortField;
import com.mycompany.oop.core.finals.model.SortDirection;

import java.util.List;

public class ProductSortingTest {

    public static void main(String[] args) {
        ProductDAO productDAO = new ProductDAO();

        try {
            System.out.println("=== PRODUCT SORTING TEST ===");

            testSorting(productDAO, ProductSortField.ID,
                    SortDirection.ASCENDING);

            testSorting(productDAO, ProductSortField.ID,
                    SortDirection.DESCENDING);

            testSorting(productDAO, ProductSortField.NAME,
                    SortDirection.ASCENDING);

            testSorting(productDAO, ProductSortField.NAME,
                    SortDirection.DESCENDING);

            testSorting(productDAO, ProductSortField.QUANTITY,
                    SortDirection.ASCENDING);

            testSorting(productDAO, ProductSortField.QUANTITY,
                    SortDirection.DESCENDING);

            testSorting(productDAO, ProductSortField.PRICE,
                    SortDirection.ASCENDING);

            testSorting(productDAO, ProductSortField.PRICE,
                    SortDirection.DESCENDING);

            System.out.println("\nALL SORTING TESTS COMPLETED.");

        } catch (Exception e) {
            System.out.println("\nSORTING TEST FAILED: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void testSorting(
            ProductDAO productDAO,
            ProductSortField field,
            SortDirection direction) throws Exception {

        System.out.println("\n--- " + field + " (" + direction + ") ---");

        List<Product> products =
                productDAO.getSortedProducts(field, direction);

        for (Product product : products) {
            System.out.printf(
                    "ID: %d | Name: %s | Quantity: %d | Price: ₱%.2f%n",
                    product.getProductId(),
                    product.getProductName(),
                    product.getQuantity(),
                    product.getPriceCentavos() / 100.0
            );
        }

        System.out.println("Products found: " + products.size());
    }
}
