package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.repositories.MovieRepository;
import com.att.tdp.popcorn_palace.repositories.ShowtimeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShowtimeServiceTest {

    @Mock
    private ShowtimeRepository showtimeRepository;

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private ShowtimeService showtimeService;

    private Movie sampleMovie;
    private Showtime sampleShowtime;

    @BeforeEach
    void init() {
        sampleMovie = new Movie("Black Panther", "Action/Sci-Fi", 134, 7.3, 2018);
        sampleShowtime = new Showtime();
        sampleShowtime.setId(100L);
        sampleShowtime.setTheater("Wakanda Theater");
        sampleShowtime.setPrice(15.0);
        sampleShowtime.setStartTime(Instant.parse("2026-08-01T14:00:00Z"));
        sampleShowtime.setEndTime(Instant.parse("2026-08-01T16:00:00Z"));
    }

    @Test
    void testAddShowtime_NoOverlap() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(sampleMovie));
        when(showtimeRepository.findByTheaterAndStartTimeLessThanAndEndTimeGreaterThan(
                "Wakanda Theater",
                sampleShowtime.getEndTime(),
                sampleShowtime.getStartTime()))
            .thenReturn(new ArrayList<>()); // no overlaps

        when(showtimeRepository.save(any(Showtime.class))).thenReturn(sampleShowtime);

        Showtime added = showtimeService.addShowtime(1L, 15.0, "Wakanda Theater",
                sampleShowtime.getStartTime(), sampleShowtime.getEndTime());

        assertNotNull(added);
        assertEquals("Wakanda Theater", added.getTheater());
        verify(showtimeRepository, times(1)).save(any(Showtime.class));
    }

    @Test
    void testAddShowtime_Overlap() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(sampleMovie));
        List<Showtime> overlapping = new ArrayList<>();
        overlapping.add(sampleShowtime);

        when(showtimeRepository.findByTheaterAndStartTimeLessThanAndEndTimeGreaterThan(
                "Wakanda Theater",
                sampleShowtime.getEndTime(),
                sampleShowtime.getStartTime()))
            .thenReturn(overlapping);

        assertThrows(IllegalArgumentException.class, () -> {
            showtimeService.addShowtime(1L, 15.0, "Wakanda Theater",
                    sampleShowtime.getStartTime(), sampleShowtime.getEndTime());
        });
    }

    @Test
    void testUpdateShowtime_Overlap() {
        // Similar approach to addShowtime, but for the update method
        when(movieRepository.findById(2L)).thenReturn(Optional.of(sampleMovie));
        List<Showtime> overlapping = new ArrayList<>();
        overlapping.add(sampleShowtime);

        Showtime existing = new Showtime();
        existing.setId(200L);
        existing.setTheater("Wakanda Theater");
        existing.setStartTime(Instant.parse("2026-08-01T12:00:00Z"));
        existing.setEndTime(Instant.parse("2026-08-01T14:00:00Z"));

        when(showtimeRepository.findByTheaterAndStartTimeLessThanAndEndTimeGreaterThan(
                "Wakanda Theater",
                sampleShowtime.getEndTime(),
                sampleShowtime.getStartTime()))
            .thenReturn(overlapping);

        assertThrows(IllegalArgumentException.class, () -> {
            showtimeService.updateShowtime(existing, 2L, 20.0, "Wakanda Theater",
                    sampleShowtime.getStartTime(), sampleShowtime.getEndTime());
        });
    }
}
