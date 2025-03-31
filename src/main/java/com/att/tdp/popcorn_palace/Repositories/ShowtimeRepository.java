package com.att.tdp.popcorn_palace.Repositories;


import com.att.tdp.popcorn_palace.model.Showtime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    // Function used to validate that there are no overlapping showtimes for the
    // same theater.
    List<Showtime> noOverlappingShowtimes(String theater, Instant endTime,
        Instant startTime);
}