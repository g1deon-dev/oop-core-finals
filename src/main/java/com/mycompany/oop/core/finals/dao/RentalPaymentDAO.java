package com.mycompany.oop.core.finals.dao;

import com.mycompany.oop.core.finals.model.RentalPayment;
import com.mycompany.oop.core.finals.util.CsvStorage;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RentalPaymentDAO {

    private static final String FILE = "rental_payments.csv";
    private static final String[] HEADERS = {
        "payment_id", "rental_id", "amount_centavos", "payment_method", "payment_date", "payment_status"
    };

    public int createPayment(Connection conn, RentalPayment payment) throws SQLException {
        List<String[]> rows = CsvStorage.readAllRows(FILE, HEADERS);
        int id = CsvStorage.nextIntId(rows, 0);
        payment.setPaymentId(id);
        rows.add(new String[]{
            String.valueOf(id),
            String.valueOf(payment.getRentalId()),
            String.valueOf(payment.getAmountCentavos()),
            CsvStorage.str(payment.getPaymentMethod()),
            CsvStorage.str(payment.getPaymentDate()),
            CsvStorage.str(payment.getPaymentStatus())
        });
        CsvStorage.writeAllRows(FILE, HEADERS, rows);
        return id;
    }

    public int createPayment(RentalPayment payment) throws SQLException {
        return createPayment(null, payment);
    }

    public boolean markPaymentAsPaid(int paymentId) throws SQLException {
        List<String[]> rows = CsvStorage.readAllRows(FILE, HEADERS);
        boolean found = false;
        for (String[] r : rows) {
            if (Integer.parseInt(r[0]) == paymentId) {
                r[5] = "PAID";
                r[4] = LocalDateTime.now().toString();
                found = true;
                break;
            }
        }
        if (found) {
            CsvStorage.writeAllRows(FILE, HEADERS, rows);
        }
        return found;
    }

    public List<RentalPayment> getPaymentsByRentalId(int rentalId) throws SQLException {
        List<RentalPayment> list = new ArrayList<>();
        for (String[] r : CsvStorage.readAllRows(FILE, HEADERS)) {
            if (Integer.parseInt(r[1]) == rentalId) {
                list.add(mapRow(r));
            }
        }
        Collections.reverse(list);
        return list;
    }

    public RentalPayment getPaymentById(int paymentId) throws SQLException {
        for (String[] r : CsvStorage.readAllRows(FILE, HEADERS)) {
            if (Integer.parseInt(r[0]) == paymentId) {
                return mapRow(r);
            }
        }
        return null;
    }

    private RentalPayment mapRow(String[] r) {
        return new RentalPayment(
            Integer.parseInt(r[0]),
            Integer.parseInt(r[1]),
            Integer.parseInt(r[2]),
            r[3],
            CsvStorage.parseNullableDateTime(r[4]),
            r[5]
        );
    }
}
