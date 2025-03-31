package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.model.Ticket;
import com.att.tdp.popcorn_palace.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // Booking a ticket
    @PostMapping
    public ResponseEntity<?> bookTicket(@RequestBody Map<String, Object> request) {
        // 1) Validate the request body
        if (!request.containsKey("showtimeId") ||
            !request.containsKey("seatNumber") ||
            !request.containsKey("userId")) {
            return ResponseEntity.badRequest().body("Missing required fields: showtimeId, seatNumber, userId");
        }

        try {
            Long showtimeId = Long.parseLong(request.get("showtimeId").toString());
            Integer seatNumber = Integer.parseInt(request.get("seatNumber").toString());
            String userId = request.get("userId").toString();

            // 2) Delegate to the BookingService
            Ticket savedTicket = bookingService.bookTicket(showtimeId, seatNumber, userId);

            // 3) Return booking ID
            return ResponseEntity.ok(Map.of("bookingId", savedTicket.getTicketId().toString()));

        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid showtimeId or seatNumber format");
        } catch (IllegalArgumentException ex) {
            // If seat is taken or showtime doesn't exist, service throws an exception
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
