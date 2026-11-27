package com.mycompany.oop.core.finals;

import com.mycompany.oop.core.finals.dao.UserDAO;
import com.mycompany.oop.core.finals.model.*;
import com.mycompany.oop.core.finals.security.AuthorizationService;
import com.mycompany.oop.core.finals.security.UserSession;
import com.mycompany.oop.core.finals.service.BundleService;
import com.mycompany.oop.core.finals.service.PreOrderService;
import com.mycompany.oop.core.finals.service.ProductService;

import java.util.List;

public class PreOrderTest {

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException("FAILED: " + message);
        }
        System.out.println("  [PASS] " + message);
    }

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println(" PRE-ORDER MODULE INTEGRATION TEST");
        System.out.println("==================================================");

        UserSession session = new UserSession();
        AuthorizationService authService = new AuthorizationService(session);
        ProductService productService = new ProductService(authService);
        BundleService bundleService = new BundleService(authService);
        PreOrderService preOrderService = new PreOrderService(authService, session);

        try {
            // Ensure test users exist
            UserDAO userDAO = new UserDAO();
            if (userDAO.findByUsername("admin_test") == null) {
                userDAO.createUser("admin_test", "Admin Tester", "hash", "salt", UserRole.CHAIRPERSON);
            }
            if (userDAO.findByUsername("staff_test") == null) {
                userDAO.createUser("staff_test", "Staff Tester", "hash", "salt", UserRole.COMMITTEE_STAFF);
            }

            User adminUser = userDAO.findByUsername("admin_test");
            User staffUser = userDAO.findByUsername("staff_test");

            // Ensure at least one product and one bundle exist
            session.startSession(adminUser);
            List<Product> products = productService.getAllProducts();
            if (products.isEmpty()) {
                String testProdName = "PreOrder Test Item " + (System.currentTimeMillis() % 100000);
                productService.addProduct(new Product(0, testProdName, 25000, 20, 5, true));
                products = productService.getAllProducts();
            }
            Product testProduct = products.get(0);

            List<Bundle> bundles = bundleService.getAllBundles();
            if (bundles.isEmpty()) {
                String testBundleName = "PreOrder Test Bundle " + (System.currentTimeMillis() % 100000);
                BundleItem item = new BundleItem(testProduct.getProductId(), testProduct.getProductName(), 1);
                bundleService.createBundle(new Bundle(testBundleName, 45000, List.of(item)));
                bundles = bundleService.getAllBundles();
            }
            Bundle testBundle = bundles.get(0);

            // ----------------------------------------------------
            // TEST 1: Staff Creates a Bundle Pre-Order
            // ----------------------------------------------------
            System.out.println("\n--- TEST 1: Staff Creates Bundle Pre-Order ---");
            session.startSession(staffUser);

            int qty = 2;
            long totalCentavos = testBundle.getPriceCentavos() * qty;
            int bundleOrderId = preOrderService.createPreOrder(
                "Juan Dela Cruz",
                "09171234567",
                testBundle.getBundleId(),
                null,
                qty,
                totalCentavos,
                "Size L, pick up Friday"
            );
            check(bundleOrderId > 0, "Staff created bundle pre-order (ID: " + bundleOrderId + ")");

            PreOrder fetched = preOrderService.getOrderById(bundleOrderId);
            check(fetched != null, "Fetched created pre-order by ID");
            check("Juan Dela Cruz".equals(fetched.getCustomerName()), "Customer name matches");
            check(fetched.getBundleId() != null && fetched.getBundleId() == testBundle.getBundleId(),
                  "Bundle ID matches (" + fetched.getBundleId() + ")");
            check(fetched.getProductId() == null, "Product ID is null for bundle pre-order");
            check(fetched.getStatus() == PreOrderStatus.PENDING, "Initial status is PENDING");
            check(fetched.getTotalCentavos() == totalCentavos,
                  "Total centavos matches (" + fetched.getFormattedTotal() + ")");

            // ----------------------------------------------------
            // TEST 2: Role-Based Access Control
            // ----------------------------------------------------
            System.out.println("\n--- TEST 2: Staff Blocked from Admin Status Transitions ---");
            boolean blockedMarkReady = false;
            try {
                preOrderService.markReady(bundleOrderId);
            } catch (SecurityException se) {
                blockedMarkReady = true;
            }
            check(blockedMarkReady, "Staff is blocked from markReady() (SecurityException)");

            boolean blockedFulfill = false;
            try {
                preOrderService.fulfillOrder(bundleOrderId);
            } catch (SecurityException se) {
                blockedFulfill = true;
            }
            check(blockedFulfill, "Staff is blocked from fulfillOrder() (SecurityException)");

            // ----------------------------------------------------
            // TEST 3: Admin Workflow (PENDING -> READY -> FULFILLED)
            // ----------------------------------------------------
            System.out.println("\n--- TEST 3: Admin Workflow (PENDING -> READY -> FULFILLED) ---");
            session.startSession(adminUser);

            preOrderService.markReady(bundleOrderId);
            PreOrder readyOrder = preOrderService.getOrderById(bundleOrderId);
            check(readyOrder.getStatus() == PreOrderStatus.READY, "Admin marked order as READY");

            List<PreOrder> readyList = preOrderService.getReadyOrders();
            check(readyList.stream().anyMatch(o -> o.getOrderId() == bundleOrderId),
                  "Order appears in getReadyOrders()");

            preOrderService.fulfillOrder(bundleOrderId);
            PreOrder fulfilledOrder = preOrderService.getOrderById(bundleOrderId);
            check(fulfilledOrder.getStatus() == PreOrderStatus.FULFILLED, "Admin marked order as FULFILLED");

            // ----------------------------------------------------
            // TEST 4: Product Pre-Order & Staff Self-Cancellation
            // ----------------------------------------------------
            System.out.println("\n--- TEST 4: Product Pre-Order & Staff Cancellation ---");
            session.startSession(staffUser);

            int prodOrderId = preOrderService.createPreOrder(
                "Maria Santos",
                "09189876543",
                null,
                testProduct.getProductId(),
                1,
                testProduct.getPriceCentavos(),
                "From Google Form row 12"
            );
            check(prodOrderId > 0, "Staff created product pre-order (ID: " + prodOrderId + ")");

            PreOrder prodOrder = preOrderService.getOrderById(prodOrderId);
            check(prodOrder.getBundleId() == null && prodOrder.getProductId() != null,
                  "Bundle ID is null and Product ID is set (" + prodOrder.getProductId() + ")");

            preOrderService.cancelOrder(prodOrderId);
            PreOrder cancelledOrder = preOrderService.getOrderById(prodOrderId);
            check(cancelledOrder.getStatus() == PreOrderStatus.CANCELLED,
                  "Staff cancelled their own PENDING pre-order");

            // Verify fulfilled order cannot be cancelled even by Admin
            session.startSession(adminUser);
            boolean blockedCancelFulfilled = false;
            try {
                preOrderService.cancelOrder(bundleOrderId);
            } catch (IllegalStateException ise) {
                blockedCancelFulfilled = true;
            }
            check(blockedCancelFulfilled, "Cannot cancel an already FULFILLED order (IllegalStateException)");

            // ----------------------------------------------------
            // TEST 5: Input Validation
            // ----------------------------------------------------
            System.out.println("\n--- TEST 5: Input Validation ---");
            boolean rejectedBlankName = false;
            try {
                preOrderService.createPreOrder("  ", "", testBundle.getBundleId(), null, 1, 1000, "");
            } catch (IllegalArgumentException iae) {
                rejectedBlankName = true;
            }
            check(rejectedBlankName, "Blank customer name rejected");

            boolean rejectedBothIds = false;
            try {
                preOrderService.createPreOrder("Test", "", testBundle.getBundleId(), testProduct.getProductId(), 1, 1000, "");
            } catch (IllegalArgumentException iae) {
                rejectedBothIds = true;
            }
            check(rejectedBothIds, "Passing both bundleId and productId rejected");

            System.out.println("\n==================================================");
            System.out.println(" ALL PRE-ORDER TESTS PASSED SUCCESSFULLY!");
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
