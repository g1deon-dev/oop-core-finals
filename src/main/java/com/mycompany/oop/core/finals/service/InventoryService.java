package com.mycompany.oop.core.finals.service;

import com.mycompany.oop.core.finals.dao.ProductDAO;
import com.mycompany.oop.core.finals.dao.StockChangeRequestDAO;
import com.mycompany.oop.core.finals.dao.StockMovementDAO;
import com.mycompany.oop.core.finals.model.Product;
import com.mycompany.oop.core.finals.model.StockChangeRequest;
import com.mycompany.oop.core.finals.model.StockChangeStatus;
import com.mycompany.oop.core.finals.model.StockMovement;
import com.mycompany.oop.core.finals.security.AuthorizationService;
import com.mycompany.oop.core.finals.security.UserSession;
import com.mycompany.oop.core.finals.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class InventoryService {

    private final StockChangeRequestDAO requestDAO;
    private final StockMovementDAO movementDAO;
    private final ProductDAO productDAO;
    private final AuthorizationService authorizationService;
    private final UserSession session;

    public InventoryService(AuthorizationService authorizationService, UserSession session) {
        if (authorizationService == null || session == null) {
            throw new IllegalArgumentException("Authorization service and user session cannot be null.");
        }
        this.requestDAO = new StockChangeRequestDAO();
        this.movementDAO = new StockMovementDAO();
        this.productDAO = new ProductDAO();
        this.authorizationService = authorizationService;
        this.session = session;
    }

    // Submit a stock change request (Committee Staff or Admin)
    public int submitStockChangeRequest(int productId, int quantity, String reason) throws SQLException {
        authorizationService.requireLogin();

        if (productId <= 0) {
            throw new IllegalArgumentException("Invalid product ID.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Requested quantity must be greater than zero.");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Reason for stock change request is required.");
        }

        Product product = productDAO.getProductById(productId);
        if (product == null || !product.isActive()) {
            throw new IllegalArgumentException("Product does not exist or is inactive.");
        }

        int currentUserId = session.getCurrentUser().getUserId();
        StockChangeRequest request = new StockChangeRequest(productId, quantity, reason.trim(), currentUserId);

        return requestDAO.createRequest(request);
    }

    // Direct restock by Admin, automatically logged in Stock Movements
    public void restockProduct(int productId, int quantityToAdd) throws SQLException {
        authorizationService.requireProductManagementAccess();

        if (productId <= 0) {
            throw new IllegalArgumentException("Invalid product ID.");
        }
        if (quantityToAdd <= 0) {
            throw new IllegalArgumentException("Quantity to add must be greater than zero.");
        }

        int currentUserId = session.getCurrentUser().getUserId();

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                productDAO.restockProduct(productId, quantityToAdd);
                movementDAO.recordMovement(conn, new StockMovement(
                    productId,
                    quantityToAdd,
                    "RESTOCK",
                    null,
                    currentUserId
                ));
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    // Approve a request (Admin only)
    public void approveStockChangeRequest(int requestId, String reviewNotes) throws SQLException {
        authorizationService.requireProductManagementAccess();

        StockChangeRequest request = requestDAO.getRequestById(requestId);
        if (request == null) {
            throw new IllegalArgumentException("Request not found: " + requestId);
        }
        if (request.getStatus() != StockChangeStatus.PENDING) {
            throw new IllegalStateException("Only PENDING requests can be approved. Current status: " + request.getStatus());
        }

        int currentUserId = session.getCurrentUser().getUserId();
        String notes = (reviewNotes != null && !reviewNotes.isBlank()) ? reviewNotes.trim() : "Approved";

        requestDAO.updateStatus(requestId, StockChangeStatus.APPROVED, currentUserId, notes);
    }

    // Reject a request (Admin only)
    public void rejectStockChangeRequest(int requestId, String reviewNotes) throws SQLException {
        authorizationService.requireProductManagementAccess();

        StockChangeRequest request = requestDAO.getRequestById(requestId);
        if (request == null) {
            throw new IllegalArgumentException("Request not found: " + requestId);
        }
        if (request.getStatus() != StockChangeStatus.PENDING) {
            throw new IllegalStateException("Only PENDING requests can be rejected. Current status: " + request.getStatus());
        }

        int currentUserId = session.getCurrentUser().getUserId();
        String notes = (reviewNotes != null && !reviewNotes.isBlank()) ? reviewNotes.trim() : "Rejected";

        requestDAO.updateStatus(requestId, StockChangeStatus.REJECTED, currentUserId, notes);
    }

    // Mark physical shipment received and automatically increase inventory stock (Staff or Admin)
    public void receiveStockChangeRequest(int requestId) throws SQLException {
        authorizationService.requireLogin();

        StockChangeRequest request = requestDAO.getRequestById(requestId);
        if (request == null) {
            throw new IllegalArgumentException("Request not found: " + requestId);
        }
        if (request.getStatus() != StockChangeStatus.APPROVED) {
            throw new IllegalStateException("Only APPROVED requests can be received into inventory. Current status: " + request.getStatus());
        }

        int currentUserId = session.getCurrentUser().getUserId();

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Update product quantity
                productDAO.restockProduct(request.getProductId(), request.getRequestedQuantity());

                // Record stock movement
                StockMovement movement = new StockMovement(
                    request.getProductId(),
                    request.getRequestedQuantity(),
                    "REQUEST_FULFILLED",
                    (long) requestId,
                    currentUserId
                );
                movementDAO.recordMovement(conn, movement);

                // Update request status to RECEIVED
                requestDAO.updateStatus(requestId, StockChangeStatus.RECEIVED, currentUserId, "Received into inventory");

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    // Cancel a pending request by the requester
    public void cancelStockChangeRequest(int requestId) throws SQLException {
        authorizationService.requireLogin();
        int currentUserId = session.getCurrentUser().getUserId();
        requestDAO.cancelRequest(requestId, currentUserId);
    }

    // Get all requests
    public List<StockChangeRequest> getAllRequests() throws SQLException {
        authorizationService.requireLogin();
        return requestDAO.getAllRequests();
    }

    // Get pending requests (useful for Admin approval dashboard)
    public List<StockChangeRequest> getPendingRequests() throws SQLException {
        authorizationService.requireLogin();
        return requestDAO.getRequestsByStatus(StockChangeStatus.PENDING);
    }

    // Get audit movements for a product
    public List<StockMovement> getMovementsByProductId(int productId) throws SQLException {
        authorizationService.requireProductViewAccess();
        return movementDAO.getMovementsByProductId(productId);
    }

    // Get all movements
    public List<StockMovement> getAllMovements() throws SQLException {
        authorizationService.requireProductViewAccess();
        return movementDAO.getAllMovements();
    }

    // Dashboard metric: Total active products count
    public int getTotalProductCount() throws SQLException {
        authorizationService.requireProductViewAccess();
        return productDAO.getAllProducts().size();
    }

    // Dashboard metric: Total inventory units across active products
    public int getTotalStockUnits() throws SQLException {
        authorizationService.requireProductViewAccess();
        return productDAO.getAllProducts().stream()
                .mapToInt(Product::getQuantity)
                .sum();
    }

    // Dashboard metric: Total count of low-stock products
    public int getLowStockCount() throws SQLException {
        authorizationService.requireProductViewAccess();
        List<Product> products = productDAO.getAllProducts();
        int count = 0;
        for (Product p : products) {
            if (p.getQuantity() <= p.getLowStockThreshold()) {
                count++;
            }
        }
        return count;
    }
}
