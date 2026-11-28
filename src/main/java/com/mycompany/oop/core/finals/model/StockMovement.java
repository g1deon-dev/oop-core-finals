package com.mycompany.oop.core.finals.model;

import java.time.LocalDateTime;

public class StockMovement {

    private int movementId;
    private int productId;
    private int changeAmount;
    private String reason;
    private Long referenceId;
    private int createdBy;
    private LocalDateTime createdAt;

    public StockMovement(int movementId, int productId, int changeAmount,
                         String reason, Long referenceId, int createdBy,
                         LocalDateTime createdAt) {
        this.movementId = movementId;
        this.productId = productId;
        this.changeAmount = changeAmount;
        this.reason = reason;
        this.referenceId = referenceId;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    // Constructor for creating a new movement before DB insertion
    public StockMovement(int productId, int changeAmount, String reason,
                         Long referenceId, int createdBy) {
        this(0, productId, changeAmount, reason, referenceId, createdBy, null);
    }

    public int getMovementId() {
        return movementId;
    }

    public int getProductId() {
        return productId;
    }

    public int getChangeAmount() {
        return changeAmount;
    }

    public String getReason() {
        return reason;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
