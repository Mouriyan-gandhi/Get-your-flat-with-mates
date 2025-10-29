package com.roommateai.controller;

import com.roommateai.dto.RentalRequest;
import com.roommateai.dto.RentalResponse;
import com.roommateai.model.User;
import com.roommateai.service.AuthService;
import com.roommateai.service.RentalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Rental Controller
 * REST endpoints for rental management
 */
@RestController
@RequestMapping("/api/rentals")
@CrossOrigin(origins = "*")
public class RentalController {

    @Autowired
    private RentalService rentalService;

    @Autowired
    private AuthService authService;

    /**
     * Create a new rental listing
     */
    @PostMapping
    public ResponseEntity<?> createRental(@Valid @RequestBody RentalRequest rentalRequest,
                                          @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            RentalResponse rental = rentalService.createRental(rentalRequest, user);
            return ResponseEntity.ok(rental);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create rental");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get all available rentals
     */
    @GetMapping
    public ResponseEntity<List<RentalResponse>> getAllRentals() {
        List<RentalResponse> rentals = rentalService.getAllAvailableRentals();
        return ResponseEntity.ok(rentals);
    }

    /**
     * Get rental by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getRentalById(@PathVariable Long id) {
        Optional<RentalResponse> rental = rentalService.getRentalById(id);
        
        if (rental.isPresent()) {
            return ResponseEntity.ok(rental.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get rentals by owner
     */
    @GetMapping("/my-rentals")
    public ResponseEntity<?> getMyRentals(@RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            List<RentalResponse> rentals = rentalService.getRentalsByOwner(user);
            return ResponseEntity.ok(rentals);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch rentals");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Search rentals by city
     */
    @GetMapping("/search/city/{city}")
    public ResponseEntity<List<RentalResponse>> searchByCity(@PathVariable String city) {
        List<RentalResponse> rentals = rentalService.searchRentalsByCity(city);
        return ResponseEntity.ok(rentals);
    }

    /**
     * Search rentals by price range
     */
    @GetMapping("/search/price")
    public ResponseEntity<List<RentalResponse>> searchByPriceRange(
            @RequestParam BigDecimal minRent,
            @RequestParam BigDecimal maxRent) {
        List<RentalResponse> rentals = rentalService.searchRentalsByPriceRange(minRent, maxRent);
        return ResponseEntity.ok(rentals);
    }

    /**
     * Search rentals by multiple criteria
     */
    @GetMapping("/search")
    public ResponseEntity<List<RentalResponse>> searchRentals(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) BigDecimal minRent,
            @RequestParam(required = false) BigDecimal maxRent,
            @RequestParam(required = false) String propertyType,
            @RequestParam(required = false) String roomType) {
        
        List<RentalResponse> rentals = rentalService.searchRentalsByCriteria(
                city, state, minRent, maxRent, propertyType, roomType);
        return ResponseEntity.ok(rentals);
    }

    /**
     * Search rentals by text
     */
    @GetMapping("/search/text")
    public ResponseEntity<List<RentalResponse>> searchByText(@RequestParam String q) {
        List<RentalResponse> rentals = rentalService.searchRentalsByText(q);
        return ResponseEntity.ok(rentals);
    }

    /**
     * Find nearby rentals
     */
    @GetMapping("/search/nearby")
    public ResponseEntity<List<RentalResponse>> findNearbyRentals(
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude,
            @RequestParam(defaultValue = "5.0") Double radiusKm) {
        
        List<RentalResponse> rentals = rentalService.findNearbyRentals(latitude, longitude, radiusKm);
        return ResponseEntity.ok(rentals);
    }

    /**
     * Update rental
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRental(@PathVariable Long id,
                                         @Valid @RequestBody RentalRequest rentalRequest,
                                         @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            Optional<RentalResponse> updatedRental = rentalService.updateRental(id, rentalRequest, user);
            
            if (updatedRental.isPresent()) {
                return ResponseEntity.ok(updatedRental.get());
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update rental");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Delete rental
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRental(@PathVariable Long id,
                                         @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            boolean deleted = rentalService.deleteRental(id, user);
            
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Rental deleted successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete rental");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Verify rental (Admin only)
     */
    @PostMapping("/{id}/verify")
    public ResponseEntity<?> verifyRental(@PathVariable Long id,
                                         @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null || !user.getRole().equals(User.UserRole.ADMIN)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Admin access required"));
            }

            boolean verified = rentalService.verifyRental(id);
            
            if (verified) {
                return ResponseEntity.ok(Map.of("message", "Rental verified successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to verify rental");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
