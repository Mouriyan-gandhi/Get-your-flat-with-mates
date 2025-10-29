package com.roommateai.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Review Request DTO
 */
public class ReviewRequest {
    
    @NotNull(message = "Target ID is required")
    private Long targetId;
    
    @NotNull(message = "Target type is required")
    private String targetType;
    
    @NotNull(message = "Rating is required")
    @DecimalMin(value = "1.0", message = "Rating must be at least 1.0")
    @DecimalMax(value = "5.0", message = "Rating must not exceed 5.0")
    private BigDecimal rating;
    
    @Size(max = 1000, message = "Comment must not exceed 1000 characters")
    private String comment;
    
    // Constructors
    public ReviewRequest() {}
    
    public ReviewRequest(Long targetId, String targetType, BigDecimal rating, String comment) {
        this.targetId = targetId;
        this.targetType = targetType;
        this.rating = rating;
        this.comment = comment;
    }
    
    // Getters and Setters
    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }
    
    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    
    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}

/**
 * Review Response DTO
 */
class ReviewResponse {
    
    private Long id;
    private Long reviewerId;
    private String reviewerName;
    private String reviewerEmail;
    private Long targetId;
    private String targetName;
    private String targetType;
    private BigDecimal rating;
    private String comment;
    private Boolean isVerified;
    private String createdAt;
    private String updatedAt;
    
    // Constructors
    public ReviewResponse() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getReviewerId() { return reviewerId; }
    public void setReviewerId(Long reviewerId) { this.reviewerId = reviewerId; }
    
    public String getReviewerName() { return reviewerName; }
    public void setReviewerName(String reviewerName) { this.reviewerName = reviewerName; }
    
    public String getReviewerEmail() { return reviewerEmail; }
    public void setReviewerEmail(String reviewerEmail) { this.reviewerEmail = reviewerEmail; }
    
    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }
    
    public String getTargetName() { return targetName; }
    public void setTargetName(String targetName) { this.targetName = targetName; }
    
    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    
    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    
    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}

/**
 * Review Statistics DTO
 */
class ReviewStatsResponse {
    
    private Long targetId;
    private String targetName;
    private BigDecimal averageRating;
    private Long totalReviews;
    private BigDecimal minRating;
    private BigDecimal maxRating;
    private List<Map<String, Object>> ratingDistribution;
    
    // Constructors
    public ReviewStatsResponse() {}
    
    // Getters and Setters
    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }
    
    public String getTargetName() { return targetName; }
    public void setTargetName(String targetName) { this.targetName = targetName; }
    
    public BigDecimal getAverageRating() { return averageRating; }
    public void setAverageRating(BigDecimal averageRating) { this.averageRating = averageRating; }
    
    public Long getTotalReviews() { return totalReviews; }
    public void setTotalReviews(Long totalReviews) { this.totalReviews = totalReviews; }
    
    public BigDecimal getMinRating() { return minRating; }
    public void setMinRating(BigDecimal minRating) { this.minRating = minRating; }
    
    public BigDecimal getMaxRating() { return maxRating; }
    public void setMaxRating(BigDecimal maxRating) { this.maxRating = maxRating; }
    
    public List<Map<String, Object>> getRatingDistribution() { return ratingDistribution; }
    public void setRatingDistribution(List<Map<String, Object>> ratingDistribution) { this.ratingDistribution = ratingDistribution; }
}
