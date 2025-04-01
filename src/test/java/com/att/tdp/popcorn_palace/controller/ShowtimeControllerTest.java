package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.repositories.MovieRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ShowtimeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long sampleMovieId;

    @BeforeEach
    public void setup() {
        // Insert a Marvel movie used for showtime tests
        Movie movie = new Movie("Spider-Man: Far From Home", "Action/Sci-Fi", 129, 7.5, 2019);
        movieRepository.save(movie);
        sampleMovieId = movie.getId();
    }

    // 1) Add valid showtime
    @Test
    public void testAddShowtime_Valid() throws Exception {
        Map<String, Object> payload = Map.of(
                "movieId", sampleMovieId,
                "price", 22.0,
                "theater", "Avengers Theater",
                "startTime", Instant.parse("2026-07-03T14:00:00Z").toString(),
                "endTime", Instant.parse("2026-07-03T16:00:00Z").toString()
        );

        String json = objectMapper.writeValueAsString(payload);

        mockMvc.perform(post("/showtimes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.theater").value("Avengers Theater"));
    }

    // 2) Get showtime by ID
    @Test
    public void testGetShowtimeById_Valid() throws Exception {
        // Create a showtime first
        Map<String, Object> payload = Map.of(
                "movieId", sampleMovieId,
                "price", 25.0,
                "theater", "Wakanda Theater",
                "startTime", Instant.parse("2026-07-03T10:00:00Z").toString(),
                "endTime", Instant.parse("2026-07-03T12:00:00Z").toString()
        );
        String json = objectMapper.writeValueAsString(payload);

        String response = mockMvc.perform(post("/showtimes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        @SuppressWarnings("unchecked")
        Map<String, Object> respMap = objectMapper.readValue(response, Map.class);
        Integer showtimeId = (Integer) respMap.get("id");

        mockMvc.perform(get("/showtimes/" + showtimeId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(showtimeId));
    }

    // 3) Update a showtime
    @Test
    public void testUpdateShowtime_Valid() throws Exception {
        // Add a showtime
        Map<String, Object> payload = Map.of(
                "movieId", sampleMovieId,
                "price", 30.0,
                "theater", "Marvel Theater",
                "startTime", Instant.parse("2026-08-01T14:00:00Z").toString(),
                "endTime", Instant.parse("2026-08-01T16:00:00Z").toString()
        );
        String json = objectMapper.writeValueAsString(payload);

        String response = mockMvc.perform(post("/showtimes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        @SuppressWarnings("unchecked")
        Map<String, Object> respMap = objectMapper.readValue(response, Map.class);
        Integer showtimeId = (Integer) respMap.get("id");

        // Update payload
        Map<String, Object> updatePayload = Map.of(
                "movieId", sampleMovieId,
                "price", 35.0,
                "theater", "Marvel Theater Updated",
                "startTime", Instant.parse("2026-08-01T15:00:00Z").toString(),
                "endTime", Instant.parse("2026-08-01T17:00:00Z").toString()
        );
        String updateJson = objectMapper.writeValueAsString(updatePayload);

        mockMvc.perform(post("/showtimes/update/" + showtimeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.theater").value("Marvel Theater Updated"))
            .andExpect(jsonPath("$.price").value(35.0));
    }

    // 4) Delete a showtime
    @Test
    public void testDeleteShowtime_Valid() throws Exception {
        // Add a showtime
        Map<String, Object> payload = Map.of(
                "movieId", sampleMovieId,
                "price", 20.0,
                "theater", "Delete Theater",
                "startTime", Instant.parse("2026-07-03T14:00:00Z").toString(),
                "endTime", Instant.parse("2026-07-03T16:00:00Z").toString()
        );
        String json = objectMapper.writeValueAsString(payload);

        String response = mockMvc.perform(post("/showtimes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        @SuppressWarnings("unchecked")
        Map<String, Object> respMap = objectMapper.readValue(response, Map.class);
        Integer showtimeId = (Integer) respMap.get("id");

        mockMvc.perform(delete("/showtimes/" + showtimeId))
            .andExpect(status().isOk());
    }

    // 5) Invalid inputs
    @Test
public void testAddShowtime_InvalidInputs() throws Exception {
    // Missing movieId
    Map<String, Object> payload1 = Map.of(
            "price", 20.0,
            "theater", "Invalid Theater",
            "startTime", Instant.parse("2026-07-03T14:00:00Z").toString(),
            "endTime", Instant.parse("2026-07-03T16:00:00Z").toString()
    );
    String json1 = objectMapper.writeValueAsString(payload1);
    mockMvc.perform(post("/showtimes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json1))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Fields: movieId, price, theater, startTime, endTime are required")));

    Map<String, Object> payload2 = Map.of(
            "movieId", sampleMovieId,
            "theater", "Invalid Theater",
            "startTime", Instant.parse("2026-07-03T14:00:00Z").toString(),
            "endTime", Instant.parse("2026-07-03T16:00:00Z").toString()
    );
    String json2 = objectMapper.writeValueAsString(payload2);
    mockMvc.perform(post("/showtimes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json2))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Fields: movieId, price, theater, startTime, endTime are required")));

    Map<String, Object> payload3 = Map.of(
            "movieId", sampleMovieId,
            "price", 20.0,
            "startTime", Instant.parse("2026-07-03T14:00:00Z").toString(),
            "endTime", Instant.parse("2026-07-03T16:00:00Z").toString()
    );
    String json3 = objectMapper.writeValueAsString(payload3);
    mockMvc.perform(post("/showtimes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json3))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Fields: movieId, price, theater, startTime, endTime are required")));

    // Missing startTime
    Map<String, Object> payload4 = Map.of(
            "movieId", sampleMovieId,
            "price", 20.0,
            "theater", "Invalid Theater",
            "endTime", Instant.parse("2026-07-03T16:00:00Z").toString()
    );
    String json4 = objectMapper.writeValueAsString(payload4);
    mockMvc.perform(post("/showtimes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json4))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Fields: movieId, price, theater, startTime, endTime are required")));

    // Missing endTime
    Map<String, Object> payload5 = Map.of(
            "movieId", sampleMovieId,
            "price", 20.0,
            "theater", "Invalid Theater",
            "startTime", Instant.parse("2026-07-03T14:00:00Z").toString()
    );
    String json5 = objectMapper.writeValueAsString(payload5);
    mockMvc.perform(post("/showtimes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json5))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Fields: movieId, price, theater, startTime, endTime are required")));
}

    // 6) Overlapping showtimes
    @Test
    public void testAddShowtime_Overlapping() throws Exception {
        // Add first showtime
        Map<String, Object> payload1 = Map.of(
                "movieId", sampleMovieId,
                "price", 20.0,
                "theater", "Overlap Theater",
                "startTime", Instant.parse("2026-07-03T14:00:00Z").toString(),
                "endTime", Instant.parse("2026-07-03T16:00:00Z").toString()
        );
        String json1 = objectMapper.writeValueAsString(payload1);
        mockMvc.perform(post("/showtimes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json1))
            .andExpect(status().isOk());

        // Second showtime overlapping
        Map<String, Object> payload2 = Map.of(
                "movieId", sampleMovieId,
                "price", 25.0,
                "theater", "Overlap Theater",
                "startTime", Instant.parse("2026-07-03T15:00:00Z").toString(),
                "endTime", Instant.parse("2026-07-03T17:00:00Z").toString()
        );
        String json2 = objectMapper.writeValueAsString(payload2);
        mockMvc.perform(post("/showtimes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json2))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Overlapping showtime")));
    }
}
