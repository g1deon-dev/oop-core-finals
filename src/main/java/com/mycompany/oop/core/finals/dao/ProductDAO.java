package com.mycompany.oop.core.finals.dao;

import com.mycompany.oop.core.finals.model.Product;
import com.mycompany.oop.core.finals.model.ProductSortField;
import com.mycompany.oop.core.finals.model.SortDirection;
import com.mycompany.oop.core.finals.model.StockStatus;
import com.mycompany.oop.core.finals.util.CsvStorage;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ProductDAO {

    private static final String FILE = "products.csv";
    private static final String[] HEADERS = {
        "product_id", "product_name", "price_centavos", "quantity", "low_stock_threshold", "is_active"
    };

    public void addProduct(Product product) throws SQLException {
        addProductAndReturnId(product);
    }

    public int addProductAndReturnId(Product product) throws SQLException {
        List<String[]> rows = CsvStorage.readAllRows(FILE, HEADERS);
        int id = CsvStorage.nextIntId(rows, 0);
        rows.add(new String[]{
            String.valueOf(id),
            product.getProductName(),
            String.valueOf(product.getPriceCentavos()),
            String.valueOf(product.getQuantity()),
            String.valueOf(product.getLowStockThreshold()),
            "true"
        });
        CsvStorage.writeAllRows(FILE, HEADERS, rows);
        return id;
    }

    public void adjustStock(Connection conn, int productId, int delta) throws SQLException {
        List<String[]> rows = CsvStorage.readAllRows(FILE, HEADERS);
        boolean updated = false;
        for (String[] r : rows) {
            if (Integer.parseInt(r[0]) == productId && Boolean.parseBoolean(r[5])) {
                int current = Integer.parseInt(r[3]);
                if (current + delta < 0) {
                    throw new SQLException("Product not found, inactive, or insufficient stock for deduction.");
                }
                r[3] = String.valueOf(current + delta);
                updated = true;
                break;
            }
        }
        if (!updated) {
            throw new SQLException("Product not found, inactive, or insufficient stock for deduction.");
        }
        CsvStorage.writeAllRows(FILE, HEADERS, rows);
    }

    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        for (String[] r : CsvStorage.readAllRows(FILE, HEADERS)) {
            Product p = mapRow(r);
            if (p.isActive()) {
                products.add(p);
            }
        }
        products.sort(Comparator.comparingInt(Product::getProductId));
        return products;
    }

    public List<Product> searchProducts(String keyword) throws SQLException {
        String lower = keyword.toLowerCase();
        List<Product> products = new ArrayList<>();
        for (Product p : getAllProducts()) {
            if (p.getProductName().toLowerCase().contains(lower)) {
                products.add(p);
            }
        }
        products.sort(Comparator.comparing(Product::getProductName, String.CASE_INSENSITIVE_ORDER));
        return products;
    }

    public Product getProductById(int productId) throws SQLException {
        for (String[] r : CsvStorage.readAllRows(FILE, HEADERS)) {
            if (Integer.parseInt(r[0]) == productId) {
                return mapRow(r);
            }
        }
        return null;
    }

    public void restockProduct(int productId, int quantityToAdd) throws SQLException {
        adjustStock(null, productId, quantityToAdd);
    }

    public void updateProduct(Product product) throws SQLException {
        List<String[]> rows = CsvStorage.readAllRows(FILE, HEADERS);
        boolean found = false;
        for (String[] r : rows) {
            if (Integer.parseInt(r[0]) == product.getProductId() && Boolean.parseBoolean(r[5])) {
                r[1] = product.getProductName();
                r[2] = String.valueOf(product.getPriceCentavos());
                r[4] = String.valueOf(product.getLowStockThreshold());
                found = true;
                break;
            }
        }
        if (!found) {
            throw new SQLException("Product not found or inactive.");
        }
        CsvStorage.writeAllRows(FILE, HEADERS, rows);
    }

    public void deactivateProduct(int productId) throws SQLException {
        setProductActive(productId, false);
    }

    public List<Product> getInactiveProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        for (String[] r : CsvStorage.readAllRows(FILE, HEADERS)) {
            Product p = mapRow(r);
            if (!p.isActive()) {
                products.add(p);
            }
        }
        products.sort(Comparator.comparingInt(Product::getProductId));
        return products;
    }

    public void reactivateProduct(int productId) throws SQLException {
        setProductActive(productId, true);
    }

    public boolean productNameExists(String productName) throws SQLException {
        String target = productName.trim().toLowerCase();
        for (String[] r : CsvStorage.readAllRows(FILE, HEADERS)) {
            if (r[1].trim().toLowerCase().equals(target)) {
                return true;
            }
        }
        return false;
    }

    public boolean productNameExistsForOtherProduct(String productName, int productId) throws SQLException {
        String target = productName.trim().toLowerCase();
        for (String[] r : CsvStorage.readAllRows(FILE, HEADERS)) {
            if (Integer.parseInt(r[0]) != productId && r[1].trim().toLowerCase().equals(target)) {
                return true;
            }
        }
        return false;
    }

    public List<Product> getSortedProducts(ProductSortField sortField, SortDirection sortDirection) throws SQLException {
        List<Product> products = getAllProducts();
        products.sort(buildComparator(sortField, sortDirection));
        return products;
    }

    public List<Product> getProductsByStockStatusSorted(
            StockStatus status, ProductSortField sortField, SortDirection sortDirection) throws SQLException {
        List<Product> filtered = new ArrayList<>();
        for (Product p : getAllProducts()) {
            if (matchesStatus(p, status)) {
                filtered.add(p);
            }
        }
        filtered.sort(buildComparator(sortField, sortDirection));
        return filtered;
    }

    public List<Product> searchFilterAndSortProducts(
            String keyword, StockStatus status, ProductSortField sortField, SortDirection sortDirection) throws SQLException {
        String lower = keyword.toLowerCase();
        List<Product> filtered = new ArrayList<>();
        for (Product p : getAllProducts()) {
            if (p.getProductName().toLowerCase().contains(lower) && matchesStatus(p, status)) {
                filtered.add(p);
            }
        }
        filtered.sort(buildComparator(sortField, sortDirection));
        return filtered;
    }

    private void setProductActive(int productId, boolean active) throws SQLException {
        List<String[]> rows = CsvStorage.readAllRows(FILE, HEADERS);
        boolean found = false;
        for (String[] r : rows) {
            if (Integer.parseInt(r[0]) == productId) {
                r[5] = String.valueOf(active);
                found = true;
                break;
            }
        }
        if (!found) {
            throw new SQLException("Product not found.");
        }
        CsvStorage.writeAllRows(FILE, HEADERS, rows);
    }

    private boolean matchesStatus(Product p, StockStatus status) {
        int q = p.getQuantity();
        int t = p.getLowStockThreshold();
        return switch (status) {
            case IN_STOCK -> q > t;
            case LOW_STOCK -> q > 0 && q <= t;
            case OUT_OF_STOCK -> q == 0;
        };
    }

    private Comparator<Product> buildComparator(ProductSortField sortField, SortDirection sortDirection) {
        Comparator<Product> cmp = switch (sortField) {
            case ID -> Comparator.comparingInt(Product::getProductId);
            case NAME -> Comparator.comparing(Product::getProductName, String.CASE_INSENSITIVE_ORDER);
            case PRICE -> Comparator.comparingInt(Product::getPriceCentavos);
            case QUANTITY -> Comparator.comparingInt(Product::getQuantity);
        };
        return sortDirection == SortDirection.DESCENDING ? cmp.reversed() : cmp;
    }

    private Product mapRow(String[] r) {
        return new Product(
            Integer.parseInt(r[0]),
            r[1],
            Integer.parseInt(r[2]),
            Integer.parseInt(r[3]),
            Integer.parseInt(r[4]),
            Boolean.parseBoolean(r[5])
        );
    }
}
