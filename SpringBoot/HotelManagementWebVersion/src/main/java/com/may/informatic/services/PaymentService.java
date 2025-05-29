package com.may.informatic.services;

import com.may.informatic.entities.Payment;
import com.may.informatic.entities.Reservation;
import com.may.informatic.repositories.PaymentRepository;
import com.may.informatic.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PaymentRepository paymentRepository;

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

        // Save the payment record
        Payment payment = new Payment();
        payment.setReservationId(reservation.getId());
        payment.setAmountPaid(paymentAmount);
        paymentRepository.save(payment);

        return true;
    }
    public double getPaidAmount(int reservationId) {
        return paymentRepository.findByReservationId(reservationId)
                                .stream()
                                .mapToDouble(payment -> payment.getAmountPaid())
                                .sum();
    }
}