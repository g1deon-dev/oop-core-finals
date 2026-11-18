package com.mycompany.oop.core.finals.service;

import com.mycompany.oop.core.finals.model.Rental;
import com.mycompany.oop.core.finals.model.RentalStatus;
import java.time.LocalDateTime;

public class RentalService {
    public void startRental(Rental rental) {

    if (rental == null) {
        throw new IllegalArgumentException("Rental cannot be null.");
    }

    if (rental.getStatus() != RentalStatus.RESERVED) {
        throw new IllegalStateException(
            "Only reserved rentals can be started."
        );
    }

    rental.startRental();
}
    public void returnRental(Rental rental) {

    if (rental == null) {
        throw new IllegalArgumentException("Rental cannot be null.");
    }

    if (rental.getStatus() != RentalStatus.ONGOING) {
        throw new IllegalStateException(
            "Only ongoing rentals can be returned."
        );
    }

    rental.returnRental(LocalDateTime.now());
}
    public void cancelRental(Rental rental) {

    if (rental == null) {
        throw new IllegalArgumentException("Rental cannot be null.");
    }

    if (rental.getStatus() != RentalStatus.RESERVED) {
        throw new IllegalStateException(
            "Only reserved rentals can be cancelled."
        );
    }

    rental.setStatus(RentalStatus.CANCELLED);
}
    public boolean isOverdue(Rental rental) {

    if (rental == null) {
        throw new IllegalArgumentException("Rental cannot be null.");
    }

    if (rental.getStatus() != RentalStatus.ONGOING) {
        return false;
    }

    return LocalDateTime.now().isAfter(rental.getExpectedReturn());
}
}