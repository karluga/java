package com.may.informatic.services;

import com.may.informatic.entities.Reservation;
import com.may.informatic.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;

    public Page<Reservation> fetchReservations(int page, int pageSize) {
        return reservationRepository.findAll(PageRequest.of(page, pageSize));
    }

    public Page<Reservation> filterReservations(String searchQuery, int page, int pageSize) {
        return reservationRepository.findByRoomNameOrCustomerNameContaining(searchQuery, PageRequest.of(page, pageSize));
    }

    public Optional<Reservation> findById(int reservationId) {
        return reservationRepository.findById(reservationId);
    }
}