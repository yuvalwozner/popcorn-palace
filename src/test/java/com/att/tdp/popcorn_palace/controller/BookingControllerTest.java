package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.repositories.MovieRepository;
import com.att.tdp.popcorn_palace.repositories.ShowtimeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    private Long sampleShowtimeId;

    @BeforeEach
    public void setup() {
        // Insert a Marvel movie
        Movie movie = new Movie();
        movie.setTitle("Captain America: The Winter Soldier");
        movie.setGenre("Action/Sci-Fi");
        movie.setDuration(136);
        movie.setRating(7.7);
        movie.setReleaseYear(2014);
        movieRepository.save(movie);

        // Create a showtime for booking
        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        showtime.setTheater("Hydra Theater");
        showtime.setStartTime(Instant.parse("2026-09-01T14:00:00Z"));
        showtime.setEndTime(Instant.parse("2026-09-01T16:00:00Z"));
        showtime.setPrice(15.0);
        showtimeRepository.save(showtime);

        sampleShowtimeId = showtime.getId();
    }

    // 1) Valid booking
    @Test
    public void testBookTicket_Valid() throws Exception {
        Map<String, Object> payload = Map.of(
                "showtimeId", sampleShowtimeId,
                "seatNumber", 5,
                "userId", "shield-agent"
        );
        String json = objectMapper.writeValueAsString(payload);

        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.bookingId").exists());
    }

    // 2) Prevent double booking
    @Test
    public void testBookTicket_DoubleBooking() throws Exception {
        // First booking
        Map<String, Object> payload = Map.of(
                "showtimeId", sampleShowtimeId,
                "seatNumber", 10,
                "userId", "first-agent"
        );
        String json = objectMapper.writeValueAsString(payload);

        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.bookingId").exists());

        // Second booking for same seat
        Map<String, Object> payload2 = Map.of(
                "showtimeId", sampleShowtimeId,
                "seatNumber", 10,
                "userId", "second-agent"
        );
        String json2 = objectMapper.writeValueAsString(payload2);

        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json2))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("already booked")));
    }

    // 3) Missing userId
    @Test
    public void testBookTicket_Invalid_MissingUserId() throws Exception {
        Map<String, Object> payload = Map.of(
                "showtimeId", sampleShowtimeId,
                "seatNumber", 5
        );
        String json = objectMapper.writeValueAsString(payload);

        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Missing required fields")));
    }

    // 4) Invalid showtime ID
    @Test
    public void testBookTicket_Invalid_NonNumericShowtimeId() throws Exception {
        Map<String, Object> payload = Map.of(
                "showtimeId", "non-numeric",
                "seatNumber", 5,
                "userId", "hydra-agent"
        );
        String json = objectMapper.writeValueAsString(payload);

        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Invalid showtimeId")));
    }

    // 5) Invalid seatNumber
    @Test
    public void testBookTicket_Invalid_NonNumericSeatNumber() throws Exception {
        Map<String, Object> payload = Map.of(
                "showtimeId", sampleShowtimeId,
                "seatNumber", "invalid-seat",
                "userId", "test-user"
        );
        String json = objectMapper.writeValueAsString(payload);

        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Invalid showtimeId or seatNumber format")));
    }
}
