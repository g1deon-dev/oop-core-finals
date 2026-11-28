package com.mycompany.oop.core.finals.dao;

import com.mycompany.oop.core.finals.model.Rental;
import com.mycompany.oop.core.finals.model.RentalStatus;
import com.mycompany.oop.core.finals.util.CsvStorage;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RentalDAO {

    private static final String FILE = "rentals.csv";
    private static final String[] HEADERS = {
        "rental_id", "customer_name", "contact_number", "venue",
        "rental_start", "expected_return", "actual_return", "status", "created_at"
    };

    public int createRental(Connection conn, Rental rental) throws SQLException {
        List<String[]> rows = CsvStorage.readAllRows(FILE, HEADERS);
        int id = CsvStorage.nextIntId(rows, 0);
        rental.setRentalId(id);
        rows.add(new String[]{
            String.valueOf(id),
            CsvStorage.str(rental.getCustomerName()),
            CsvStorage.str(rental.getContactNumber()),
            CsvStorage.str(rental.getVenue()),
            CsvStorage.str(rental.getRentalStart()),
            CsvStorage.str(rental.getExpectedReturn()),
            CsvStorage.str(rental.getActualReturn()),
            rental.getStatus().name(),
            LocalDateTime.now().toString()
        });
        CsvStorage.writeAllRows(FILE, HEADERS, rows);
        return id;
    }

    public int createRental(Rental rental) throws SQLException {
        return createRental(null, rental);
    }

    public boolean updateStatus(Connection conn, int rentalId, RentalStatus newStatus) throws SQLException {
        List<String[]> rows = CsvStorage.readAllRows(FILE, HEADERS);
        boolean found = false;
        for (String[] r : rows) {
            if (Integer.parseInt(r[0]) == rentalId) {
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

    public boolean updateStatus(int rentalId, RentalStatus newStatus) throws SQLException {
        return updateStatus(null, rentalId, newStatus);
    }

    public boolean markReturned(Connection conn, int rentalId, LocalDateTime actualReturn) throws SQLException {
        List<String[]> rows = CsvStorage.readAllRows(FILE, HEADERS);
        boolean found = false;
        for (String[] r : rows) {
            if (Integer.parseInt(r[0]) == rentalId) {
                r[7] = RentalStatus.RETURNED.name();
                r[6] = actualReturn != null ? actualReturn.toString() : LocalDateTime.now().toString();
                found = true;
                break;
            }
        }
        if (found) {
            CsvStorage.writeAllRows(FILE, HEADERS, rows);
        }
        return found;
    }

    public boolean markReturned(int rentalId, LocalDateTime actualReturn) throws SQLException {
        return markReturned(null, rentalId, actualReturn);
    }

    public Rental getRentalById(int rentalId) throws SQLException {
        for (Rental r : getAllRentals()) {
            if (r.getRentalId() == rentalId) {
                return r;
            }
        }
        return null;
    }

    public List<Rental> getAllRentals() throws SQLException {
        List<Rental> list = new ArrayList<>();
        for (String[] r : CsvStorage.readAllRows(FILE, HEADERS)) {
            Rental rental = new Rental(
                Integer.parseInt(r[0]),
                r[1],
                r[2],
                r[3],
                CsvStorage.parseNullableDateTime(r[4]),
                CsvStorage.parseNullableDateTime(r[5])
            );
            rental.setActualReturn(CsvStorage.parseNullableDateTime(r[6]));
            rental.setStatus(RentalStatus.valueOf(r[7]));
            list.add(rental);
        }
        Collections.reverse(list);
        return list;
    }

    public List<Rental> getRentalsByStatus(RentalStatus status) throws SQLException {
        List<Rental> list = new ArrayList<>();
        for (Rental r : getAllRentals()) {
            if (r.getStatus() == status) {
                list.add(r);
            }
        }
        return list;
    }

    public List<Rental> searchRentals(String keyword) throws SQLException {
        String lower = keyword == null ? "" : keyword.trim().toLowerCase();
        List<Rental> list = new ArrayList<>();
        for (Rental r : getAllRentals()) {
            if ((r.getCustomerName() != null && r.getCustomerName().toLowerCase().contains(lower))
                    || (r.getVenue() != null && r.getVenue().toLowerCase().contains(lower))
                    || (r.getContactNumber() != null && r.getContactNumber().toLowerCase().contains(lower))) {
                list.add(r);
            }
        }
        return list;
    }
}
