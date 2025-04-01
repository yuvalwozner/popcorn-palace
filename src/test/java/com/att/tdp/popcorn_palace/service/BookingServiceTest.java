package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.model.Ticket;
import com.att.tdp.popcorn_palace.repositories.ShowtimeRepository;
import com.att.tdp.popcorn_palace.repositories.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ShowtimeRepository showtimeRepository;

    @InjectMocks
    private BookingService bookingService;

    private Showtime sampleShowtime;

    @BeforeEach
    void init() {
        Movie sampleMovie = new Movie("Avengers: Infinity War", "Action/Sci-Fi", 149, 8.4, 2018);

        sampleShowtime = new Showtime();
        sampleShowtime.setId(300L);
        sampleShowtime.setMovie(sampleMovie);
        sampleShowtime.setTheater("Marvel Theater");
        sampleShowtime.setStartTime(Instant.parse("2026-09-01T14:00:00Z"));
        sampleShowtime.setEndTime(Instant.parse("2026-09-01T16:00:00Z"));
        sampleShowtime.setPrice(15.0);
    }

    @Test
    void testBookTicket_Success() {
        when(showtimeRepository.findById(300L)).thenReturn(Optional.of(sampleShowtime));
        when(ticketRepository.existsByShowtimeAndSeatNumber(sampleShowtime, 5)).thenReturn(false);

        Ticket ticketStub = new Ticket();
        ticketStub.setSeatNumber(5);
        ticketStub.setShowtime(sampleShowtime);
        ticketStub.setUserId("marvel-fan");

        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticketStub);

        Ticket created = bookingService.bookTicket(300L, 5, "marvel-fan");
        assertNotNull(created);
        assertEquals(5, created.getSeatNumber());
        assertEquals("marvel-fan", created.getUserId());

        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void testBookTicket_ShowtimeNotFound() {
        when(showtimeRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.bookTicket(999L, 10, "tony-stark");
        });
    }

    @Test
    void testBookTicket_SeatAlreadyBooked() {
        when(showtimeRepository.findById(300L)).thenReturn(Optional.of(sampleShowtime));
        when(ticketRepository.existsByShowtimeAndSeatNumber(sampleShowtime, 10)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.bookTicket(300L, 10, "another-user");
        });
    }
}
