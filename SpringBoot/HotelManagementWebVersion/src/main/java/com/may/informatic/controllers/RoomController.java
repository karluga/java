package com.may.informatic.controllers;

import com.may.informatic.entities.Room;
import com.may.informatic.entities.Reservation;
import com.may.informatic.entities.User;
import com.may.informatic.services.RoomService;
import com.may.informatic.services.ReservationService;
import com.may.informatic.services.UserService;
import com.may.informatic.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class RoomController {
    @Autowired
    private RoomService roomService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserService userService;

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/rooms")
    public String showRoomsPage(@RequestParam(defaultValue = "0") int page, 
                               @RequestParam(defaultValue = "") String searchQuery, 
                               Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        Integer role = (Integer) session.getAttribute("role");
        if (userId == null || role != 1) {
            return "redirect:/login";
        }

        Page<Room> roomPage = roomService.fetchRooms(userId, searchQuery, page, 10);
        List<Reservation> reservations = reservationService.fetchReservations(page, 10).getContent();
        reservations.forEach(reservation -> {
            reservation.getStatusClass(); // Ensure statusClass is calculated
            reservation.isCancelable();  // Ensure isCancelable is calculated
        });
        List<User> users = userService.fetchUsers();

        model.addAttribute("rooms", roomPage.getContent());
        model.addAttribute("reservations", reservations);
        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalRoomPages", roomPage.getTotalPages());
        model.addAttribute("totalReservationPages", reservationService.fetchReservations(page, 10).getTotalPages());
        model.addAttribute("searchQuery", searchQuery);

        return "rooms";
    }

    @PostMapping("/rooms/add")
    public String handleAddRoom(@RequestParam String name, @RequestParam double pricePerNight, 
                               @RequestParam int maxPeople, Model model) {
        Room room = new Room(name, maxPeople, pricePerNight);
        boolean success = roomService.addRoom(room);
        if (success) {
            model.addAttribute("status", "Room added successfully.");
        } else {
            model.addAttribute("status", "Failed to add room.");
        }
        return "redirect:/rooms";
    }

    @PostMapping("/rooms/reserve")
    public String handleReservation(@RequestParam int roomId, @RequestParam int userId, 
                                   @RequestParam String startDate, @RequestParam String endDate, 
                                   @RequestParam int numberOfPeople, Model model) {
        Room room = roomService.findById(roomId).orElse(null);
        if (room == null) {
            model.addAttribute("status", "Room not found.");
            return "rooms";
        }

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        Optional<Integer> result = roomService.reserveRoom(userId, room, start, end, numberOfPeople);
        if (result.isEmpty()) {
            model.addAttribute("status", "Reservation failed: The selected dates are already booked or an error occurred.");
        } else {
            model.addAttribute("status", "Reservation successful for room: " + room.getName());
        }
        return "redirect:/rooms";
    }

    @PostMapping("/rooms/cancel")
    public String handleCancellation(@RequestParam int reservationId, Model model) {
        boolean success = roomService.cancelReservation(reservationId);
        if (success) {
            model.addAttribute("status", "Reservation canceled successfully.");
        } else {
            model.addAttribute("status", "Failed to cancel reservation.");
        }
        return "redirect:/rooms";
    }

    @PostMapping("/rooms/markPaid")
    public String handleMarkAsPaid(@RequestParam int reservationId, Model model) {
        Reservation reservation = reservationService.findById(reservationId).orElse(null);
        if (reservation == null) {
            model.addAttribute("status", "Reservation not found.");
            return "rooms";
        }
        boolean success = paymentService.markAsPaid(reservation, true);
        if (success) {
            model.addAttribute("status", "Reservation marked as paid.");
        } else {
            model.addAttribute("status", "Failed to mark reservation as paid.");
        }
        return "redirect:/rooms";
    }

    @PostMapping("/rooms/partialPayment")
    public String handlePartialPayment(@RequestParam int reservationId, @RequestParam double paymentAmount, Model model) {
        Reservation reservation = reservationService.findById(reservationId).orElse(null);
        if (reservation == null) {
            model.addAttribute("status", "Reservation not found.");
            return "rooms";
        }
        boolean success = paymentService.handlePartialPayment(reservation, paymentAmount);
        if (success) {
            model.addAttribute("status", "Partial payment recorded.");
        } else {
            model.addAttribute("status", "Failed to record payment.");
        }
        return "redirect:/rooms";
    }
}