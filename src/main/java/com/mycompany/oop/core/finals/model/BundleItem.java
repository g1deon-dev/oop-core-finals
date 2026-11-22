
package com.mycompany.oop.core.finals.model;

public class BundleItem {

    private int bundleItemId;
    private int bundleId;
    private int productId;
    private String productName;
    private int quantity;

    // Constructor for retrieving an existing bundle item
    public BundleItem(int bundleItemId, int bundleId,
                      int productId, String productName,
                      int quantity) {
        this.bundleItemId = bundleItemId;
        this.bundleId = bundleId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
    }

    // Constructor for adding a product to a new bundle
    public BundleItem(int productId, String productName,
                      int quantity) {
        this(0, 0, productId, productName, quantity);
    }

    public int getBundleItemId() {
        return bundleItemId;
    }

    public int getBundleId() {
        return bundleId;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }
}
