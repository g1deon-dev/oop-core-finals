
package com.mycompany.oop.core.finals;

import com.mycompany.oop.core.finals.model.Sale;
import com.mycompany.oop.core.finals.model.SaleItem;
import com.mycompany.oop.core.finals.model.User;
import com.mycompany.oop.core.finals.security.AuthorizationService;
import com.mycompany.oop.core.finals.security.UserSession;
import com.mycompany.oop.core.finals.service.AuthService;
import com.mycompany.oop.core.finals.service.SalesService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SalesTest {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        UserSession session = new UserSession();
        AuthService authService = new AuthService(session);
        AuthorizationService authorizationService =
                new AuthorizationService(session);

        SalesService salesService =
                new SalesService(authorizationService, session);

        try {
            System.out.println("=== SALES MANAGEMENT TEST ===");

            System.out.print("Chairperson username: ");
            String username = scanner.nextLine();

            System.out.print("Password: ");
            String password = scanner.nextLine();

            User user = authService.login(username, password);

            System.out.println("\nLogin successful!");
            System.out.println("Logged in as: " + user.getFullName());
            System.out.println("Role: " + user.getRole());

            System.out.println("\n=== RECORDING TEST SALE ===");

            List<SaleItem> items = new ArrayList<>();

            // 2 Org Shirts (Product ID 1)
            items.add(new SaleItem(1, "", 0, 2, 0));

            // 3 Org Pens (Product ID 7)
            items.add(new SaleItem(7, "", 0, 3, 0));

            long saleId = salesService.recordSale(items);

            System.out.println("Sale recorded successfully!");
            System.out.println("Generated Sale ID: " + saleId);

            System.out.println("\n=== RETRIEVING SALE ===");

            Sale sale = salesService.getSaleById(saleId);

            if (sale != null) {
                System.out.println("Sale ID: " + sale.getSaleId());
                System.out.println(
                        "Total: ₱" + sale.getTotalCentavos() / 100.0
                );

                System.out.println("\nItems:");

                for (SaleItem item : sale.getItems()) {
                    System.out.println(
                            item.getProductName()
                            + " | Quantity: " + item.getQuantity()
                            + " | Unit Price: ₱"
                            + item.getUnitPriceCentavos() / 100.0
                            + " | Subtotal: ₱"
                            + item.getSubtotalCentavos() / 100.0
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
