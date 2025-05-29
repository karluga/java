package com.may.informatic.services;

import com.may.informatic.entities.Room;
import com.may.informatic.entities.Reservation;
import com.may.informatic.repositories.RoomRepository;

import jakarta.servlet.http.HttpSession;

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
    public boolean addRoom(Room room) {
        try {
            roomRepository.save(room);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isReservationOverlapping(int roomId, LocalDate startDate, LocalDate endDate) {
        List<Reservation> overlapping = reservationRepository.findByRoomIdAndDateRange(roomId, startDate, endDate);
        return !overlapping.isEmpty();
    }

    public Optional<Integer> reserveRoom(HttpSession session, Room room, LocalDate startDate, LocalDate endDate, int numberOfPeople) {
        // Retrieve userId from session
        Integer userId = (Integer) session.getAttribute("userId");
        System.out.println("User ID from session: " + userId);
        if (userId == null) {
            return Optional.empty(); // No user ID in session, cannot proceed
        }

        // Check for overlapping reservations using the new method
        if (isReservationOverlapping(room.getId(), startDate, endDate)) {
            return Optional.empty(); // Dates are already booked
        }

        Reservation reservation = new Reservation();
        reservation.setUserId(userId); // Set userId from session
        reservation.setRoomId(room.getId());
        reservation.setRoomName(room.getName());
        reservation.setStartDate(startDate);
        reservation.setEndDate(endDate);
        reservation.setNumberOfPeople(numberOfPeople);
        reservation.setTotalPrice(room.getPricePerNight() * (endDate.toEpochDay() - startDate.toEpochDay() + 1));
        reservation.setIsPaid(false);
        System.out.print(reservation);
        try {
            reservationRepository.save(reservation);
            System.out.println("Debug: Reservation saved successfully: " + reservation);
        } catch (Exception e) {
            System.out.println("Debug: Failed to save reservation: " + e.getMessage());
            e.printStackTrace();
        }        
        return Optional.of(reservation.getId());
    }


    public String processReservation(int userId, int roomId, String startDate, String endDate, int numberOfPeople) {
        Optional<Room> roomOptional = findById(roomId);
        if (roomOptional.isEmpty()) {
            return "Room not found.";
        }

        Room room = roomOptional.get();
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        LocalDate today = LocalDate.now();

        if (start.isBefore(today)) {
            return "Booking start date cannot be in the past.";
        }

        if (end.isBefore(start)) {
            return "End date cannot be before start date.";
        }

        if (numberOfPeople > room.getMaxPeople()) {
            return "Selected room cannot accommodate " + numberOfPeople + " people.";
        }

        if (isReservationOverlapping(room.getId(), start, end)) {
            return "The selected dates are already booked for " + room.getName() + ".";
        }

        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setRoomId(room.getId());
        reservation.setRoomName(room.getName());
        reservation.setStartDate(start);
        reservation.setEndDate(end);
        reservation.setNumberOfPeople(numberOfPeople);
        reservation.setTotalPrice(room.getPricePerNight() * (end.toEpochDay() - start.toEpochDay() + 1));
        reservation.setIsPaid(false);

        try {
            reservationRepository.save(reservation);
            return "Booking successful for " + room.getName() + ".";
        } catch (Exception e) {
            return "An unexpected error occurred. Please try again later.";
        }
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

    public List<Reservation> getReservationsSortedByStartDate(int userId) {
        return reservationRepository.findByUserIdOrderByStartDateAsc(userId);
    }

    public List<Reservation> getCancellableReservations(int userId) {
        return reservationRepository.findCancellableReservations(userId, LocalDate.now());
    }

    public Optional<Room> findById(int roomId) {
        return roomRepository.findById(roomId);
    }
}
