package com.att.tdp.popcorn_palace.Repositories;


import com.att.tdp.popcorn_palace.model.Movie;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    // custom query method
    Optional<Movie> findByTitle(String title);
}
