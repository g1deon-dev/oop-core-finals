package com.mycompany.oop.core.finals.dao;

import com.mycompany.oop.core.finals.model.StockMovement;
import com.mycompany.oop.core.finals.util.CsvStorage;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StockMovementDAO {

    private static final String FILE = "stock_movements.csv";
    private static final String[] HEADERS = {
        "movement_id", "product_id", "change_amount", "reason", "reference_id", "created_by", "created_at"
    };

    public void recordMovement(Connection conn, StockMovement movement) throws SQLException {
        List<String[]> rows = CsvStorage.readAllRows(FILE, HEADERS);
        int id = CsvStorage.nextIntId(rows, 0);
        LocalDateTime ts = movement.getCreatedAt() != null ? movement.getCreatedAt() : LocalDateTime.now();
        rows.add(new String[]{
            String.valueOf(id),
            String.valueOf(movement.getProductId()),
            String.valueOf(movement.getChangeAmount()),
            CsvStorage.str(movement.getReason()),
            CsvStorage.str(movement.getReferenceId()),
            String.valueOf(movement.getCreatedBy()),
            ts.toString()
        });
        CsvStorage.writeAllRows(FILE, HEADERS, rows);
    }

    public void recordMovement(StockMovement movement) throws SQLException {
        recordMovement(null, movement);
    }

    public List<StockMovement> getMovementsByProductId(int productId) throws SQLException {
        List<StockMovement> movements = new ArrayList<>();
        for (StockMovement m : getAllMovements()) {
            if (m.getProductId() == productId) {
                movements.add(m);
            }
        }
        return movements;
    }

    public List<StockMovement> getAllMovements() throws SQLException {
        List<String[]> rows = CsvStorage.readAllRows(FILE, HEADERS);
        List<StockMovement> movements = new ArrayList<>();
        for (String[] r : rows) {
            movements.add(new StockMovement(
                Integer.parseInt(r[0]),
                Integer.parseInt(r[1]),
                Integer.parseInt(r[2]),
                r[3],
                CsvStorage.parseNullableLong(r[4]),
                Integer.parseInt(r[5]),
                CsvStorage.parseNullableDateTime(r[6])
            ));
        }
        Collections.reverse(movements);
        return movements;
    }
}
