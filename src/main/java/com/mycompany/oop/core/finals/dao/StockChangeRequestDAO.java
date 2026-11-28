package com.mycompany.oop.core.finals.dao;

import com.mycompany.oop.core.finals.model.StockChangeRequest;
import com.mycompany.oop.core.finals.model.StockChangeStatus;
import com.mycompany.oop.core.finals.util.CsvStorage;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StockChangeRequestDAO {

    private static final String FILE = "stock_change_requests.csv";
    private static final String[] HEADERS = {
        "request_id", "product_id", "requested_quantity", "reason", "status",
        "requested_by", "reviewed_by", "review_notes", "created_at", "reviewed_at"
    };

    public int createRequest(StockChangeRequest request) throws SQLException {
        List<String[]> rows = CsvStorage.readAllRows(FILE, HEADERS);
        int id = CsvStorage.nextIntId(rows, 0);
        LocalDateTime now = LocalDateTime.now();
        rows.add(new String[]{
            String.valueOf(id),
            String.valueOf(request.getProductId()),
            String.valueOf(request.getRequestedQuantity()),
            CsvStorage.str(request.getReason()),
            request.getStatus().name(),
            String.valueOf(request.getRequestedBy()),
            "",
            "",
            now.toString(),
            ""
        });
        CsvStorage.writeAllRows(FILE, HEADERS, rows);
        return id;
    }

    public StockChangeRequest getRequestById(int requestId) throws SQLException {
        for (StockChangeRequest r : getAllRequests()) {
            if (r.getRequestId() == requestId) {
                return r;
            }
        }
        return null;
    }

    public List<StockChangeRequest> getAllRequests() throws SQLException {
        List<StockChangeRequest> requests = new ArrayList<>();
        for (String[] r : CsvStorage.readAllRows(FILE, HEADERS)) {
            requests.add(new StockChangeRequest(
                Integer.parseInt(r[0]),
                Integer.parseInt(r[1]),
                Integer.parseInt(r[2]),
                r[3],
                StockChangeStatus.valueOf(r[4]),
                Integer.parseInt(r[5]),
                CsvStorage.parseNullableInt(r[6]),
                r[7].isEmpty() ? null : r[7],
                CsvStorage.parseNullableDateTime(r[8]),
                CsvStorage.parseNullableDateTime(r[9])
            ));
        }
        Collections.reverse(requests);
        return requests;
    }

    public List<StockChangeRequest> getRequestsByStatus(StockChangeStatus status) throws SQLException {
        List<StockChangeRequest> requests = new ArrayList<>();
        for (StockChangeRequest r : getAllRequests()) {
            if (r.getStatus() == status) {
                requests.add(r);
            }
        }
        return requests;
    }

    public void updateStatus(int requestId, StockChangeStatus newStatus, int reviewedBy, String reviewNotes) throws SQLException {
        List<String[]> rows = CsvStorage.readAllRows(FILE, HEADERS);
        boolean found = false;
        for (String[] r : rows) {
            if (Integer.parseInt(r[0]) == requestId) {
                r[4] = newStatus.name();
                r[6] = String.valueOf(reviewedBy);
                r[7] = CsvStorage.str(reviewNotes);
                r[9] = LocalDateTime.now().toString();
                found = true;
                break;
            }
        }
        if (!found) {
            throw new SQLException("Stock change request not found: " + requestId);
        }
        CsvStorage.writeAllRows(FILE, HEADERS, rows);
    }

    public void cancelRequest(int requestId, int userId) throws SQLException {
        List<String[]> rows = CsvStorage.readAllRows(FILE, HEADERS);
        boolean updated = false;
        for (String[] r : rows) {
            if (Integer.parseInt(r[0]) == requestId
                    && Integer.parseInt(r[5]) == userId
                    && "PENDING".equals(r[4])) {
                r[4] = StockChangeStatus.CANCELLED.name();
                updated = true;
                break;
            }
        }
        if (!updated) {
            throw new SQLException("Request could not be cancelled. It may not exist, belong to another user, or is no longer PENDING.");
        }
        CsvStorage.writeAllRows(FILE, HEADERS, rows);
    }
}
