package com.mycompany.oop.core.finals.service;

import com.mycompany.oop.core.finals.dao.PreOrderDAO;
import com.mycompany.oop.core.finals.model.PreOrder;
import com.mycompany.oop.core.finals.model.PreOrderStatus;
import com.mycompany.oop.core.finals.security.AuthorizationService;
import com.mycompany.oop.core.finals.security.UserSession;

import java.sql.SQLException;
import java.util.List;

/**
 * Business-logic layer for the pre-order module.
 *
 * Access rules:
 *   • All logged-in roles may create pre-orders and view the list.
 *   • Only CHAIRPERSON / BUSINESS_MANAGER may change status
 *     (mark READY, FULFILLED, or CANCELLED).
 *   • The staff member who created an order can cancel their own
 *     PENDING order without needing admin rights.
 */
public class PreOrderService {

    private final PreOrderDAO preOrderDAO;
    private final AuthorizationService auth;
    private final UserSession session;

    public PreOrderService(AuthorizationService auth, UserSession session) {
        this.preOrderDAO = new PreOrderDAO();
        this.auth = auth;
        this.session = session;
    }

    // ── Create ───────────────────────────────────────────────────────────────

    /**
     * Creates a new pre-order.
     * Any logged-in staff member may enter orders.
     *
     * @param customerName  Name of the student/customer
     * @param contactNumber Contact number (can be null/empty)
     * @param bundleId      Null if ordering a standalone product
     * @param productId     Null if ordering a bundle
     * @param quantity      Must be > 0
     * @param totalCentavos Price × qty in centavos (caller computes this)
     * @param notes         Optional notes
     * @return generated order_id
     */
    public int createPreOrder(String customerName,
                              String contactNumber,
                              Integer bundleId,
                              Integer productId,
                              int quantity,
                              long totalCentavos,
                              String notes)
            throws SQLException {

        auth.requireSalesAccess(); // all 3 roles allowed

        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException("Customer name is required.");
        }
        if (bundleId == null && productId == null) {
            throw new IllegalArgumentException(
                "Pre-order must reference either a bundle or a product.");
        }
        if (bundleId != null && productId != null) {
            throw new IllegalArgumentException(
                "Pre-order must reference a bundle OR a product, not both.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        if (totalCentavos < 0) {
            throw new IllegalArgumentException("Total cannot be negative.");
        }

        int userId = session.getCurrentUser().getUserId();

        PreOrder order = new PreOrder(
            customerName.trim(),
            contactNumber == null ? "" : contactNumber.trim(),
            bundleId,
            productId,
            quantity,
            totalCentavos,
            notes == null ? "" : notes.trim(),
            userId
        );

        return preOrderDAO.createOrder(order);
    }

    // ── Status transitions ───────────────────────────────────────────────────

    /**
     * Admin marks the order as READY (items set aside for pick-up).
     */
    public void markReady(int orderId) throws SQLException {
        auth.requireProductManagementAccess(); // admin only
        transitionStatus(orderId, PreOrderStatus.PENDING, PreOrderStatus.READY,
            "Order must be PENDING to mark it READY.");
    }

    /**
     * Admin marks the order as FULFILLED (handed to customer).
     * Note: actual stock deduction should happen through SalesService.recordSale().
     * Call that before or after this depending on your GUI flow.
     */
    public void fulfillOrder(int orderId) throws SQLException {
        auth.requireProductManagementAccess(); // admin only
        // Allow transition from PENDING or READY → FULFILLED
        PreOrder order = getExistingOrder(orderId);
        if (order.getStatus() == PreOrderStatus.CANCELLED ||
            order.getStatus() == PreOrderStatus.FULFILLED) {
            throw new IllegalStateException(
                "Cannot fulfil an order that is already " + order.getStatus() + ".");
        }
        preOrderDAO.updateStatus(orderId, PreOrderStatus.FULFILLED);
    }

    /**
     * Cancels a pre-order.
     * Admin can cancel any PENDING or READY order.
     * The order creator can cancel their own PENDING order.
     */
    public void cancelOrder(int orderId) throws SQLException {
        auth.requireLogin();

        PreOrder order = getExistingOrder(orderId);

        if (order.getStatus() == PreOrderStatus.FULFILLED) {
            throw new IllegalStateException("Cannot cancel a fulfilled order.");
        }
        if (order.getStatus() == PreOrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled.");
        }

        int currentUserId = session.getCurrentUser().getUserId();
        boolean isAdmin = isAdmin();
        boolean isOwner = order.getCreatedBy() == currentUserId;

        if (!isAdmin && !isOwner) {
            throw new SecurityException(
                "You can only cancel orders that you created.");
        }

        // Non-admins can only cancel PENDING orders, not READY ones
        if (!isAdmin && order.getStatus() != PreOrderStatus.PENDING) {
            throw new IllegalStateException(
                "You can only cancel your order while it is PENDING.");
        }

        preOrderDAO.updateStatus(orderId, PreOrderStatus.CANCELLED);
    }

    // ── Queries ──────────────────────────────────────────────────────────────

    public List<PreOrder> getAllOrders() throws SQLException {
        auth.requireSalesAccess();
        return preOrderDAO.getAllOrders();
    }

    public List<PreOrder> getPendingOrders() throws SQLException {
        auth.requireSalesAccess();
        return preOrderDAO.getOrdersByStatus(PreOrderStatus.PENDING);
    }

    public List<PreOrder> getReadyOrders() throws SQLException {
        auth.requireSalesAccess();
        return preOrderDAO.getOrdersByStatus(PreOrderStatus.READY);
    }

    public List<PreOrder> getOrdersByStatus(PreOrderStatus status) throws SQLException {
        auth.requireSalesAccess();
        return preOrderDAO.getOrdersByStatus(status);
    }

    public PreOrder getOrderById(int orderId) throws SQLException {
        auth.requireSalesAccess();
        return getExistingOrder(orderId);
    }

    // ── Internal helpers ─────────────────────────────────────────────────────

    /** Fetches the order and throws if it doesn't exist. */
    private PreOrder getExistingOrder(int orderId) throws SQLException {
        PreOrder order = preOrderDAO.getOrderById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Pre-order ID " + orderId + " not found.");
        }
        return order;
    }

    /** Status transition guard: checks currentStatus before updating. */
    private void transitionStatus(int orderId,
                                  PreOrderStatus expectedCurrent,
                                  PreOrderStatus next,
                                  String errorMsg) throws SQLException {
        PreOrder order = getExistingOrder(orderId);
        if (order.getStatus() != expectedCurrent) {
            throw new IllegalStateException(errorMsg
                + " Current status: " + order.getStatus());
        }
        preOrderDAO.updateStatus(orderId, next);
    }

    private boolean isAdmin() {
        var role = session.getCurrentUser().getRole();
        return switch (role) {
            case CHAIRPERSON, BUSINESS_MANAGER -> true;
            default -> false;
        };
    }
}

