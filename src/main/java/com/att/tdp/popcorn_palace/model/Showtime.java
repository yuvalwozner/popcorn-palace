package com.att.tdp.popcorn_palace.model;


import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "showtimes")
public class Showtime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many showtimes can belong to one movie
    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    private String theater;

    private double price;

    // Using Instant for a universal timestamp (UTC-based)
    private Instant startTime;

    private Instant endTime;

    // === Constructors ===
    public Showtime() {
    }

    public Showtime(Movie movie, String theater, double price,
                    Instant startTime, Instant endTime) {
        this.movie = movie;
        this.theater = theater;
        this.price = price;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // === Getters and Setters ===
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public String getTheater() {
        return theater;
    }

    public void setTheater(String theater) {
        this.theater = theater;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }
}