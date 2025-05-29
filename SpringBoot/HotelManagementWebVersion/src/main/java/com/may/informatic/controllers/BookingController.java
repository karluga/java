package com.may.informatic.controllers;

import com.may.informatic.entities.Room;
import com.may.informatic.entities.Reservation;
import com.may.informatic.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // For status message

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        
        // Fetch cancellable reservations
        List<Reservation> cancellableReservations = roomService.getCancellableReservations(userId);

        // Debug: Print all cancellable reservations
        System.out.println("Debug: Fetched cancellable reservations:");
        cancellableReservations.forEach(reservation -> System.out.println(
            "Reservation ID: " + reservation.getId() +
            ", Room ID: " + reservation.getRoomId() +
            ", Start Date: " + reservation.getStartDate() +
            ", End Date: " + reservation.getEndDate()
        ));

        // Fetch all rooms and mark reserved ones
        Page<Room> roomPage = roomService.fetchRooms(userId, searchQuery, page, 10);
        List<Room> allRooms = roomPage.getContent();
        allRooms.forEach(room -> {
            boolean isReserved = cancellableReservations.stream()
                .anyMatch(reservation -> reservation.getRoomId() == room.getId());
            room.setReserved(isReserved); // Add a reserved flag to the room object

            // Debug: Print reservation matching logic for each room
            System.out.println("Debug: Room ID: " + room.getId() + ", Name: " + room.getName() + ", Reserved: " + isReserved);
        });

        // Debug: Print all room objects and their reserved status
        System.out.println("Debug: Final room objects and their reserved status:");
        allRooms.forEach(room -> System.out.println("Room ID: " + room.getId() + ", Name: " + room.getName() + ", Reserved: " + room.isReserved()));

        model.addAttribute("welcomeMessage", "Welcome, " + username);
        model.addAttribute("activeBookings", roomService.getActiveBookingsCount(userId));
        model.addAttribute("totalBookings", roomService.getTotalBookingsCount(userId));
        model.addAttribute("rooms", allRooms); // Rooms with reservation statuses
        model.addAttribute("reservationsByRoomId", cancellableReservations.stream()
            .collect(Collectors.toMap(Reservation::getRoomId, reservation -> reservation))); // Map reservations by room ID
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", roomPage.getTotalPages());
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("userId", userId);

        return "booking";
    }

    @PostMapping("/booking/reserve")
    public String handleReservation(@RequestParam int roomId, @RequestParam String startDate, 
                                   @RequestParam String endDate, @RequestParam int numberOfPeople, 
                                   RedirectAttributes redirectAttributes, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            redirectAttributes.addFlashAttribute("status", "User not logged in.");
            return "redirect:/booking";
        }

        String status = roomService.processReservation(userId, roomId, startDate, endDate, numberOfPeople);
        redirectAttributes.addFlashAttribute("status", status);
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