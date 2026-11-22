package com.mycompany.oop.core.finals.model;

public class SaleItem {

    private long saleItemId;
    private long saleId;
    private int productId;
    private int bundleId;
    private String productName;
    private long unitPriceCentavos;
    private int quantity;
    private long subtotalCentavos;

    // Constructor for retrieving an existing sale item from the database
    public SaleItem(long saleItemId, long saleId, int productId,
                    String productName, long unitPriceCentavos,
                    int quantity, long subtotalCentavos) {
        this(saleItemId, saleId, productId, 0, productName,
             unitPriceCentavos, quantity, subtotalCentavos);
    }

    // Constructor supporting both product and bundle sale items
    public SaleItem(long saleItemId, long saleId,
                    int productId, int bundleId,
                    String productName, long unitPriceCentavos,
                    int quantity, long subtotalCentavos) {
        this.saleItemId = saleItemId;
        this.saleId = saleId;
        this.productId = productId;
        this.bundleId = bundleId;
        this.productName = productName;
        this.unitPriceCentavos = unitPriceCentavos;
        this.quantity = quantity;
        this.subtotalCentavos = subtotalCentavos;
    }

    // Constructor for a new individual product sale item
    public SaleItem(int productId, String productName,
                    long unitPriceCentavos, int quantity,
                    long subtotalCentavos) {
        this(0, 0, productId, 0, productName,
             unitPriceCentavos, quantity, subtotalCentavos);
    }

    // Factory method for creating a new bundle sale item
    public static SaleItem forBundle(int bundleId, String bundleName,
                                      long unitPriceCentavos, int quantity,
                                      long subtotalCentavos) {
        return new SaleItem(0, 0, 0, bundleId, bundleName,
                            unitPriceCentavos, quantity, subtotalCentavos);
    }

    public long getSaleItemId() {
        return saleItemId;
    }

    public long getSaleId() {
        return saleId;
    }

    public int getProductId() {
        return productId;
    }

    public int getBundleId() {
        return bundleId;
    }

    public boolean isBundle() {
        return bundleId > 0;
    }

    public String getProductName() {
        return productName;
    }

    public long getUnitPriceCentavos() {
        return unitPriceCentavos;
    }

    public int getQuantity() {
        return quantity;
    }

    public long getSubtotalCentavos() {
        return subtotalCentavos;
    }
}
