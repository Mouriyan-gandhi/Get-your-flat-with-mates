package com.roommateai.controller;

import com.roommateai.dto.ReviewRequest;
import com.roommateai.dto.ReviewResponse;
import com.roommateai.dto.ReviewStatsResponse;
import com.roommateai.model.User;
import com.roommateai.service.AuthService;
import com.roommateai.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Review Controller
 * REST endpoints for review and rating functionality
 */
@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private AuthService authService;

    /**
     * Create a new review
     */
    @PostMapping
    public ResponseEntity<?> createReview(@Valid @RequestBody ReviewRequest request,
                                        @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            // Check if user can review this target
            if (!reviewService.canUserReviewTarget(user.getId(), request.getTargetId())) {
                return ResponseEntity.badRequest().body(Map.of("error", "You have already reviewed this user/rental"));
            }

            ReviewResponse response = reviewService.createReview(request, user);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create review");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get reviews for a target
     */
    @GetMapping("/target/{targetId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByTarget(@PathVariable Long targetId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByTarget(targetId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Get reviews by target and type
     */
    @GetMapping("/target/{targetId}/type/{targetType}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByTargetAndType(
            @PathVariable Long targetId, @PathVariable String targetType) {
        List<ReviewResponse> reviews = reviewService.getReviewsByTargetAndType(targetId, targetType);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Get reviews given by current user
     */
    @GetMapping("/my-reviews")
    public ResponseEntity<?> getMyReviews(@RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            List<ReviewResponse> reviews = reviewService.getReviewsByReviewer(user.getId());
            return ResponseEntity.ok(reviews);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get reviews");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get recent reviews
     */
    @GetMapping("/recent")
    public ResponseEntity<List<ReviewResponse>> getRecentReviews() {
        List<ReviewResponse> reviews = reviewService.getRecentReviews();
        return ResponseEntity.ok(reviews);
    }

    /**
     * Get review statistics for a target
     */
    @GetMapping("/stats/{targetId}")
    public ResponseEntity<?> getReviewStats(@PathVariable Long targetId) {
        try {
            ReviewStatsResponse stats = reviewService.getReviewStats(targetId);
            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get review statistics");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get top rated targets
     */
    @GetMapping("/top-rated")
    public ResponseEntity<List<Map<String, Object>>> getTopRatedTargets(
            @RequestParam(defaultValue = "5") Long minReviews) {
        List<Map<String, Object>> topRated = reviewService.getTopRatedTargets(minReviews);
        return ResponseEntity.ok(topRated);
    }

    /**
     * Get review by ID
     */
    @GetMapping("/{reviewId}")
    public ResponseEntity<?> getReviewById(@PathVariable Long reviewId) {
        Optional<ReviewResponse> review = reviewService.getReviewById(reviewId);
        
        if (review.isPresent()) {
            return ResponseEntity.ok(review.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update a review
     */
    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(@PathVariable Long reviewId,
                                        @Valid @RequestBody ReviewRequest request,
                                        @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            Optional<ReviewResponse> updatedReview = reviewService.updateReview(reviewId, request, user);
            
            if (updatedReview.isPresent()) {
                return ResponseEntity.ok(updatedReview.get());
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update review");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Delete a review
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId,
                                        @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            boolean deleted = reviewService.deleteReview(reviewId, user);
            
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Review deleted successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete review");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Verify a review (Admin only)
     */
    @PostMapping("/{reviewId}/verify")
    public ResponseEntity<?> verifyReview(@PathVariable Long reviewId,
                                        @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null || !user.getRole().equals(User.UserRole.ADMIN)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Admin access required"));
            }

            boolean verified = reviewService.verifyReview(reviewId);
            
            if (verified) {
                return ResponseEntity.ok(Map.of("message", "Review verified successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to verify review");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Check if user can review target
     */
    @GetMapping("/can-review/{targetId}")
    public ResponseEntity<?> canUserReviewTarget(@PathVariable Long targetId,
                                               @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            boolean canReview = reviewService.canUserReviewTarget(user.getId(), targetId);
            return ResponseEntity.ok(Map.of("canReview", canReview));

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to check review eligibility");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get review summary for dashboard
     */
    @GetMapping("/summary")
    public ResponseEntity<?> getReviewSummary(@RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            Map<String, Object> summary = reviewService.getReviewSummary(user.getId());
            return ResponseEntity.ok(summary);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get review summary");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get rating distribution for a target
     */
    @GetMapping("/rating-distribution/{targetId}")
    public ResponseEntity<?> getRatingDistribution(@PathVariable Long targetId) {
        try {
            ReviewStatsResponse stats = reviewService.getReviewStats(targetId);
            return ResponseEntity.ok(Map.of("ratingDistribution", stats.getRatingDistribution()));

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get rating distribution");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
