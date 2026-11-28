package com.mycompany.oop.core.finals.dao;

import com.mycompany.oop.core.finals.model.Bundle;
import com.mycompany.oop.core.finals.model.BundleItem;
import com.mycompany.oop.core.finals.model.Product;
import com.mycompany.oop.core.finals.model.Sale;
import com.mycompany.oop.core.finals.model.SaleItem;
import com.mycompany.oop.core.finals.model.StockMovement;
import com.mycompany.oop.core.finals.util.CsvStorage;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SaleDAO {

    private static final String SALES_FILE = "sales.csv";
    private static final String[] SALES_HEADERS = {
        "sale_id", "processed_by", "total_centavos", "sale_date"
    };

    private static final String SALE_ITEMS_FILE = "sale_items.csv";
    private static final String[] SALE_ITEMS_HEADERS = {
        "sale_item_id", "sale_id", "product_id", "bundle_id",
        "item_name_snapshot", "price_centavos_snapshot", "quantity", "subtotal_centavos"
    };

    private static final String COMP_FILE = "sale_bundle_components.csv";
    private static final String[] COMP_HEADERS = {
        "component_id", "sale_item_id", "product_id",
        "component_name_snapshot", "quantity_per_bundle", "total_deducted_quantity"
    };

    private final ProductDAO productDAO = new ProductDAO();
    private final BundleDAO bundleDAO = new BundleDAO();
    private final StockMovementDAO movementDAO = new StockMovementDAO();

    private record VerifiedComponent(int productId, String name, int perBundle, int totalDeducted) {}
    private record VerifiedLine(int productId, int bundleId, String name, long price, int qty, long subtotal, List<VerifiedComponent> components) {}

    public long createSale(Sale sale) throws SQLException {
        if (sale == null || sale.getItems().isEmpty()) {
            throw new SQLException("A sale must contain at least one item.");
        }
        if (sale.getProcessedBy() <= 0) {
            throw new SQLException("Invalid staff account.");
        }

        List<SaleItem> requestedItems = sale.getItems();
        Map<Integer, Bundle> bundles = new HashMap<>();
        Map<Integer, Long> requiredStock = new TreeMap<>();

        for (SaleItem item : requestedItems) {
            if (item == null || item.getQuantity() <= 0) {
                throw new SQLException("Invalid sale item.");
            }
            boolean validProduct = item.getProductId() > 0 && item.getBundleId() == 0;
            boolean validBundle = item.getBundleId() > 0 && item.getProductId() == 0;
            if (!validProduct && !validBundle) {
                throw new SQLException("A sale item must represent exactly one product or bundle.");
            }
            if (validProduct) {
                requiredStock.put(item.getProductId(),
                    Math.addExact(requiredStock.getOrDefault(item.getProductId(), 0L), item.getQuantity()));
            } else {
                Bundle b = bundles.get(item.getBundleId());
                if (b == null) {
                    b = bundleDAO.getBundleById(item.getBundleId());
                    if (b == null) {
                        throw new SQLException("Bundle not found: " + item.getBundleId());
                    }
                    if (!b.isActive()) {
                        throw new SQLException("Bundle " + b.getBundleName() + " is inactive.");
                    }
                    if (b.getItems().isEmpty()) {
                        throw new SQLException("Bundle " + b.getBundleId() + " has no components.");
                    }
                    bundles.put(b.getBundleId(), b);
                }
                for (BundleItem bi : b.getItems()) {
                    long compQty = Math.multiplyExact((long) bi.getQuantity(), item.getQuantity());
                    requiredStock.put(bi.getProductId(),
                        Math.addExact(requiredStock.getOrDefault(bi.getProductId(), 0L), compQty));
                }
            }
        }

        Map<Integer, Product> products = new HashMap<>();
        for (Map.Entry<Integer, Long> entry : requiredStock.entrySet()) {
            int pid = entry.getKey();
            long req = entry.getValue();
            if (req > Integer.MAX_VALUE) {
                throw new SQLException("Required stock quantity is too large.");
            }
            Product p = productDAO.getProductById(pid);
            if (p == null) {
                throw new SQLException("Product not found: " + pid);
            }
            if (!p.isActive()) {
                throw new SQLException(p.getProductName() + " is inactive.");
            }
            if (req > p.getQuantity()) {
                throw new SQLException("Insufficient stock for " + p.getProductName()
                    + ". Required: " + req + ", Available: " + p.getQuantity());
            }
            products.put(pid, p);
        }

        List<VerifiedLine> verified = new ArrayList<>();
        long totalCentavos = 0;
        for (SaleItem item : requestedItems) {
            if (!item.isBundle()) {
                Product p = products.get(item.getProductId());
                long sub = Math.multiplyExact((long) p.getPriceCentavos(), item.getQuantity());
                totalCentavos = Math.addExact(totalCentavos, sub);
                verified.add(new VerifiedLine(p.getProductId(), 0, p.getProductName(), p.getPriceCentavos(), item.getQuantity(), sub, List.of()));
            } else {
                Bundle b = bundles.get(item.getBundleId());
                List<VerifiedComponent> comps = new ArrayList<>();
                for (BundleItem bi : b.getItems()) {
                    Product p = products.get(bi.getProductId());
                    int totalDeducted = Math.toIntExact(Math.multiplyExact((long) bi.getQuantity(), item.getQuantity()));
                    comps.add(new VerifiedComponent(p.getProductId(), p.getProductName(), bi.getQuantity(), totalDeducted));
                }
                long sub = Math.multiplyExact(b.getPriceCentavos(), item.getQuantity());
                totalCentavos = Math.addExact(totalCentavos, sub);
                verified.add(new VerifiedLine(0, b.getBundleId(), b.getBundleName(), b.getPriceCentavos(), item.getQuantity(), sub, comps));
            }
        }

        // Write sale header
        List<String[]> saleRows = CsvStorage.readAllRows(SALES_FILE, SALES_HEADERS);
        long saleId = CsvStorage.nextLongId(saleRows, 0);
        saleRows.add(new String[]{
            String.valueOf(saleId),
            String.valueOf(sale.getProcessedBy()),
            String.valueOf(totalCentavos),
            LocalDateTime.now().toString()
        });
        CsvStorage.writeAllRows(SALES_FILE, SALES_HEADERS, saleRows);

        // Write sale items & components
        List<String[]> itemRows = CsvStorage.readAllRows(SALE_ITEMS_FILE, SALE_ITEMS_HEADERS);
        List<String[]> compRows = CsvStorage.readAllRows(COMP_FILE, COMP_HEADERS);
        long nextItemId = CsvStorage.nextLongId(itemRows, 0);
        long nextCompId = CsvStorage.nextLongId(compRows, 0);

        for (VerifiedLine line : verified) {
            long saleItemId = nextItemId++;
            itemRows.add(new String[]{
                String.valueOf(saleItemId),
                String.valueOf(saleId),
                line.bundleId() > 0 ? "" : String.valueOf(line.productId()),
                line.bundleId() > 0 ? String.valueOf(line.bundleId()) : "",
                line.name(),
                String.valueOf(line.price()),
                String.valueOf(line.qty()),
                String.valueOf(line.subtotal())
            });
            for (VerifiedComponent vc : line.components()) {
                compRows.add(new String[]{
                    String.valueOf(nextCompId++),
                    String.valueOf(saleItemId),
                    String.valueOf(vc.productId()),
                    vc.name(),
                    String.valueOf(vc.perBundle()),
                    String.valueOf(vc.totalDeducted())
                });
            }
        }
        CsvStorage.writeAllRows(SALE_ITEMS_FILE, SALE_ITEMS_HEADERS, itemRows);
        CsvStorage.writeAllRows(COMP_FILE, COMP_HEADERS, compRows);

        // Deduct stock and record audit movement
        for (Map.Entry<Integer, Long> entry : requiredStock.entrySet()) {
            int qty = Math.toIntExact(entry.getValue());
            int pid = entry.getKey();
            productDAO.adjustStock(null, pid, -qty);
            movementDAO.recordMovement(new StockMovement(pid, -qty, "SALE", saleId, sale.getProcessedBy()));
        }

        return saleId;
    }

    public Sale getSaleById(long saleId) throws SQLException {
        for (Sale s : getAllSales()) {
            if (s.getSaleId() == saleId) {
                return s;
            }
        }
        return null;
    }

    public List<Sale> getAllSales() throws SQLException {
        List<String[]> saleRows = CsvStorage.readAllRows(SALES_FILE, SALES_HEADERS);
        List<String[]> itemRows = CsvStorage.readAllRows(SALE_ITEMS_FILE, SALE_ITEMS_HEADERS);

        Map<Long, List<SaleItem>> itemsBySale = new HashMap<>();
        for (String[] ir : itemRows) {
            long saleId = Long.parseLong(ir[1]);
            int productId = ir[2].isEmpty() ? 0 : Integer.parseInt(ir[2]);
            int bundleId = ir[3].isEmpty() ? 0 : Integer.parseInt(ir[3]);
            itemsBySale.computeIfAbsent(saleId, k -> new ArrayList<>()).add(new SaleItem(
                Long.parseLong(ir[0]),
                saleId,
                productId,
                bundleId,
                ir[4],
                Long.parseLong(ir[5]),
                Integer.parseInt(ir[6]),
                Long.parseLong(ir[7])
            ));
        }

        List<Sale> sales = new ArrayList<>();
        for (String[] sr : saleRows) {
            long id = Long.parseLong(sr[0]);
            sales.add(new Sale(
                id,
                Integer.parseInt(sr[1]),
                Long.parseLong(sr[2]),
                CsvStorage.parseNullableDateTime(sr[3]),
                itemsBySale.getOrDefault(id, List.of())
            ));
        }
        Collections.reverse(sales);
        return sales;
    }
}
