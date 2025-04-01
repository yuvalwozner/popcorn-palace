package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.service.MovieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    // GET /movies/all
    @GetMapping("/all")
    public ResponseEntity<List<Movie>> getMovies() {
        List<Movie> movies = movieService.getAllMovies();
        return ResponseEntity.ok(movies);
    }

    // POST /movies
    @PostMapping
    public ResponseEntity<?> addMovie(@RequestBody Movie movie) {
        // Basic request validation
        if (movie.getTitle() == null || movie.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Title is required");
        }
        if (movie.getGenre() == null || movie.getGenre().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Genre is required");
        }
        if (movie.getDuration() == null || movie.getDuration() < 0) {
            return ResponseEntity.badRequest().body("Duration must be non-negative");
        }
        if (movie.getRating() == null || movie.getRating() < 0 || movie.getRating() > 10) {
            return ResponseEntity.badRequest().body("Rating must be between 0 and 10");
        }
        if (movie.getReleaseYear() == null) {
            return ResponseEntity.badRequest().body("Release year is required");
        }
        // Delegate to service
        Movie saved = movieService.addMovie(movie);
        return ResponseEntity.ok(saved);
    }

    // POST /movies/update/{movieTitle}
    @PostMapping("/update/{movieTitle}")
    public ResponseEntity<?> updateMovie(@PathVariable String movieTitle, @RequestBody Movie updateMovie) {
        // Check if the movie exists
        Optional<Movie> optionalMovie = movieService.findByTitle(movieTitle);
        if (!optionalMovie.isPresent()) {
            return ResponseEntity.badRequest().body("Movie not found with title: " + movieTitle);
        }

        // Basic request validation
        if (updateMovie.getTitle() == null || updateMovie.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Title is required");
        }
        if (updateMovie.getGenre() == null || updateMovie.getGenre().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Genre is required");
        }
        if (updateMovie.getDuration() == null || updateMovie.getDuration() < 0) {
            return ResponseEntity.badRequest().body("Duration must be non-negative");
        }
        if (updateMovie.getRating() == null || updateMovie.getRating() < 0 || updateMovie.getRating() > 10) {
            return ResponseEntity.badRequest().body("Rating must be between 0 and 10");
        }
        if (updateMovie.getReleaseYear() == null) {
            return ResponseEntity.badRequest().body("Release year is required");
        }

        // Delegate to service
        Movie existing = optionalMovie.get();
        Movie updated = movieService.updateMovie(existing, updateMovie);
        return ResponseEntity.ok(updated);
    }

    // DELETE /movies/{movieTitle}
    @DeleteMapping("/{movieTitle}")
    public ResponseEntity<?> deleteMovie(@PathVariable String movieTitle) {
        Optional<Movie> optionalMovie = movieService.findByTitle(movieTitle);
        if (!optionalMovie.isPresent()) {
            return ResponseEntity.badRequest().body("Movie not found with title: " + movieTitle);
        }

        movieService.deleteMovie(optionalMovie.get());
        return ResponseEntity.ok().build();
    }
}
