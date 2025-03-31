package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.service.ShowtimeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/showtimes")
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    public ShowtimeController(ShowtimeService showtimeService) {
        this.showtimeService = showtimeService;
    }

    // GET /showtimes/{id}
    @GetMapping("/{showtimeId}")
    public ResponseEntity<?> getShowtime(@PathVariable("showtimeId") Long id) {
        Optional<Showtime> optionalShowtime = showtimeService.findById(id);
        if (!optionalShowtime.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Showtime showtime = optionalShowtime.get();

        // Construct a response
        Map<String, Object> response = Map.of(
                "id", showtime.getId(),
                "price", showtime.getPrice(),
                "movieId", showtime.getMovie().getId(),
                "theater", showtime.getTheater(),
                "startTime", showtime.getStartTime(),
                "endTime", showtime.getEndTime()
        );
        return ResponseEntity.ok(response);
    }

    // POST /showtimes
    @PostMapping
    public ResponseEntity<?> addShowtime(@RequestBody Map<String, Object> payload) {
        // Basic request checks
        if (!payload.containsKey("movieId") || !payload.containsKey("price") ||
            !payload.containsKey("theater") || !payload.containsKey("startTime") ||
            !payload.containsKey("endTime")) {
            return ResponseEntity.badRequest().body("Fields: movieId, price, theater, startTime, endTime are required");
        }

        try {
            Long movieId = Long.parseLong(payload.get("movieId").toString());
            Double price = Double.parseDouble(payload.get("price").toString());
            String theater = payload.get("theater").toString().trim();
            Instant startTime = Instant.parse(payload.get("startTime").toString());
            Instant endTime = Instant.parse(payload.get("endTime").toString());

            if (price <= 0) {
                return ResponseEntity.badRequest().body("Price must be positive");
            }
            if (!endTime.isAfter(startTime)) {
                return ResponseEntity.badRequest().body("endTime must be after startTime");
            }

            Showtime saved = showtimeService.addShowtime(movieId, price, theater, startTime, endTime);
            Map<String, Object> response = Map.of(
                    "id", saved.getId(),
                    "price", saved.getPrice(),
                    "movieId", saved.getMovie().getId(),
                    "theater", saved.getTheater(),
                    "startTime", saved.getStartTime(),
                    "endTime", saved.getEndTime()
            );
            return ResponseEntity.ok(response);

        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid numeric format for movieId or price");
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // POST /showtimes/update/{showtimeId}
    @PostMapping("/update/{showtimeId}")
    public ResponseEntity<?> updateShowtime(@PathVariable Long showtimeId, @RequestBody Map<String, Object> payload) {
        // Basic validations
        if (!payload.containsKey("movieId") || !payload.containsKey("price") ||
            !payload.containsKey("theater") || !payload.containsKey("startTime") ||
            !payload.containsKey("endTime")) {
            return ResponseEntity.badRequest().body("All fields (movieId, price, theater, startTime, endTime) are required");
        }

        Optional<Showtime> optExisting = showtimeService.findById(showtimeId);
        if (!optExisting.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Showtime existing = optExisting.get();

        try {
            Long movieId = Long.parseLong(payload.get("movieId").toString());
            Double price = Double.parseDouble(payload.get("price").toString());
            String theater = payload.get("theater").toString().trim();
            Instant startTime = Instant.parse(payload.get("startTime").toString());
            Instant endTime = Instant.parse(payload.get("endTime").toString());

            if (price <= 0) {
                return ResponseEntity.badRequest().body("Price must be positive");
            }
            if (!endTime.isAfter(startTime)) {
                return ResponseEntity.badRequest().body("endTime must be after startTime");
            }

            // Service call
            Showtime updated = showtimeService.updateShowtime(existing, movieId, price, theater, startTime, endTime);
            return ResponseEntity.ok(updated);

        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid numeric format for movieId or price");
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // DELETE /showtimes/{showtimeId}
    @DeleteMapping("/{showtimeId}")
    public ResponseEntity<?> deleteShowtime(@PathVariable Long showtimeId) {
        Optional<Showtime> opt = showtimeService.findById(showtimeId);
        if (!opt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        showtimeService.deleteShowtime(opt.get());
        return ResponseEntity.ok().build();
    }
}
