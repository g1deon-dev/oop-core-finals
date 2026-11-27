
package com.mycompany.oop.core.finals.model;

import java.util.ArrayList;
import java.util.List;

public class Bundle {

    private int bundleId;
    private String bundleName;
    private long priceCentavos;
    private boolean active;
    private List<BundleItem> items;

    // Constructor for retrieving an existing bundle
    public Bundle(int bundleId, String bundleName,
                  long priceCentavos, boolean active,
                  List<BundleItem> items) {
        this.bundleId = bundleId;
        this.bundleName = bundleName;
        this.priceCentavos = priceCentavos;
        this.active = active;
        this.items = new ArrayList<>(items);
    }

    // Constructor for creating a new bundle
    public Bundle(String bundleName, long priceCentavos,
                  List<BundleItem> items) {
        this(0, bundleName, priceCentavos, true, items);
    }

    public int getBundleId() {
        return bundleId;
    }

    public String getBundleName() {
        return bundleName;
    }

    public long getPriceCentavos() {
        return priceCentavos;
    }

    public boolean isActive() {
        return active;
    }

    public List<BundleItem> getItems() {
        return new ArrayList<>(items);
    }
}
