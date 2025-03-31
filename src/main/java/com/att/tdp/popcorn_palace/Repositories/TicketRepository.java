package com.att.tdp.popcorn_palace.Repositories;

import com.att.tdp.popcorn_palace.model.Ticket;
import com.att.tdp.popcorn_palace.model.Showtime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    // For seat duplication check:
    boolean existsByShowtimeAndSeatNumber(Showtime show, Integer seatnumber);
}
