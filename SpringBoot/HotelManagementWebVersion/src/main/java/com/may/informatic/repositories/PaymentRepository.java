package com.may.informatic.repositories;

import com.may.informatic.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByReservationId(int reservationId);
}
