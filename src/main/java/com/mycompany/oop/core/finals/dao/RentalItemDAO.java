package com.mycompany.oop.core.finals.dao;

import com.mycompany.oop.core.finals.model.RentalItem;
import com.mycompany.oop.core.finals.util.CsvStorage;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RentalItemDAO {

    private static final String FILE = "rental_items.csv";
    private static final String[] HEADERS = {
        "rental_item_id", "rental_id", "product_id", "product_name", "quantity", "rental_price_centavos"
    };

    public int addRentalItem(Connection conn, RentalItem item) throws SQLException {
        List<String[]> rows = CsvStorage.readAllRows(FILE, HEADERS);
        int id = CsvStorage.nextIntId(rows, 0);
        item.setRentalItemId(id);
        rows.add(new String[]{
            String.valueOf(id),
            String.valueOf(item.getRentalId()),
            item.getProductId() > 0 ? String.valueOf(item.getProductId()) : "",
            CsvStorage.str(item.getProductName()),
            String.valueOf(item.getQuantity()),
            String.valueOf(item.getRentalPriceCentavos())
        });
        CsvStorage.writeAllRows(FILE, HEADERS, rows);
        return id;
    }

    public int addRentalItem(RentalItem item) throws SQLException {
        return addRentalItem(null, item);
    }

    public List<RentalItem> getItemsByRentalId(int rentalId) throws SQLException {
        List<RentalItem> items = new ArrayList<>();
        for (String[] r : CsvStorage.readAllRows(FILE, HEADERS)) {
            if (Integer.parseInt(r[1]) == rentalId) {
                int productId = r[2].isEmpty() ? 0 : Integer.parseInt(r[2]);
                items.add(new RentalItem(
                    Integer.parseInt(r[0]),
                    rentalId,
                    productId,
                    r[3],
                    Integer.parseInt(r[4]),
                    Integer.parseInt(r[5])
                ));
            }
        }
        return items;
    }

    public boolean deleteItemsByRentalId(Connection conn, int rentalId) throws SQLException {
        List<String[]> rows = CsvStorage.readAllRows(FILE, HEADERS);
        boolean removed = rows.removeIf(r -> Integer.parseInt(r[1]) == rentalId);
        if (removed) {
            CsvStorage.writeAllRows(FILE, HEADERS, rows);
        }
        return removed;
    }
}
