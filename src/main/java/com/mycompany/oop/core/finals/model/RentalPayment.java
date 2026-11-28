package com.mycompany.oop.core.finals.model;

import java.time.LocalDateTime;

public class RentalPayment {

    private int paymentId;
    private int rentalId;
    private int amountCentavos;
    private String paymentMethod;
    private LocalDateTime paymentDate;
    private String paymentStatus;

    public RentalPayment(int paymentId, int rentalId, int amountCentavos,
                     String paymentMethod, LocalDateTime paymentDate,
                     String paymentStatus) {

    this.paymentId = paymentId;
    this.rentalId = rentalId;
    this.amountCentavos = amountCentavos;
    this.paymentMethod = paymentMethod;
    this.paymentDate = paymentDate;
    this.paymentStatus = paymentStatus;
}

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getRentalId() {
        return rentalId;
    }

    public void setRentalId(int rentalId) {
        this.rentalId = rentalId;
    }

    public int getAmountCentavos() {
        return amountCentavos;
    }

    public void setAmountCentavos(int amountCentavos) {
        this.amountCentavos = amountCentavos;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public void markAsPaid() {
    this.paymentStatus = "PAID";
}
}