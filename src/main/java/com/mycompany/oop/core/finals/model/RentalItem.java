package com.mycompany.oop.core.finals.model;

public class RentalItem {

    private int rentalItemId;
    private int rentalId;
    private int productId;
    private String productName;
    private int quantity;
    private int rentalPriceCentavos;

    public RentalItem(int rentalItemId, int rentalId, int productId,
                  String productName, int quantity,
                  int rentalPriceCentavos) {

    this.rentalItemId = rentalItemId;
    this.rentalId = rentalId;
    this.productId = productId;
    this.productName = productName;
    this.quantity = quantity;
    this.rentalPriceCentavos = rentalPriceCentavos;
}

    public int getRentalItemId() {
        return rentalItemId;
    }

    public void setRentalItemId(int rentalItemId) {
        this.rentalItemId = rentalItemId;
    }

    public int getRentalId() {
        return rentalId;
    }

    public void setRentalId(int rentalId) {
        this.rentalId = rentalId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getRentalPriceCentavos() {
        return rentalPriceCentavos;
    }

    public void setRentalPriceCentavos(int rentalPriceCentavos) {
        this.rentalPriceCentavos = rentalPriceCentavos;
    }
    
    public int getTotalPriceCentavos() {
    return rentalPriceCentavos * quantity;
}
}