package com.mycompany.oop.core.finals.service;

import com.mycompany.oop.core.finals.dao.RentalDAO;
import com.mycompany.oop.core.finals.dao.RentalItemDAO;
import com.mycompany.oop.core.finals.dao.RentalPaymentDAO;
import com.mycompany.oop.core.finals.model.Rental;
import com.mycompany.oop.core.finals.model.RentalItem;
import com.mycompany.oop.core.finals.model.RentalPayment;
import com.mycompany.oop.core.finals.model.RentalStatus;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class RentalService {

    private final RentalDAO rentalDAO;
    private final RentalItemDAO rentalItemDAO;
    private final RentalPaymentDAO rentalPaymentDAO;

    public RentalService() {
        this(new RentalDAO(), new RentalItemDAO(), new RentalPaymentDAO());
    }

    public RentalService(RentalDAO rentalDAO, RentalItemDAO rentalItemDAO, RentalPaymentDAO rentalPaymentDAO) {
        this.rentalDAO = rentalDAO;
        this.rentalItemDAO = rentalItemDAO;
        this.rentalPaymentDAO = rentalPaymentDAO;
    }

    // ── Database-backed operations ───────────────────────────────────────────

    public int createRental(
            String customerName,
            String contactNumber,
            String venue,
            LocalDateTime rentalStart,
            LocalDateTime expectedReturn,
            List<RentalItem> items,
            String paymentMethod,
            boolean paidNow
    ) throws SQLException {
        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException("Customer name is required.");
        }
        if (rentalStart == null || expectedReturn == null) {
            throw new IllegalArgumentException("Rental start and expected return dates are required.");
        }
        if (!expectedReturn.isAfter(rentalStart)) {
            throw new IllegalArgumentException("Expected return must be after rental start.");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("At least one rental item is required.");
        }

        int totalCentavos = 0;
        for (RentalItem item : items) {
            if (item == null || item.getProductName() == null || item.getProductName().isBlank()) {
                throw new IllegalArgumentException("All rental items must have a valid product name.");
            }
            if (item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Item quantity must be greater than zero.");
            }
            if (item.getRentalPriceCentavos() < 0) {
                throw new IllegalArgumentException("Item price cannot be negative.");
            }
            totalCentavos += item.getTotalPriceCentavos();
        }

        Rental rental = new Rental(
                0,
                customerName.trim(),
                contactNumber == null ? "" : contactNumber.trim(),
                venue == null ? "" : venue.trim(),
                rentalStart,
                expectedReturn
        );

        int rentalId = rentalDAO.createRental(null, rental);

        for (RentalItem item : items) {
            item.setRentalId(rentalId);
            rentalItemDAO.addRentalItem(null, item);
        }

        if (totalCentavos > 0) {
            String method = (paymentMethod == null || paymentMethod.isBlank())
                    ? "CASH"
                    : paymentMethod.trim().toUpperCase();
            RentalPayment payment = new RentalPayment(
                    0,
                    rentalId,
                    totalCentavos,
                    method,
                    paidNow ? LocalDateTime.now() : null,
                    paidNow ? "PAID" : "PENDING"
            );
            rentalPaymentDAO.createPayment(null, payment);
        }

        return rentalId;
    }

    public List<Rental> getAllRentals() throws SQLException {
        return rentalDAO.getAllRentals();
    }

    public List<Rental> getRentalsByStatus(RentalStatus status) throws SQLException {
        return rentalDAO.getRentalsByStatus(status);
    }

    public List<Rental> searchRentals(String keyword) throws SQLException {
        return rentalDAO.searchRentals(keyword);
    }

    public Rental getRentalById(int rentalId) throws SQLException {
        return rentalDAO.getRentalById(rentalId);
    }

    public List<RentalItem> getRentalItems(int rentalId) throws SQLException {
        return rentalItemDAO.getItemsByRentalId(rentalId);
    }

    public void startRental(int rentalId) throws SQLException {
        Rental rental = requireExistingRental(rentalId);
        startRental(rental);
        rentalDAO.updateStatus(rentalId, RentalStatus.ONGOING);
    }

    public void returnRental(int rentalId) throws SQLException {
        Rental rental = requireExistingRental(rentalId);
        returnRental(rental);
        rentalDAO.markReturned(rentalId, rental.getActualReturn());
    }

    public void cancelRental(int rentalId) throws SQLException {
        Rental rental = requireExistingRental(rentalId);
        cancelRental(rental);
        rentalDAO.updateStatus(rentalId, RentalStatus.CANCELLED);
    }

    private Rental requireExistingRental(int rentalId) throws SQLException {
        Rental rental = rentalDAO.getRentalById(rentalId);
        if (rental == null) {
            throw new IllegalArgumentException("Rental #" + rentalId + " not found.");
        }
        return rental;
    }

    // ── Domain state-transition helpers ──────────────────────────────────────

    public void startRental(Rental rental) {
        if (rental == null) {
            throw new IllegalArgumentException("Rental cannot be null.");
        }
        if (rental.getStatus() != RentalStatus.RESERVED) {
            throw new IllegalStateException("Only reserved rentals can be started.");
        }
        rental.startRental();
    }

    public void returnRental(Rental rental) {
        if (rental == null) {
            throw new IllegalArgumentException("Rental cannot be null.");
        }
        if (rental.getStatus() != RentalStatus.ONGOING) {
            throw new IllegalStateException("Only ongoing rentals can be returned.");
        }
        rental.returnRental(LocalDateTime.now());
    }

    public void cancelRental(Rental rental) {
        if (rental == null) {
            throw new IllegalArgumentException("Rental cannot be null.");
        }
        if (rental.getStatus() != RentalStatus.RESERVED) {
            throw new IllegalStateException("Only reserved rentals can be cancelled.");
        }
        rental.setStatus(RentalStatus.CANCELLED);
    }

    public boolean isOverdue(Rental rental) {
        if (rental == null) {
            throw new IllegalArgumentException("Rental cannot be null.");
        }
        if (rental.getStatus() != RentalStatus.ONGOING || rental.getExpectedReturn() == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(rental.getExpectedReturn());
    }
}
