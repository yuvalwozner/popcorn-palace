package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.model.Movie;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 1. Add test
    @Test
    public void testAddMovie_Valid() throws Exception {
        Movie movie = new Movie();
        movie.setTitle("Black Panther");
        movie.setGenre("Action/Sci-Fi");
        movie.setDuration(134);
        movie.setRating(7.3);
        movie.setReleaseYear(2018);

        String json = objectMapper.writeValueAsString(movie);

        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.title").value("Black Panther"));
    }

    // 2. Update test
    @Test
    public void testUpdateMovie_Valid() throws Exception {
        // First, add a movie
        Movie movie = new Movie();
        movie.setTitle("Thor: Ragnarok");
        movie.setGenre("Action/Sci-Fi");
        movie.setDuration(130);
        movie.setRating(7.9);
        movie.setReleaseYear(2017);

        String addJson = objectMapper.writeValueAsString(movie);
        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(addJson))
            .andExpect(status().isOk());

        // Now update it
        Movie updateMovie = new Movie();
        updateMovie.setTitle("Thor: Ragnarok UPDATED");
        updateMovie.setGenre("Comedy/Action");
        updateMovie.setDuration(131);
        updateMovie.setRating(8.1);
        updateMovie.setReleaseYear(2017);

        String updateJson = objectMapper.writeValueAsString(updateMovie);
        mockMvc.perform(post("/movies/update/Thor: Ragnarok")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Thor: Ragnarok UPDATED"))
            .andExpect(jsonPath("$.genre").value("Comedy/Action"))
            .andExpect(jsonPath("$.rating").value(8.1));
    }

    // 3. Delete test
    @Test
    public void testDeleteMovie_Valid() throws Exception {
        // Add a movie for deletion
        Movie movie = new Movie();
        movie.setTitle("Doctor Strange");
        movie.setGenre("Action/Fantasy");
        movie.setDuration(115);
        movie.setRating(7.5);
        movie.setReleaseYear(2016);

        String json = objectMapper.writeValueAsString(movie);
        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk());

        // Delete it
        mockMvc.perform(delete("/movies/Doctor Strange"))
            .andExpect(status().isOk());
    }

    // 4. Get test
    @Test
    public void testGetAllMovies() throws Exception {
        // Add a movie
        Movie movie = new Movie();
        movie.setTitle("Avengers: Age of Ultron");
        movie.setGenre("Action/Sci-Fi");
        movie.setDuration(141);
        movie.setRating(7.3);
        movie.setReleaseYear(2015);

        String json = objectMapper.writeValueAsString(movie);
        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk());

        // Get all movies
        mockMvc.perform(get("/movies/all"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[?(@.title=='Avengers: Age of Ultron')]").exists());
    }

    // 5. Invalid input: Missing title
    @Test
    public void testAddMovie_Invalid_MissingTitle() throws Exception {
        Movie movie = new Movie();
        movie.setGenre("Action/Sci-Fi");
        movie.setDuration(130);
        movie.setRating(7.9);
        movie.setReleaseYear(2017);

        String json = objectMapper.writeValueAsString(movie);
        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Title is required")));
    }

    // 6. Invalid: empty genre
    @Test
    public void testAddMovie_Invalid_EmptyGenre() throws Exception {
        Movie movie = new Movie();
        movie.setTitle("Captain Marvel");
        movie.setGenre("    "); // Just spaces
        movie.setDuration(123);
        movie.setRating(7.0);
        movie.setReleaseYear(2019);

        String json = objectMapper.writeValueAsString(movie);
        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Genre is required")));
    }

    // 7. Invalid: negative duration
    @Test
    public void testAddMovie_Invalid_NegativeDuration() throws Exception {
        Movie movie = new Movie();
        movie.setTitle("Guardians of the Galaxy Vol. 2");
        movie.setGenre("Action/Sci-Fi");
        movie.setDuration(-10);
        movie.setRating(7.6);
        movie.setReleaseYear(2017);

        String json = objectMapper.writeValueAsString(movie);
        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Duration must be non-negative")));
    }

    // 8. Invalid: rating out of range
    @Test
    public void testAddMovie_Invalid_RatingOutOfRange() throws Exception {
        Movie movie = new Movie();
        movie.setTitle("Ant-Man");
        movie.setGenre("Action/Comedy");
        movie.setDuration(117);
        movie.setRating(12.0); // Out of expected range
        movie.setReleaseYear(2015);

        String json = objectMapper.writeValueAsString(movie);
        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Rating must be between 0 and 10")));
    }

    // 9. Invalid: missing release year
    @Test
    public void testAddMovie_Invalid_MissingReleaseYear() throws Exception {
        Movie movie = new Movie();
        movie.setTitle("Captain America: Civil War");
        movie.setGenre("Action/Sci-Fi");
        movie.setDuration(147);
        movie.setRating(7.8);

        String json = objectMapper.writeValueAsString(movie);
        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Release year is required")));
    }
}
