package com.att.tdp.popcorn_palace.repositories;

import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.Showtime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ShowtimeRepositoryTest {

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Test
    void testOverlappingShowtimeQuery() {
        // Insert a Marvel movie: Iron Man
        Movie movie = new Movie("Iron Man", "Action/Sci-Fi", 126, 7.9, 2008);
        movieRepository.save(movie);

        // Create a showtime from 10:00 to 12:00 UTC on July 1, 2026
        Showtime s = new Showtime();
        s.setMovie(movie);
        s.setTheater("Stark Theater");
        s.setStartTime(Instant.parse("2026-07-01T10:00:00Z"));
        s.setEndTime(Instant.parse("2026-07-01T12:00:00Z"));
        s.setPrice(12.0);
        showtimeRepository.save(s);

        // Overlapping window: 09:00 to 11:00 on July 1, 2026
        List<Showtime> overlapping = showtimeRepository.findByTheaterAndStartTimeLessThanAndEndTimeGreaterThan(
                "Stark Theater",
                Instant.parse("2026-07-01T11:00:00Z"),
                Instant.parse("2026-07-01T09:00:00Z")
        );
        assertThat(overlapping).hasSize(1);

        // Non-overlapping window: 07:00 to 09:00 on July 1, 2026
        List<Showtime> nonOverlapping = showtimeRepository.findByTheaterAndStartTimeLessThanAndEndTimeGreaterThan(
                "Stark Theater",
                Instant.parse("2026-07-01T09:00:00Z"),
                Instant.parse("2026-07-01T07:00:00Z")
        );
        assertThat(nonOverlapping).isEmpty();
    }
}
