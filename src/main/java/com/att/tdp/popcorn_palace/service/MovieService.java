package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.repositories.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Movie addMovie(Movie movie) {
        // If you have extra business rules or validations beyond the basics, do them here
        return movieRepository.save(movie);
    }

    public Optional<Movie> findByTitle(String title) {
        return movieRepository.findByTitle(title);
    }

    public Movie updateMovie(Movie existing, Movie updateData) {
        // Merge updates into the existing entity
        existing.setTitle(updateData.getTitle());
        existing.setGenre(updateData.getGenre());
        existing.setDuration(updateData.getDuration());
        existing.setRating(updateData.getRating());
        existing.setReleaseYear(updateData.getReleaseYear());

        return movieRepository.save(existing);
    }

    public void deleteMovie(Movie movie) {
        movieRepository.delete(movie);
    }
}
