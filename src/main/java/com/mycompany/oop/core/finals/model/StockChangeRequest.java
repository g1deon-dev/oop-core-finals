package com.mycompany.oop.core.finals.model;

import java.time.LocalDateTime;

public class StockChangeRequest {

    private int requestId;
    private int productId;
    private int requestedQuantity;
    private String reason;
    private StockChangeStatus status;
    private int requestedBy;
    private Integer reviewedBy;
    private String reviewNotes;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;

    public StockChangeRequest(int requestId, int productId, int requestedQuantity,
                              String reason, StockChangeStatus status,
                              int requestedBy, Integer reviewedBy, String reviewNotes,
                              LocalDateTime createdAt, LocalDateTime reviewedAt) {
        this.requestId = requestId;
        this.productId = productId;
        this.requestedQuantity = requestedQuantity;
        this.reason = reason;
        this.status = status;
        this.requestedBy = requestedBy;
        this.reviewedBy = reviewedBy;
        this.reviewNotes = reviewNotes;
        this.createdAt = createdAt;
        this.reviewedAt = reviewedAt;
    }

    // Constructor for a new request before DB insertion
    public StockChangeRequest(int productId, int requestedQuantity, String reason, int requestedBy) {
        this(0, productId, requestedQuantity, reason, StockChangeStatus.PENDING,
             requestedBy, null, null, null, null);
    }

    public int getRequestId() {
        return requestId;
    }

    public int getProductId() {
        return productId;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public String getReason() {
        return reason;
    }

    public StockChangeStatus getStatus() {
        return status;
    }

    public int getRequestedBy() {
        return requestedBy;
    }

    public Integer getReviewedBy() {
        return reviewedBy;
    }

    public String getReviewNotes() {
        return reviewNotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }
}
