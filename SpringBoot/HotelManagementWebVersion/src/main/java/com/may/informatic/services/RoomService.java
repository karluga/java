package com.may.informatic.services;

import com.may.informatic.entities.Room;
import com.may.informatic.entities.Reservation;
import com.may.informatic.repositories.RoomRepository;
import com.may.informatic.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    public Page<Room> fetchRooms(int userId, String searchQuery, int page, int pageSize) {
        return roomRepository.findByNameContaining(searchQuery, PageRequest.of(page, pageSize));
    }

    public int getActiveBookingsCount(int userId) {
        return reservationRepository.findByUserIdAndEndDateAfter(userId, LocalDate.now()).size();
    }

    public int getTotalBookingsCount(int userId) {
        return reservationRepository.findByUserId(userId).size();
    }

    public List<Room> filterRooms(String searchQuery) {
        return roomRepository.findByNameContaining(searchQuery, PageRequest.of(0, Integer.MAX_VALUE)).getContent();
    }

    public Optional<Integer> reserveRoom(int userId, Room room, LocalDate startDate, LocalDate endDate, int numberOfPeople) {
        // Check for overlapping reservations
        List<Reservation> overlapping = reservationRepository.findByRoomIdAndDateRange(
                room.getId(), startDate, endDate);
        if (!overlapping.isEmpty()) {
            return Optional.empty();
        }

        Reservation reservation = new Reservation();
        reservation.setRoomId(room.getId());
        reservation.setRoomName(room.getName());
        reservation.setCustomerName("Customer"); // Adjust based on user
        reservation.setStartDate(startDate);
        reservation.setEndDate(endDate);
        reservation.setNumberOfPeople(numberOfPeople);
        reservation.setTotalPrice(room.getPricePerNight() * (endDate.toEpochDay() - startDate.toEpochDay() + 1));
        reservation.setIsPaid(false);
        reservationRepository.save(reservation);
        return Optional.of(reservation.getId());
    }

    public boolean cancelReservation(int reservationId) {
        try {
            reservationRepository.deleteById(reservationId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addRoom(Room room) {
        try {
            roomRepository.save(room);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Reservation> getAllCancellableReservations(int userId) {
        return reservationRepository.findCancellableReservations(userId, LocalDate.now());
    }

    public Optional<Room> findById(int roomId) {
        return roomRepository.findById(roomId);
	}
}