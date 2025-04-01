package com.att.tdp.popcorn_palace.repositories;

import com.att.tdp.popcorn_palace.model.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MovieRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;

    @Test
    void testFindByTitle_Found() {
        // Insert a Marvel movie: Avengers: Infinity War
        Movie movie = new Movie("Avengers: Infinity War", "Action/Sci-Fi", 149, 8.4, 2018);
        movieRepository.save(movie);

        // Check if we can find it by title
        Optional<Movie> found = movieRepository.findByTitle("Avengers: Infinity War");
        assertThat(found).isPresent();
        assertThat(found.get().getGenre()).isEqualTo("Action/Sci-Fi");
    }

    @Test
    void testFindByTitle_NotFound() {
        // Searching for a nonexistent Marvel movie
        Optional<Movie> found = movieRepository.findByTitle("Non-Existent Marvel Movie");
        assertThat(found).isNotPresent();
    }
}
