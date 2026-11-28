package com.mycompany.oop.core.finals.dao;

import com.mycompany.oop.core.finals.model.PreOrder;
import com.mycompany.oop.core.finals.model.PreOrderStatus;
import com.mycompany.oop.core.finals.util.CsvStorage;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PreOrderDAO {

    private static final String FILE = "pre_orders.csv";
    private static final String[] HEADERS = {
        "order_id", "customer_name", "contact_number", "bundle_id", "product_id",
        "quantity", "total_centavos", "status", "notes", "created_by", "created_at"
    };

    public int createOrder(Connection conn, PreOrder order) throws SQLException {
        List<String[]> rows = CsvStorage.readAllRows(FILE, HEADERS);
        int id = CsvStorage.nextIntId(rows, 0);
        LocalDateTime now = order.getCreatedAt() != null ? order.getCreatedAt() : LocalDateTime.now();
        rows.add(new String[]{
            String.valueOf(id),
            CsvStorage.str(order.getCustomerName()),
            CsvStorage.str(order.getContactNumber()),
            CsvStorage.str(order.getBundleId()),
            CsvStorage.str(order.getProductId()),
            String.valueOf(order.getQuantity()),
            String.valueOf(order.getTotalCentavos()),
            order.getStatus().name(),
            CsvStorage.str(order.getNotes()),
            String.valueOf(order.getCreatedBy()),
            now.toString()
        });
        CsvStorage.writeAllRows(FILE, HEADERS, rows);
        return id;
    }

    public int createOrder(PreOrder order) throws SQLException {
        return createOrder(null, order);
    }

    public boolean updateStatus(Connection conn, int orderId, PreOrderStatus newStatus) throws SQLException {
        List<String[]> rows = CsvStorage.readAllRows(FILE, HEADERS);
        boolean found = false;
        for (String[] r : rows) {
            if (Integer.parseInt(r[0]) == orderId) {
                r[7] = newStatus.name();
                found = true;
                break;
            }
        }
        if (found) {
            CsvStorage.writeAllRows(FILE, HEADERS, rows);
        }
        return found;
    }

    public boolean updateStatus(int orderId, PreOrderStatus newStatus) throws SQLException {
        return updateStatus(null, orderId, newStatus);
    }

    public PreOrder getOrderById(int orderId) throws SQLException {
        for (PreOrder o : getAllOrders()) {
            if (o.getOrderId() == orderId) {
                return o;
            }
        }
        return null;
    }

    public List<PreOrder> getAllOrders() throws SQLException {
        List<PreOrder> result = new ArrayList<>();
        for (String[] r : CsvStorage.readAllRows(FILE, HEADERS)) {
            result.add(new PreOrder(
                Integer.parseInt(r[0]),
                r[1],
                r[2],
                CsvStorage.parseNullableInt(r[3]),
                CsvStorage.parseNullableInt(r[4]),
                Integer.parseInt(r[5]),
                Long.parseLong(r[6]),
                PreOrderStatus.valueOf(r[7]),
                r[8],
                Integer.parseInt(r[9]),
                CsvStorage.parseNullableDateTime(r[10])
            ));
        }
        Collections.reverse(result);
        return result;
    }

    public List<PreOrder> getOrdersByStatus(PreOrderStatus status) throws SQLException {
        List<PreOrder> result = new ArrayList<>();
        for (PreOrder o : getAllOrders()) {
            if (o.getStatus() == status) {
                result.add(o);
            }
        }
        return result;
    }
}
