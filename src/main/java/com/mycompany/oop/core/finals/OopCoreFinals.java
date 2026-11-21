/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.oop.core.finals;

import com.mycompany.oop.core.finals.service.AuthService;

/**
 *
 * @author g1deon-dev
 */


import com.mycompany.oop.core.finals.service.ProductService;
import com.mycompany.oop.core.finals.model.Product;
import com.mycompany.oop.core.finals.model.User;
import com.mycompany.oop.core.finals.model.UserRole;
import com.mycompany.oop.core.finals.security.AuthorizationService;
import com.mycompany.oop.core.finals.security.UserSession;
import com.mycompany.oop.core.finals.service.AuthService;

import java.sql.SQLException;
import java.util.Scanner;

public class OopCoreFinals {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        
        UserSession session = new UserSession();
        AuthService authService = new AuthService(session);

        AuthorizationService authorizationService =
            new AuthorizationService(session);

        ProductService service =
            new ProductService(authorizationService);


        try {
            System.out.println("=== INITIAL ADMIN SETUP ===");

            System.out.print("Username: ");
            String username = scanner.nextLine();

            System.out.print("Full name: ");
            String fullName = scanner.nextLine();

            System.out.print("Password (minimum 8 characters): ");
            String password = scanner.nextLine();

            authService.createInitialChairperson(
                username,
                fullName,
                password
            );

            System.out.println("Chairperson account created!");

        } catch (Exception e) {
            System.out.println("Setup failed: " + e.getMessage());
            scanner.close();
            return;
        }

        try {
            System.out.println("\n=== LOGIN TEST ===");

            System.out.print("Username: ");
            String username = scanner.nextLine();

            System.out.print("Password: ");
            String password = scanner.nextLine();

            User user = authService.login(username, password);

            System.out.println("Welcome, " + user.getFullName());
            System.out.println("Role: " + user.getRole());

            authorizationService.requireProductManagementAccess();

            System.out.println("Product management access granted!");

            // TEMPORARY TEST ACCOUNT CREATION
            System.out.println("\n=== CREATE TEST ACCOUNTS ===");

            authService.createUser(
                "businessmanager",
                "Business Manager",
                "YourPassword123",
                UserRole.BUSINESS_MANAGER
            );

            authService.createUser(
                "committeestaff",
                "Committee Staff",
                "YourPassword123",
                UserRole.COMMITTEE_STAFF
            );

            System.out.println("Test accounts created!");

        } catch (Exception e) {
            System.out.println("Access failed: " + e.getMessage());
            scanner.close();
            return;
        }




        try {

            System.out.println("=== INACTIVE PRODUCTS ===");

            for (Product product : service.getInactiveProducts()) {
                System.out.println(
                    product.getProductId() + " - " +
                    product.getProductName()
                );
            }

            // Reactivate product ID 7
            service.reactivateProduct(7);

            System.out.println("\nProduct reactivated successfully.");

            //service.deactivateProduct(7);

            //System.out.println("Product deactivated successfully!");

            // TEST PRODUCT UPDATE
            /*Product updatedProduct = new Product(
                7,              // Replace with Org Pen's actual ID
                "Org Pen - Blue",
                2500,           // ₱25.00
                30,             // Keep original quantity
                5,               // New low-stock threshold
                true
            );

            service.updateProduct(updatedProduct);

            System.out.println("Product updated successfully!");
            */

            

        boolean productAdded = false;

        while (!productAdded) {
            try {
                System.out.println("=== ADD NEW PRODUCT ===");

                System.out.print("Product name: ");
                String name = scanner.nextLine();

                System.out.print("Price (in pesos): ");
                int price = (int) (scanner.nextDouble() * 100);
                scanner.nextLine();

                System.out.print("Quantity: ");
                int quantity = scanner.nextInt();
                scanner.nextLine();

                System.out.print("Low-stock threshold: ");
                int threshold = scanner.nextInt();
                scanner.nextLine();

                Product product = new Product(
                    0, name, price, quantity, threshold, true
                );

                service.addProduct(product);

                System.out.println("Product added successfully!");
                productAdded = true;

            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input: " + e.getMessage());
                System.out.println("Please try again.\n");

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                break;
            }
        }

            System.out.println("\n=== CURRENT INVENTORY ===");

            for (Product p : service.getAllProducts()) {
                System.out.println(
                    p.getProductId() + " | " +
                    p.getProductName() + " | " +
                    "₱" + p.getPriceCentavos() / 100.0 + " | " +
                    p.getQuantity()
                );
            }

        } catch (SQLException error) {
            System.out.println("Database error: " + error.getMessage());

        } catch (IllegalArgumentException error) {
            System.out.println("Invalid input: " + error.getMessage());

        } finally {
            scanner.close();
        }
    }
}