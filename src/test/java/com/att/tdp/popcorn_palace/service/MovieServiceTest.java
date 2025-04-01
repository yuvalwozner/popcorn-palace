package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.repositories.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    private Movie sampleMovie;

    @BeforeEach
    void init() {
        sampleMovie = new Movie("Avengers: Endgame", "Action/Sci-Fi", 181, 8.4, 2019);
    }

    @Test
    void testAddMovie() {
        when(movieRepository.save(sampleMovie)).thenReturn(sampleMovie);

        Movie created = movieService.addMovie(sampleMovie);
        assertNotNull(created);
        assertEquals("Avengers: Endgame", created.getTitle());
        verify(movieRepository, times(1)).save(sampleMovie);
    }

    @Test
    void testUpdateMovie() {
        // existing + updates
        Movie existing = new Movie("Avengers: Endgame", "Action/Sci-Fi", 180, 8.0, 2019);
        Movie updates = new Movie("Avengers: Endgame", "Action/Sci-Fi", 181, 8.4, 2019);

        when(movieRepository.save(existing)).thenReturn(updates);

        Movie updated = movieService.updateMovie(existing, updates);
        assertEquals(181, updated.getDuration());
        assertEquals(8.4, updated.getRating());
        verify(movieRepository, times(1)).save(existing);
    }

    @Test
    void testFindByTitle() {
        when(movieRepository.findByTitle("Avengers: Endgame")).thenReturn(Optional.of(sampleMovie));

        Optional<Movie> found = movieService.findByTitle("Avengers: Endgame");
        assertTrue(found.isPresent());
        assertEquals("Avengers: Endgame", found.get().getTitle());
        verify(movieRepository, times(1)).findByTitle("Avengers: Endgame");
    }

    @Test
    void testDeleteMovie() {
        movieService.deleteMovie(sampleMovie);
        verify(movieRepository, times(1)).delete(sampleMovie);
    }
}
