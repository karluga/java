package com.may.informatic.controllers;

import com.may.informatic.entities.Room;
import com.may.informatic.entities.Reservation;
import com.may.informatic.services.RoomService;
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
public class BookingController {
    @Autowired
    private RoomService roomService;

    @GetMapping("/booking")
    public String showBookingPage(@RequestParam(defaultValue = "0") int page, 
                                 @RequestParam(defaultValue = "") String searchQuery, 
                                 Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        String username = (String) session.getAttribute("username");
        if (userId == null) {
            return "redirect:/login";
        }

        Page<Room> roomPage = roomService.fetchRooms(userId, searchQuery, page, 10);
        List<Reservation> cancellableReservations = roomService.getAllCancellableReservations(userId);

        model.addAttribute("welcomeMessage", "Welcome, " + username);
        model.addAttribute("activeBookings", roomService.getActiveBookingsCount(userId));
        model.addAttribute("totalBookings", roomService.getTotalBookingsCount(userId));
        model.addAttribute("rooms", roomPage.getContent());
        model.addAttribute("cancellableReservations", cancellableReservations);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", roomPage.getTotalPages());
        model.addAttribute("searchQuery", searchQuery);

        return "booking";
    }

    @PostMapping("/booking/reserve")
    public String handleReservation(@RequestParam int roomId, @RequestParam String startDate, 
                                   @RequestParam String endDate, @RequestParam int numberOfPeople, 
                                   Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Room room = roomService.findById(roomId).orElse(null);
        if (room == null) {
            model.addAttribute("status", "Room not found.");
            return "booking";
        }

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        if (end.isBefore(start)) {
            model.addAttribute("status", "End date cannot be before start date.");
            return "booking";
        }

        if (numberOfPeople > room.getMaxPeople()) {
            model.addAttribute("status", "Selected room cannot accommodate " + numberOfPeople + " people.");
            return "booking";
        }

        Optional<Integer> result = roomService.reserveRoom(userId, room, start, end, numberOfPeople);
        if (result.isEmpty()) {
            model.addAttribute("status", "Booking failed: The selected dates are already booked or an error occurred.");
        } else {
            model.addAttribute("status", "Booking successful for " + room.getName() + ".");
        }
        return "redirect:/booking";
    }

    @PostMapping("/booking/cancel")
    public String handleCancellation(@RequestParam int reservationId, Model model) {
        boolean success = roomService.cancelReservation(reservationId);
        if (success) {
            model.addAttribute("status", "Reservation canceled successfully.");
        } else {
            model.addAttribute("status", "Failed to cancel the reservation.");
        }
        return "redirect:/booking";
    }
}