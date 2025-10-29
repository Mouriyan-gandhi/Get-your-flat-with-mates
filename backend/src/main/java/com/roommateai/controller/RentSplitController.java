package com.roommateai.controller;

import com.roommateai.dto.RentSplitCalculationRequest;
import com.roommateai.dto.RentSplitCalculationResponse;
import com.roommateai.dto.RentSplitRequest;
import com.roommateai.dto.RentSplitResponse;
import com.roommateai.model.User;
import com.roommateai.service.AuthService;
import com.roommateai.service.RentSplitService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Rent Split Controller
 * REST endpoints for rent splitting functionality
 */
@RestController
@RequestMapping("/api/rent-split")
@CrossOrigin(origins = "*")
public class RentSplitController {

    @Autowired
    private RentSplitService rentSplitService;

    @Autowired
    private AuthService authService;

    /**
     * Calculate rent split without saving
     */
    @PostMapping("/calculate")
    public ResponseEntity<?> calculateRentSplit(@Valid @RequestBody RentSplitCalculationRequest request) {
        try {
            RentSplitCalculationResponse response = rentSplitService.calculateRentSplit(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to calculate rent split");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Calculate rent split with specific method
     */
    @PostMapping("/calculate/{method}")
    public ResponseEntity<?> calculateRentSplitWithMethod(
            @PathVariable String method,
            @Valid @RequestBody RentSplitCalculationRequest request) {
        try {
            RentSplitCalculationResponse response = rentSplitService.calculateRentSplitWithMethod(request, method);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to calculate rent split");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Create a rent split record
     */
    @PostMapping
    public ResponseEntity<?> createRentSplit(@Valid @RequestBody RentSplitRequest request,
                                            @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            RentSplitResponse response = rentSplitService.createRentSplit(request, user);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create rent split");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get rent splits for a rental
     */
    @GetMapping("/rental/{rentalId}")
    public ResponseEntity<List<RentSplitResponse>> getRentSplitsByRental(@PathVariable Long rentalId) {
        List<RentSplitResponse> rentSplits = rentSplitService.getRentSplitsByRental(rentalId);
        return ResponseEntity.ok(rentSplits);
    }

    /**
     * Get rent splits created by current user
     */
    @GetMapping("/my-splits")
    public ResponseEntity<?> getMyRentSplits(@RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            List<RentSplitResponse> rentSplits = rentSplitService.getRentSplitsByUser(user.getId());
            return ResponseEntity.ok(rentSplits);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get rent splits");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get latest rent split for a rental
     */
    @GetMapping("/rental/{rentalId}/latest")
    public ResponseEntity<?> getLatestRentSplit(@PathVariable Long rentalId) {
        Optional<RentSplitResponse> rentSplit = rentSplitService.getLatestRentSplit(rentalId);
        
        if (rentSplit.isPresent()) {
            return ResponseEntity.ok(rentSplit.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update a rent split
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRentSplit(@PathVariable Long id,
                                           @Valid @RequestBody RentSplitRequest request,
                                           @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            Optional<RentSplitResponse> updatedRentSplit = rentSplitService.updateRentSplit(id, request, user);
            
            if (updatedRentSplit.isPresent()) {
                return ResponseEntity.ok(updatedRentSplit.get());
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update rent split");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Delete a rent split
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRentSplit(@PathVariable Long id,
                                           @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            boolean deleted = rentSplitService.deleteRentSplit(id, user);
            
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Rent split deleted successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete rent split");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get rent split statistics for current user
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getRentSplitStats(@RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            Map<String, Object> stats = rentSplitService.getRentSplitStats(user.getId());
            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get rent split statistics");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Quick rent split calculation endpoint
     */
    @GetMapping("/quick-calculate")
    public ResponseEntity<?> quickCalculate(@RequestParam Double totalRent,
                                          @RequestParam(defaultValue = "0") Double utilities,
                                          @RequestParam Integer membersCount) {
        try {
            RentSplitCalculationRequest request = new RentSplitCalculationRequest();
            request.setTotalRent(java.math.BigDecimal.valueOf(totalRent));
            request.setUtilities(java.math.BigDecimal.valueOf(utilities));
            request.setMembersCount(membersCount);

            RentSplitCalculationResponse response = rentSplitService.calculateRentSplit(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to calculate rent split");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get available split methods
     */
    @GetMapping("/methods")
    public ResponseEntity<Map<String, Object>> getSplitMethods() {
        Map<String, Object> methods = new HashMap<>();
        methods.put("methods", List.of(
            Map.of("id", "equal", "name", "Equal Split", "description", "Split equally among all members"),
            Map.of("id", "proportional", "name", "Proportional Split", "description", "Split based on income proportion"),
            Map.of("id", "room_size", "name", "Room Size Split", "description", "Split based on room size")
        ));
        return ResponseEntity.ok(methods);
    }
}
