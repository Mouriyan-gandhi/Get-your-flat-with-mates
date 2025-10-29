package com.roommateai.service;

import com.roommateai.dto.ReviewRequest;
import com.roommateai.dto.ReviewResponse;
import com.roommateai.dto.ReviewStatsResponse;
import com.roommateai.model.Review;
import com.roommateai.model.User;
import com.roommateai.repository.ReviewRepository;
import com.roommateai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Review Service
 * Handles review and rating functionality
 */
@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new review
     */
    public ReviewResponse createReview(ReviewRequest request, User reviewer) {
        // Check if user has already reviewed this target
        if (reviewRepository.hasUserReviewedTarget(reviewer.getId(), request.getTargetId())) {
            throw new RuntimeException("You have already reviewed this user/rental");
        }

        User target = userRepository.findById(request.getTargetId()).orElse(null);
        if (target == null) {
            throw new RuntimeException("Target user not found");
        }

        Review review = new Review();
        review.setReviewer(reviewer);
        review.setTarget(target);
        review.setTargetType(Review.ReviewTargetType.valueOf(request.getTargetType()));
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setIsVerified(false); // Admin verification required

        Review savedReview = reviewRepository.save(review);
        return convertToResponse(savedReview);
    }

    /**
     * Get reviews for a target
     */
    public List<ReviewResponse> getReviewsByTarget(Long targetId) {
        List<Review> reviews = reviewRepository.findReviewsByTargetOrderByCreatedAtDesc(targetId);
        return reviews.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get reviews by target type
     */
    public List<ReviewResponse> getReviewsByTargetAndType(Long targetId, String targetType) {
        Review.ReviewTargetType type = Review.ReviewTargetType.valueOf(targetType);
        List<Review> reviews = reviewRepository.findByTargetIdAndTargetType(targetId, type);
        return reviews.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get reviews given by a user
     */
    public List<ReviewResponse> getReviewsByReviewer(Long reviewerId) {
        List<Review> reviews = reviewRepository.findReviewsByReviewerOrderByCreatedAtDesc(reviewerId);
        return reviews.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get recent reviews
     */
    public List<ReviewResponse> getRecentReviews() {
        List<Review> reviews = reviewRepository.findRecentReviews();
        return reviews.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get review statistics for a target
     */
    public ReviewStatsResponse getReviewStats(Long targetId) {
        User target = userRepository.findById(targetId).orElse(null);
        if (target == null) {
            throw new RuntimeException("Target user not found");
        }

        ReviewStatsResponse stats = new ReviewStatsResponse();
        stats.setTargetId(targetId);
        stats.setTargetName(target.getName());

        // Get average rating
        BigDecimal averageRating = reviewRepository.getAverageRatingByTarget(targetId);
        stats.setAverageRating(averageRating != null ? averageRating : BigDecimal.ZERO);

        // Get total review count
        Long totalReviews = reviewRepository.countReviewsByTarget(targetId);
        stats.setTotalReviews(totalReviews);

        // Get rating distribution
        List<Object[]> distribution = reviewRepository.getRatingDistributionByTarget(targetId);
        List<Map<String, Object>> ratingDistribution = new ArrayList<>();
        
        for (Object[] row : distribution) {
            Map<String, Object> dist = new HashMap<>();
            dist.put("rating", row[0]);
            dist.put("count", row[1]);
            ratingDistribution.add(dist);
        }
        stats.setRatingDistribution(ratingDistribution);

        // Get min and max ratings
        Object[] reviewStats = reviewRepository.getReviewStatsForTarget(targetId);
        if (reviewStats != null && reviewStats.length >= 4) {
            stats.setMinRating((BigDecimal) reviewStats[2]);
            stats.setMaxRating((BigDecimal) reviewStats[3]);
        }

        return stats;
    }

    /**
     * Get top rated targets
     */
    public List<Map<String, Object>> getTopRatedTargets(Long minReviews) {
        List<Object[]> results = reviewRepository.getTopRatedTargets(minReviews);
        List<Map<String, Object>> topRated = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> target = new HashMap<>();
            target.put("targetId", row[0]);
            target.put("averageRating", row[1]);
            target.put("reviewCount", row[2]);
            
            // Get target name
            User targetUser = userRepository.findById((Long) row[0]).orElse(null);
            if (targetUser != null) {
                target.put("targetName", targetUser.getName());
            }
            
            topRated.add(target);
        }

        return topRated;
    }

    /**
     * Update a review
     */
    public Optional<ReviewResponse> updateReview(Long reviewId, ReviewRequest request, User user) {
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        
        if (optionalReview.isPresent()) {
            Review review = optionalReview.get();
            
            // Check if user can update this review
            if (!review.getReviewer().getId().equals(user.getId()) && 
                !user.getRole().equals(User.UserRole.ADMIN)) {
                return Optional.empty();
            }

            // Update fields
            review.setRating(request.getRating());
            review.setComment(request.getComment());
            review.setIsVerified(false); // Reset verification status

            Review updatedReview = reviewRepository.save(review);
            return Optional.of(convertToResponse(updatedReview));
        }
        
        return Optional.empty();
    }

    /**
     * Delete a review
     */
    public boolean deleteReview(Long reviewId, User user) {
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        
        if (optionalReview.isPresent()) {
            Review review = optionalReview.get();
            
            // Check if user can delete this review
            if (review.getReviewer().getId().equals(user.getId()) || 
                user.getRole().equals(User.UserRole.ADMIN)) {
                reviewRepository.delete(review);
                return true;
            }
        }
        
        return false;
    }

    /**
     * Verify a review (Admin only)
     */
    public boolean verifyReview(Long reviewId) {
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        
        if (optionalReview.isPresent()) {
            Review review = optionalReview.get();
            review.setIsVerified(true);
            reviewRepository.save(review);
            return true;
        }
        
        return false;
    }

    /**
     * Get review by ID
     */
    public Optional<ReviewResponse> getReviewById(Long reviewId) {
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        return optionalReview.map(this::convertToResponse);
    }

    /**
     * Check if user can review target
     */
    public boolean canUserReviewTarget(Long reviewerId, Long targetId) {
        return !reviewRepository.hasUserReviewedTarget(reviewerId, targetId);
    }

    /**
     * Get review summary for dashboard
     */
    public Map<String, Object> getReviewSummary(Long userId) {
        Map<String, Object> summary = new HashMap<>();
        
        // Reviews given by user
        Long reviewsGiven = reviewRepository.countReviewsByTarget(userId);
        summary.put("reviewsGiven", reviewsGiven);
        
        // Reviews received by user
        List<Review> reviewsReceived = reviewRepository.findByTargetId(userId);
        summary.put("reviewsReceived", reviewsReceived.size());
        
        // Average rating received
        BigDecimal averageRating = reviewRepository.getAverageRatingByTarget(userId);
        summary.put("averageRatingReceived", averageRating != null ? averageRating : BigDecimal.ZERO);
        
        // Recent reviews
        List<Review> recentReviews = reviewRepository.findRecentReviewsByTarget(userId);
        summary.put("recentReviews", recentReviews.stream()
                .limit(5)
                .map(this::convertToResponse)
                .collect(Collectors.toList()));

        return summary;
    }

    /**
     * Convert Review entity to ReviewResponse DTO
     */
    private ReviewResponse convertToResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setReviewerId(review.getReviewer().getId());
        response.setReviewerName(review.getReviewer().getName());
        response.setReviewerEmail(review.getReviewer().getEmail());
        response.setTargetId(review.getTarget().getId());
        response.setTargetName(review.getTarget().getName());
        response.setTargetType(review.getTargetType().name());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setIsVerified(review.getIsVerified());
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
        response.setCreatedAt(review.getCreatedAt().format(formatter));
        response.setUpdatedAt(review.getUpdatedAt().format(formatter));
        
        return response;
    }
}
