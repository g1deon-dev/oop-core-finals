
package com.mycompany.oop.core.finals;

import com.mycompany.oop.core.finals.dao.ProductDAO;
import com.mycompany.oop.core.finals.model.Bundle;
import com.mycompany.oop.core.finals.model.BundleItem;
import com.mycompany.oop.core.finals.model.Product;
import com.mycompany.oop.core.finals.model.User;
import com.mycompany.oop.core.finals.security.AuthorizationService;
import com.mycompany.oop.core.finals.security.UserSession;
import com.mycompany.oop.core.finals.service.AuthService;
import com.mycompany.oop.core.finals.service.BundleService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BundleTest {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        UserSession session = new UserSession();
        AuthService authService = new AuthService(session);

        AuthorizationService authorizationService =
                new AuthorizationService(session);

        BundleService bundleService =
                new BundleService(authorizationService);

        ProductDAO productDAO = new ProductDAO();

        try {
            System.out.println("=== BUNDLE MANAGEMENT TEST ===");

            System.out.print("Username: ");
            String username = scanner.nextLine();

            System.out.print("Password: ");
            String password = scanner.nextLine();

            User user = authService.login(username, password);

            System.out.println("\nLogin successful!");
            System.out.println("Logged in as: " + user.getFullName());
            System.out.println("Role: " + user.getRole());

            System.out.println("\n=== CHECKING INVENTORY BEFORE ===");

            List<Product> productsBefore = productDAO.getAllProducts();

            for (Product product : productsBefore) {
                if (product.getProductId() == 1
                        || product.getProductId() == 7) {

                    System.out.println(
                        product.getProductName()
                        + " | Stock: " + product.getQuantity()
                    );
                }
            }

            System.out.println("\n=== CREATING TEST BUNDLE ===");

            List<BundleItem> items = new ArrayList<>();

            items.add(new BundleItem(1, "Org Shirt", 2));
            items.add(new BundleItem(7, "Org Pen - Blue", 3));

            Bundle bundle = new Bundle(
                "Test Organization Package",
                55000,
                items
            );

            bundleService.createBundle(bundle);

            System.out.println("Bundle created successfully!");

            System.out.println("\n=== RETRIEVING CREATED BUNDLE ===");

            // Find the bundle using its unique name.
            List<Bundle> bundles = bundleService.getAllBundles();

            Bundle retrievedBundle = null;

            for (Bundle currentBundle : bundles) {
                if (currentBundle.getBundleName().equals(
                        "Test Organization Package")) {

                    retrievedBundle = currentBundle;
                    break;
                }
            }

            if (retrievedBundle == null) {
                throw new Exception("Created bundle could not be retrieved.");
            }

            System.out.println(
                "Bundle ID: " + retrievedBundle.getBundleId()
            );

            System.out.println(
                "Bundle Name: " + retrievedBundle.getBundleName()
            );

            System.out.println(
                "Bundle Price: ₱"
                + retrievedBundle.getPriceCentavos() / 100.0
            );

            System.out.println("\nComponents:");

            for (BundleItem item : retrievedBundle.getItems()) {
                System.out.println(
                    item.getProductName()
                    + " | Product ID: " + item.getProductId()
                    + " | Quantity: " + item.getQuantity()
                );
            }

            System.out.println("\n=== CHECKING INVENTORY AFTER ===");

            List<Product> productsAfter = productDAO.getAllProducts();

            for (Product product : productsAfter) {
                if (product.getProductId() == 1
                        || product.getProductId() == 7) {

                    System.out.println(
                        product.getProductName()
                        + " | Stock: " + product.getQuantity()
                    );
                }
            }

            authService.logout();

            System.out.println("\n=== TEST COMPLETED ===");

        } catch (Exception e) {
            System.out.println("\nTEST FAILED!");
            System.out.println("Reason: " + e.getMessage());
            e.printStackTrace();

        } finally {
            scanner.close();
        }
    }
}
