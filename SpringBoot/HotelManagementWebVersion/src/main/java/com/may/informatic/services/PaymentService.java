package com.may.informatic.services;

import com.may.informatic.entities.Reservation;
import com.may.informatic.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    @Autowired
    private ReservationRepository reservationRepository;

    public boolean markAsPaid(Reservation reservation, boolean fullyPaid) {
        reservation.setIsPaid(fullyPaid);
        reservationRepository.save(reservation);
        return true;
    }

    public boolean handlePartialPayment(Reservation reservation, double paymentAmount) {
        double currentPaid = reservation.getPaidAmount() + paymentAmount;
        reservation.setPaidAmount(currentPaid);
        if (currentPaid >= reservation.getTotalPrice()) {
            reservation.setIsPaid(true);
        }
        reservationRepository.save(reservation);
        return true;
    }
}