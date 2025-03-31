package com.att.tdp.popcorn_palace.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID ticketId;  // Using UUID as the primary key

    // Many tickets can reference one showtime
    @ManyToOne
    @JoinColumn(name = "showtime_id", nullable = false)
    private Showtime showtime;

    private int seatNumber;

    private String userId; // Or you could reference a separate User entity

    // === Constructors ===
    public Ticket() {
    }

    public Ticket(Showtime showtime, int seatNumber, String userId) {
        this.showtime = showtime;
        this.seatNumber = seatNumber;
        this.userId = userId;
    }

    // === Getters and Setters ===
    public UUID getTicketId() {
        return ticketId;
    }

    public void setTicketId(UUID ticketId) {
        this.ticketId = ticketId;
    }

    public Showtime getShowtime() {
        return showtime;
    }

    public void setShowtime(Showtime showtime) {
        this.showtime = showtime;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
