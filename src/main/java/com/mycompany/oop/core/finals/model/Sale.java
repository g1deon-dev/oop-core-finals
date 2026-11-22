
package com.mycompany.oop.core.finals.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Sale {

    private long saleId;
    private int processedBy;
    private long totalCentavos;
    private LocalDateTime saleDate;
    private List<SaleItem> items;

    public Sale(long saleId, int processedBy,
                long totalCentavos, LocalDateTime saleDate,
                List<SaleItem> items) {
        this.saleId = saleId;
        this.processedBy = processedBy;
        this.totalCentavos = totalCentavos;
        this.saleDate = saleDate;
        this.items = new ArrayList<>(items);
    }

    // Constructor for a new sale before database insertion
    public Sale(int processedBy, List<SaleItem> items) {
        this(0, processedBy, 0, null, items);
    }

    public long getSaleId() {
        return saleId;
    }

    public int getProcessedBy() {
        return processedBy;
    }

    public long getTotalCentavos() {
        return totalCentavos;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public List<SaleItem> getItems() {
        return new ArrayList<>(items);
    }
}
