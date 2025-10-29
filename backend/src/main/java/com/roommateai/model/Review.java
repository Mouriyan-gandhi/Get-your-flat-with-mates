package com.roommateai.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Review entity for rating landlords and roommates
 * Students can rate their experience after staying together
 */
@Entity
@Table(name = "reviews")
@EntityListeners(AuditingEntityListener.class)
public class Review {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Reviewer is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;
    
    @NotNull(message = "Target is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", nullable = false)
    private User target;
    
    @NotNull(message = "Target type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewTargetType targetType;
    
    @NotNull(message = "Rating is required")
    @DecimalMin(value = "1.0", message = "Rating must be at least 1.0")
    @DecimalMax(value = "5.0", message = "Rating must not exceed 5.0")
    @Column(nullable = false, precision = 2, scale = 1)
    private BigDecimal rating;
    
    @Column(columnDefinition = "TEXT")
    private String comment;
    
    @Column(nullable = false)
    private Boolean isVerified = false;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructors
    public Review() {}
    
    public Review(User reviewer, User target, ReviewTargetType targetType, BigDecimal rating, String comment) {
        this.reviewer = reviewer;
        this.target = target;
        this.targetType = targetType;
        this.rating = rating;
        this.comment = comment;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getReviewer() { return reviewer; }
    public void setReviewer(User reviewer) { this.reviewer = reviewer; }
    
    public User getTarget() { return target; }
    public void setTarget(User target) { this.target = target; }
    
    public ReviewTargetType getTargetType() { return targetType; }
    public void setTargetType(ReviewTargetType targetType) { this.targetType = targetType; }
    
    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    
    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", reviewer=" + (reviewer != null ? reviewer.getName() : "null") +
                ", target=" + (target != null ? target.getName() : "null") +
                ", targetType=" + targetType +
                ", rating=" + rating +
                '}';
    }
}

/**
 * Review target type enum
 */
enum ReviewTargetType {
    USER, RENTAL
}
