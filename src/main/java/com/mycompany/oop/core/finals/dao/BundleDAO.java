package com.mycompany.oop.core.finals.dao;

import com.mycompany.oop.core.finals.model.Bundle;
import com.mycompany.oop.core.finals.model.BundleItem;
import com.mycompany.oop.core.finals.model.Product;
import com.mycompany.oop.core.finals.util.CsvStorage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BundleDAO {

    private static final String BUNDLES_FILE = "bundles.csv";
    private static final String[] BUNDLES_HEADERS = {
        "bundle_id", "bundle_name", "price_centavos", "is_active"
    };

    private static final String ITEMS_FILE = "bundle_items.csv";
    private static final String[] ITEMS_HEADERS = {
        "bundle_id", "product_id", "quantity"
    };

    private final ProductDAO productDAO = new ProductDAO();

    public void addBundle(Bundle bundle) throws SQLException {
        validateBundle(bundle);
        validateProducts(bundle.getItems());

        List<String[]> bundleRows = CsvStorage.readAllRows(BUNDLES_FILE, BUNDLES_HEADERS);
        int bundleId = CsvStorage.nextIntId(bundleRows, 0);
        bundleRows.add(new String[]{
            String.valueOf(bundleId),
            bundle.getBundleName().trim(),
            String.valueOf(bundle.getPriceCentavos()),
            "true"
        });
        CsvStorage.writeAllRows(BUNDLES_FILE, BUNDLES_HEADERS, bundleRows);

        List<String[]> itemRows = CsvStorage.readAllRows(ITEMS_FILE, ITEMS_HEADERS);
        for (BundleItem item : bundle.getItems()) {
            itemRows.add(new String[]{
                String.valueOf(bundleId),
                String.valueOf(item.getProductId()),
                String.valueOf(item.getQuantity())
            });
        }
        CsvStorage.writeAllRows(ITEMS_FILE, ITEMS_HEADERS, itemRows);
    }

    public List<Bundle> getAllBundles() throws SQLException {
        List<Bundle> bundles = new ArrayList<>();
        for (String[] r : CsvStorage.readAllRows(BUNDLES_FILE, BUNDLES_HEADERS)) {
            if (Boolean.parseBoolean(r[3])) {
                bundles.add(buildBundle(r));
            }
        }
        bundles.sort(Comparator.comparing(Bundle::getBundleName, String.CASE_INSENSITIVE_ORDER));
        return bundles;
    }

    public Bundle getBundleById(int bundleId) throws SQLException {
        for (String[] r : CsvStorage.readAllRows(BUNDLES_FILE, BUNDLES_HEADERS)) {
            if (Integer.parseInt(r[0]) == bundleId) {
                return buildBundle(r);
            }
        }
        return null;
    }

    public void updateBundle(Bundle bundle) throws SQLException {
        validateBundle(bundle);
        validateProducts(bundle.getItems());

        List<String[]> bundleRows = CsvStorage.readAllRows(BUNDLES_FILE, BUNDLES_HEADERS);
        boolean found = false;
        for (String[] r : bundleRows) {
            if (Integer.parseInt(r[0]) == bundle.getBundleId()) {
                r[1] = bundle.getBundleName().trim();
                r[2] = String.valueOf(bundle.getPriceCentavos());
                found = true;
                break;
            }
        }
        if (!found) {
            throw new SQLException("Bundle not found.");
        }
        CsvStorage.writeAllRows(BUNDLES_FILE, BUNDLES_HEADERS, bundleRows);

        List<String[]> itemRows = CsvStorage.readAllRows(ITEMS_FILE, ITEMS_HEADERS);
        itemRows.removeIf(r -> Integer.parseInt(r[0]) == bundle.getBundleId());
        for (BundleItem item : bundle.getItems()) {
            itemRows.add(new String[]{
                String.valueOf(bundle.getBundleId()),
                String.valueOf(item.getProductId()),
                String.valueOf(item.getQuantity())
            });
        }
        CsvStorage.writeAllRows(ITEMS_FILE, ITEMS_HEADERS, itemRows);
    }

    public void deactivateBundle(int bundleId) throws SQLException {
        updateBundleStatus(bundleId, false);
    }

    public void reactivateBundle(int bundleId) throws SQLException {
        updateBundleStatus(bundleId, true);
    }

    private void updateBundleStatus(int bundleId, boolean active) throws SQLException {
        List<String[]> rows = CsvStorage.readAllRows(BUNDLES_FILE, BUNDLES_HEADERS);
        boolean found = false;
        for (String[] r : rows) {
            if (Integer.parseInt(r[0]) == bundleId) {
                r[3] = String.valueOf(active);
                found = true;
                break;
            }
        }
        if (!found) {
            throw new SQLException("Bundle not found.");
        }
        CsvStorage.writeAllRows(BUNDLES_FILE, BUNDLES_HEADERS, rows);
    }

    public boolean bundleNameExists(String bundleName) throws SQLException {
        String target = bundleName.trim().toLowerCase();
        for (String[] r : CsvStorage.readAllRows(BUNDLES_FILE, BUNDLES_HEADERS)) {
            if (r[1].trim().toLowerCase().equals(target)) {
                return true;
            }
        }
        return false;
    }

    private Bundle buildBundle(String[] r) throws SQLException {
        int bundleId = Integer.parseInt(r[0]);
        List<BundleItem> items = new ArrayList<>();
        for (String[] ir : CsvStorage.readAllRows(ITEMS_FILE, ITEMS_HEADERS)) {
            if (Integer.parseInt(ir[0]) == bundleId) {
                int productId = Integer.parseInt(ir[1]);
                Product p = productDAO.getProductById(productId);
                String name = p != null ? p.getProductName() : ("Product #" + productId);
                items.add(new BundleItem(0, bundleId, productId, name, Integer.parseInt(ir[2])));
            }
        }
        items.sort(Comparator.comparingInt(BundleItem::getProductId));
        return new Bundle(
            bundleId,
            r[1],
            Long.parseLong(r[2]),
            Boolean.parseBoolean(r[3]),
            items
        );
    }

    private void validateProducts(List<BundleItem> items) throws SQLException {
        for (BundleItem item : items) {
            Product p = productDAO.getProductById(item.getProductId());
            if (p == null) {
                throw new SQLException("Product not found: " + item.getProductId());
            }
            if (!p.isActive()) {
                throw new SQLException("Cannot add inactive product: " + p.getProductName());
            }
        }
    }

    private void validateBundle(Bundle bundle) {
        if (bundle == null) {
            throw new IllegalArgumentException("Bundle cannot be null.");
        }
        if (bundle.getBundleName() == null || bundle.getBundleName().trim().isEmpty()) {
            throw new IllegalArgumentException("Bundle name cannot be empty.");
        }
        if (bundle.getPriceCentavos() < 0) {
            throw new IllegalArgumentException("Bundle price cannot be negative.");
        }
        if (bundle.getItems() == null || bundle.getItems().isEmpty()) {
            throw new IllegalArgumentException("A bundle must contain at least one product.");
        }
        Set<Integer> productIds = new HashSet<>();
        for (BundleItem item : bundle.getItems()) {
            if (item == null || item.getProductId() <= 0) {
                throw new IllegalArgumentException("Invalid product in bundle.");
            }
            if (item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Component quantity must be greater than zero.");
            }
            if (!productIds.add(item.getProductId())) {
                throw new IllegalArgumentException("A product cannot appear twice in the same bundle.");
            }
        }
    }
}
