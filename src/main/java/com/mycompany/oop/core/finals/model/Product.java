/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oop.core.finals.model;

/**
 *
 * @author g1deon-dev
 */
public class Product {
    private int productId;
    private String productName;
    private int priceCentavos;
    private int quantity;
    private int lowStockThreshold;
    
    public Product(int productId, String productName, int priceCentavos, int quantity, int lowStockThreshold) {
        this.productId = productId;
        this.productName = productName;
        this.priceCentavos = priceCentavos;
        this.quantity = quantity;
        this.lowStockThreshold = lowStockThreshold;   
    }
    
    public int getProductId() {
        return productId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public int getPriceCentavos() {
        return priceCentavos;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public int getLowStockThreshold() {
        return lowStockThreshold;
    }
    
}  
