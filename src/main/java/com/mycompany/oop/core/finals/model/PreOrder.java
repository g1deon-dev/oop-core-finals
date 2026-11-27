package com.mycompany.oop.core.finals.model;

import java.time.LocalDateTime;

/**
 * Represents a customer pre-order managed internally by staff/admin.
 *
 * Fields mirror the Google Sheets columns used by the org:
 *   Order ID | Customer Name | Bundle | Quantity | Total | Status
 *
 * Additional fields (contact_number, notes, created_by, created_at)
 * support internal tracking and audit purposes.
 */
public class PreOrder {

    private int orderId;
    private String customerName;
    private String contactNumber;   // e.g. "09xxxxxxxxx"
    private Integer bundleId;       // null when ordering a standalone product
    private Integer productId;      // null when ordering a bundle
    private int quantity;
    private long totalCentavos;     // price × quantity, stored as centavos (avoid floating point)
    private PreOrderStatus status;
    private String notes;
    private int createdBy;          // user_id of the staff who entered the order
    private LocalDateTime createdAt;

    // ── Constructor for retrieving an existing record from the DB ────────────
    public PreOrder(int orderId,
                    String customerName,
                    String contactNumber,
                    Integer bundleId,
                    Integer productId,
                    int quantity,
                    long totalCentavos,
                    PreOrderStatus status,
                    String notes,
                    int createdBy,
                    LocalDateTime createdAt) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.contactNumber = contactNumber;
        this.bundleId = bundleId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalCentavos = totalCentavos;
        this.status = status;
        this.notes = notes;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    // ── Constructor for creating a new pre-order (ID and timestamp from DB) ──
    public PreOrder(String customerName,
                    String contactNumber,
                    Integer bundleId,
                    Integer productId,
                    int quantity,
                    long totalCentavos,
                    String notes,
                    int createdBy) {
        this(0, customerName, contactNumber, bundleId, productId,
             quantity, totalCentavos, PreOrderStatus.PENDING, notes,
             createdBy, null);
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int getOrderId()           { return orderId; }
    public String getCustomerName()   { return customerName; }
    public String getContactNumber()  { return contactNumber; }
    public Integer getBundleId()      { return bundleId; }
    public Integer getProductId()     { return productId; }
    public int getQuantity()          { return quantity; }
    public long getTotalCentavos()    { return totalCentavos; }
    public PreOrderStatus getStatus() { return status; }
    public String getNotes()          { return notes; }
    public int getCreatedBy()         { return createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // ── Convenience helper ───────────────────────────────────────────────────

    /** Returns the total formatted as "₱X.XX" for display. */
    public String getFormattedTotal() {
        return String.format("₱%,.2f", totalCentavos / 100.0);
    }

    @Override
    public String toString() {
        return "PreOrder{orderId=" + orderId
            + ", customer='" + customerName + "'"
            + ", qty=" + quantity
            + ", total=" + getFormattedTotal()
            + ", status=" + status + "}";
    }
}

