package com.mycompany.oop.core.finals.model;

import java.time.LocalDateTime;

public class Rental {

    private int rentalId;
    private String customerName;
    private String contactNumber;
    private String venue;

    private LocalDateTime rentalStart;
    private LocalDateTime expectedReturn;
    private LocalDateTime actualReturn;

    private RentalStatus status;

    public Rental(int rentalId, String customerName, String contactNumber,
              String venue, LocalDateTime rentalStart,
              LocalDateTime expectedReturn) {

    this.rentalId = rentalId;
    this.customerName = customerName;
    this.contactNumber = contactNumber;
    this.venue = venue;
    this.rentalStart = rentalStart;
    this.expectedReturn = expectedReturn;
    this.actualReturn = null;
    this.status = RentalStatus.RESERVED;
}

    public int getRentalId() {
        return rentalId;
    }

    public void setRentalId(int rentalId) {
        this.rentalId = rentalId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public LocalDateTime getRentalStart() {
        return rentalStart;
    }

    public void setRentalStart(LocalDateTime rentalStart) {
        this.rentalStart = rentalStart;
    }

    public LocalDateTime getExpectedReturn() {
        return expectedReturn;
    }

    public void setExpectedReturn(LocalDateTime expectedReturn) {
        this.expectedReturn = expectedReturn;
    }

    public LocalDateTime getActualReturn() {
        return actualReturn;
    }

    public void setActualReturn(LocalDateTime actualReturn) {
        this.actualReturn = actualReturn;
    }

    public RentalStatus getStatus() {
        return status;
    }

    public void setStatus(RentalStatus status) {
        this.status = status;
    }
    
    public void startRental() {
    this.status = RentalStatus.ONGOING;
    }

    public void returnRental(LocalDateTime actualReturn) {
    this.actualReturn = actualReturn;
    this.status = RentalStatus.RETURNED;
}
}