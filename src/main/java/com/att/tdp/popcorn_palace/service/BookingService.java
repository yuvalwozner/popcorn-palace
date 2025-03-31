package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.model.Ticket;
import com.att.tdp.popcorn_palace.repositories.ShowtimeRepository;
import com.att.tdp.popcorn_palace.repositories.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookingService {

    private final TicketRepository ticketRepository;
    private final ShowtimeRepository showtimeRepository;

    public BookingService(TicketRepository ticketRepository, ShowtimeRepository showtimeRepository) {
        this.ticketRepository = ticketRepository;
        this.showtimeRepository = showtimeRepository;
    }

    /**
     * Book a new ticket for a given showtime, seat, and user.
     *
     * @param showtimeId ID of the showtime
     * @param seatNumber Seat number being booked
     * @param userId ID of the user booking
     * @return The newly created Ticket entity
     * @throws IllegalArgumentException if the showtime doesn't exist or seat is already booked
     */
    public Ticket bookTicket(Long showtimeId, Integer seatNumber, String userId) {
        // 1) Validate the showtime
        Optional<Showtime> optionalShowtime = showtimeRepository.findById(showtimeId);
        if (!optionalShowtime.isPresent()) {
            throw new IllegalArgumentException("Showtime not found with id: " + showtimeId);
        }
        Showtime showtime = optionalShowtime.get();

        // 2) Check seat duplication
        boolean seatExists = ticketRepository.existsByShowtimeAndSeatNumber(showtime, seatNumber);
        if (seatExists) {
            throw new IllegalArgumentException("Seat " + seatNumber + " is already booked");
        }

        // 3) Create and save the ticket
        Ticket ticket = new Ticket();
        ticket.setShowtime(showtime);
        ticket.setSeatNumber(seatNumber);
        ticket.setUserId(userId);

        return ticketRepository.save(ticket);
    }
}
