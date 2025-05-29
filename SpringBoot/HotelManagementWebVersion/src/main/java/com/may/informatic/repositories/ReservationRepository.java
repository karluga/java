package com.may.informatic.repositories;

import com.may.informatic.entities.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    @Query("SELECT r FROM Reservation r WHERE r.userId = :userId AND r.startDate >= :currentDate")
    List<Reservation> findCancellableReservations(int userId, LocalDate currentDate) ;

    @Query("SELECT r FROM Reservation r WHERE r.roomName LIKE %:searchQuery% OR r.customerName LIKE %:searchQuery%")
    Page<Reservation> findByRoomNameOrCustomerNameContaining(String searchQuery, Pageable pageable);

    @Query("SELECT SUM(p.amountPaid) FROM Payment p WHERE p.reservationId = :reservationId")
    Double getTotalPaidForReservation(int reservationId);

    List<Reservation> findByUserIdAndEndDateAfter(int userId, LocalDate endDate);

    List<Reservation> findByUserId(int userId);

    @Query("SELECT r FROM Reservation r WHERE r.roomId = :roomId AND " +
           "(r.startDate <= :endDate AND r.endDate >= :startDate)")
    List<Reservation> findByRoomIdAndDateRange(@Param("roomId") int roomId, 
                                               @Param("startDate") LocalDate startDate, 
                                               @Param("endDate") LocalDate endDate);

    @Query("SELECT r FROM Reservation r WHERE r.userId = :userId ORDER BY r.startDate ASC")
    List<Reservation> findByUserIdOrderByStartDateAsc(@Param("userId") int userId);
}