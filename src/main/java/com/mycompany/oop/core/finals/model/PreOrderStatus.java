package com.mycompany.oop.core.finals.model;

/**
 * Lifecycle statuses for a pre-order.
 *
 * PENDING  – order placed, awaiting admin action
 * READY    – admin has set the order aside / items prepared
 * FULFILLED– order handed over to customer; stock deducted
 * CANCELLED– order cancelled by staff or admin
 */
public enum PreOrderStatus {
    PENDING,
    READY,
    FULFILLED,
    CANCELLED
}
