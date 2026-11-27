package com.mycompany.oop.core.finals;

import com.mycompany.oop.core.finals.model.*;
import com.mycompany.oop.core.finals.security.*;
import com.mycompany.oop.core.finals.service.*;

import java.util.List;

public class InventoryAndWorkflowTest {

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException("FAILED: " + message);
        }
        System.out.println("  [PASS] " + message);
    }

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println(" INVENTORY & WORKFLOW INTEGRATION TEST");
        System.out.println("==================================================");

        UserSession session = new UserSession();
        AuthorizationService authService = new AuthorizationService(session);
        ProductService productService = new ProductService(authService);
        InventoryService inventoryService = new InventoryService(authService, session);

        try {
            // Ensure test users exist in the database for foreign key integrity
            com.mycompany.oop.core.finals.dao.UserDAO userDAO = new com.mycompany.oop.core.finals.dao.UserDAO();
            if (userDAO.findByUsername("admin_test") == null) {
                userDAO.createUser("admin_test", "Admin Tester", "hash", "salt", UserRole.CHAIRPERSON);
            }
            if (userDAO.findByUsername("staff_test") == null) {
                userDAO.createUser("staff_test", "Staff Tester", "hash", "salt", UserRole.COMMITTEE_STAFF);
            }

            User adminUser = userDAO.findByUsername("admin_test");
            User staffUser = userDAO.findByUsername("staff_test");
            // ----------------------------------------------------
            // TEST 1: Committee Staff View Permissions
            // ----------------------------------------------------
            System.out.println("\n--- TEST 1: Committee Staff View & Search Access ---");
            session.startSession(staffUser);

            List<Product> products = productService.getAllProducts();
            check(products != null, "Staff can view product list without SecurityException");

            if (!products.isEmpty()) {
                Product first = products.get(0);
                Product fetched = productService.getProductById(first.getProductId());
                check(fetched != null && fetched.getProductId() == first.getProductId(),
                      "Staff can fetch product by ID (" + first.getProductName() + ")");

                List<Product> searchResults = productService.searchProducts(first.getProductName().substring(0, Math.min(3, first.getProductName().length())));
                check(!searchResults.isEmpty(), "Staff can search products without permission error");
            }

            // Verify Staff CANNOT restock or add product
            boolean blockedFromRestock = false;
            try {
                productService.restockProduct(1, 10);
            } catch (SecurityException se) {
                blockedFromRestock = true;
            }
            check(blockedFromRestock, "Staff is correctly blocked from direct restocking (SecurityException)");

            // ----------------------------------------------------
            // TEST 2: Admin Direct Restock & Audit Trail
            // ----------------------------------------------------
            System.out.println("\n--- TEST 2: Admin Direct Restock & Audit Logging ---");
            session.startSession(adminUser);

            if (products.isEmpty()) {
                System.out.println("No products found in DB to restock. Creating a test product...");
                String testName = "Test Item " + (System.currentTimeMillis() % 100000);
                productService.addProduct(new Product(0, testName, 15000, 10, 5, true));
                products = productService.getAllProducts();
            }

            Product testProduct = products.get(0);
            int targetId = testProduct.getProductId();
            int initialStock = productService.getProductById(targetId).getQuantity();

            int restockUnits = 5;
            inventoryService.restockProduct(targetId, restockUnits);

            int updatedStock = productService.getProductById(targetId).getQuantity();
            check(updatedStock == initialStock + restockUnits,
                  "Direct restock increased stock from " + initialStock + " to " + updatedStock);

            List<StockMovement> movements = inventoryService.getMovementsByProductId(targetId);
            check(!movements.isEmpty(), "Stock movement audit record was logged");
            check(movements.get(0).getChangeAmount() == restockUnits && "RESTOCK".equals(movements.get(0).getReason()),
                  "Movement correctly records amount (" + restockUnits + ") and reason (RESTOCK)");

            // ----------------------------------------------------
            // TEST 3: Stock-Change Request Lifecycle (Staff -> Admin -> Receive)
            // ----------------------------------------------------
            System.out.println("\n--- TEST 3: Stock-Change Request Approval Workflow ---");

            // Step 3a: Staff submits request
            session.startSession(staffUser);
            int requestedQuantity = 8;
            int requestId = inventoryService.submitStockChangeRequest(
                targetId, requestedQuantity, "Need extra inventory for weekend org fair"
            );
            check(requestId > 0, "Staff submitted stock change request (ID: " + requestId + ")");

            StockChangeRequest req = inventoryService.getAllRequests().stream()
                .filter(r -> r.getRequestId() == requestId)
                .findFirst().orElse(null);
            check(req != null && req.getStatus() == StockChangeStatus.PENDING,
                  "Request status is initially PENDING");

            // Step 3b: Admin approves request
            session.startSession(adminUser);
            inventoryService.approveStockChangeRequest(requestId, "Approved by Chairperson");

            StockChangeRequest approvedReq = inventoryService.getAllRequests().stream()
                .filter(r -> r.getRequestId() == requestId)
                .findFirst().orElse(null);
            check(approvedReq != null && approvedReq.getStatus() == StockChangeStatus.APPROVED,
                  "Admin approved request, status is now APPROVED");

            // Step 3c: Goods arrive, receive request into stock
            int stockBeforeReceive = productService.getProductById(targetId).getQuantity();
            inventoryService.receiveStockChangeRequest(requestId);

            int stockAfterReceive = productService.getProductById(targetId).getQuantity();
            check(stockAfterReceive == stockBeforeReceive + requestedQuantity,
                  "Receiving request increased stock from " + stockBeforeReceive + " to " + stockAfterReceive);

            StockChangeRequest receivedReq = inventoryService.getAllRequests().stream()
                .filter(r -> r.getRequestId() == requestId)
                .findFirst().orElse(null);
            check(receivedReq != null && receivedReq.getStatus() == StockChangeStatus.RECEIVED,
                  "Request status is now RECEIVED");

            List<StockMovement> fulfillMovements = inventoryService.getMovementsByProductId(targetId);
            check(fulfillMovements.get(0).getChangeAmount() == requestedQuantity
                  && "REQUEST_FULFILLED".equals(fulfillMovements.get(0).getReason()),
                  "Movement logged for request fulfillment with reference ID: " + fulfillMovements.get(0).getReferenceId());

            // ----------------------------------------------------
            // TEST 4: Request Cancellation by Requester
            // ----------------------------------------------------
            System.out.println("\n--- TEST 4: Staff Request Cancellation ---");
            session.startSession(staffUser);
            int cancelReqId = inventoryService.submitStockChangeRequest(targetId, 2, "Test cancellation");
            inventoryService.cancelStockChangeRequest(cancelReqId);

            StockChangeRequest cancelledReq = inventoryService.getAllRequests().stream()
                .filter(r -> r.getRequestId() == cancelReqId)
                .findFirst().orElse(null);
            check(cancelledReq != null && cancelledReq.getStatus() == StockChangeStatus.CANCELLED,
                  "Requester successfully cancelled their own pending request");

            // ----------------------------------------------------
            // TEST 5: Dashboard Metrics
            // ----------------------------------------------------
            System.out.println("\n--- TEST 5: Dashboard Aggregations ---");
            int totalProducts = inventoryService.getTotalProductCount();
            int totalUnits = inventoryService.getTotalStockUnits();
            int lowStockCount = inventoryService.getLowStockCount();

            check(totalProducts >= 1, "Dashboard Total Products: " + totalProducts);
            check(totalUnits >= 1, "Dashboard Total Stock Units: " + totalUnits);
            check(lowStockCount >= 0, "Dashboard Low Stock Items Count: " + lowStockCount);

            System.out.println("\n==================================================");
            System.out.println(" ALL INVENTORY & SALES TESTS PASSED SUCCESSFULLY!");
            System.out.println("==================================================");

        } catch (Exception e) {
            System.out.println("\nTEST FAILED WITH EXCEPTION:");
            e.printStackTrace();
            System.exit(1);
        } finally {
            session.endSession();
        }
    }
}
