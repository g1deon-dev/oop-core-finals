package com.mycompany.oop.core.finals.service;

import com.mycompany.oop.core.finals.dao.RentalPaymentDAO;
import com.mycompany.oop.core.finals.model.RentalPayment;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class RentalPaymentService {

    private final RentalPaymentDAO paymentDAO;

    public RentalPaymentService() {
        this(new RentalPaymentDAO());
    }

    public RentalPaymentService(RentalPaymentDAO paymentDAO) {
        this.paymentDAO = paymentDAO;
    }

    // ── Database-backed operations ───────────────────────────────────────────

    public List<RentalPayment> getPaymentsByRentalId(int rentalId) throws SQLException {
        return paymentDAO.getPaymentsByRentalId(rentalId);
    }

    public int recordPayment(int rentalId, int amountCentavos, String paymentMethod, boolean paidNow)
            throws SQLException {
        if (rentalId <= 0) {
            throw new IllegalArgumentException("Valid rental ID is required.");
        }
        if (amountCentavos <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero.");
        }
        String method = (paymentMethod == null || paymentMethod.isBlank())
                ? "CASH"
                : paymentMethod.trim().toUpperCase();

        RentalPayment payment = new RentalPayment(
                0,
                rentalId,
                amountCentavos,
                method,
                paidNow ? LocalDateTime.now() : null,
                paidNow ? "PAID" : "PENDING"
        );
        return paymentDAO.createPayment(payment);
    }

    public void markPaymentAsPaid(int paymentId) throws SQLException {
        RentalPayment payment = paymentDAO.getPaymentById(paymentId);
        if (payment == null) {
            throw new IllegalArgumentException("Payment #" + paymentId + " not found.");
        }
        if (!canProcessPayment(payment)) {
            throw new IllegalStateException("Payment cannot be processed (already paid or invalid).");
        }
        paymentDAO.markPaymentAsPaid(paymentId);
    }

    // ── Domain validation helpers ────────────────────────────────────────────

    public boolean isValidPayment(RentalPayment payment) {
        if (payment == null) {
            return false;
        }
        return payment.getAmountCentavos() > 0;
    }

    public void markPaymentAsPaid(RentalPayment payment) {
        if (!isValidPayment(payment)) {
            throw new IllegalArgumentException("Invalid payment.");
        }
        payment.markAsPaid();
    }

    public boolean isPaymentPaid(RentalPayment payment) {
        if (payment == null) {
            return false;
        }
        return "PAID".equalsIgnoreCase(payment.getPaymentStatus());
    }

    public boolean canProcessPayment(RentalPayment payment) {
        if (!isValidPayment(payment)) {
            return false;
        }
        return !isPaymentPaid(payment);
    }
}
