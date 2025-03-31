package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.repositories.MovieRepository;
import com.att.tdp.popcorn_palace.repositories.ShowtimeRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;

    public ShowtimeService(ShowtimeRepository showtimeRepository, MovieRepository movieRepository) {
        this.showtimeRepository = showtimeRepository;
        this.movieRepository = movieRepository;
    }

    public Optional<Showtime> findById(Long showtimeId) {
        return showtimeRepository.findById(showtimeId);
    }

    public Showtime addShowtime(Long movieId, Double price, String theater,
                                Instant startTime, Instant endTime) {

        // 1) Validate movie existence
        Movie movie = movieRepository.findById(movieId)
            .orElseThrow(() -> new IllegalArgumentException("Movie not found with id: " + movieId));

        // 2) Check overlapping showtimes
        List<Showtime> overlapping = showtimeRepository.findByTheaterAndStartTimeLessThanAndEndTimeGreaterThan(
            theater, endTime, startTime
        );
        if (!overlapping.isEmpty()) {
            throw new IllegalArgumentException("Overlapping showtime in theater: " + theater);
        }

        // 3) Create and save
        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        showtime.setPrice(price);
        showtime.setTheater(theater);
        showtime.setStartTime(startTime);
        showtime.setEndTime(endTime);

        return showtimeRepository.save(showtime);
    }

    public Showtime updateShowtime(Showtime existing, Long movieId, Double price, String theater,
                                   Instant startTime, Instant endTime) {

        // 1) Validate new movie
        Movie movie = movieRepository.findById(movieId)
            .orElseThrow(() -> new IllegalArgumentException("Movie not found with id: " + movieId));

        // 2) Overlapping check (excluding current showtime)
        List<Showtime> overlapping = showtimeRepository.findByTheaterAndStartTimeLessThanAndEndTimeGreaterThan(
            theater, endTime, startTime
        );
        overlapping.removeIf(s -> s.getId().equals(existing.getId()));
        if (!overlapping.isEmpty()) {
            throw new IllegalArgumentException("Overlapping showtime in the same theater");
        }

        // 3) Update fields
        existing.setMovie(movie);
        existing.setPrice(price);
        existing.setTheater(theater);
        existing.setStartTime(startTime);
        existing.setEndTime(endTime);

        return showtimeRepository.save(existing);
    }

    public void deleteShowtime(Showtime showtime) {
        showtimeRepository.delete(showtime);
    }
}
