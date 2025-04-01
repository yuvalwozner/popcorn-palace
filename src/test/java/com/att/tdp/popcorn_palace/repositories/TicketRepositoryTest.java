package com.att.tdp.popcorn_palace.repositories;

import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.model.Ticket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TicketRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Test
    void testExistsByShowtimeAndSeatNumber() {
        // Insert a Marvel movie: Thor
        Movie movie = new Movie("Thor", "Fantasy/Action", 115, 7.0, 2011);
        movieRepository.save(movie);

        // Create a showtime
        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        showtime.setTheater("Asgard Theater");
        showtime.setStartTime(Instant.parse("2026-07-02T14:00:00Z"));
        showtime.setEndTime(Instant.parse("2026-07-02T16:00:00Z"));
        showtime.setPrice(15.0);
        showtimeRepository.save(showtime);

        // Create a ticket for seat 10
        Ticket ticket = new Ticket();
        ticket.setShowtime(showtime);
        ticket.setSeatNumber(10);
        ticket.setUserId("marvel-fan");
        ticketRepository.save(ticket);

        // Verify the ticket exists for seat 10
        boolean exists = ticketRepository.existsByShowtimeAndSeatNumber(showtime, 10);
        assertThat(exists).isTrue();

        // Verify seat 20 is not booked
        boolean notExists = ticketRepository.existsByShowtimeAndSeatNumber(showtime, 20);
        assertThat(notExists).isFalse();
    }
}
