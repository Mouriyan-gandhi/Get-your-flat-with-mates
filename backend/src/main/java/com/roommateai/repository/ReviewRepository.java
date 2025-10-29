package com.roommateai.repository;

import com.roommateai.model.Review;
import com.roommateai.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Review Repository
 * Data access layer for Review entity
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    /**
     * Find reviews by reviewer
     */
    List<Review> findByReviewerId(Long reviewerId);
    
    /**
     * Find reviews by reviewer
     */
    List<Review> findByReviewer(User reviewer);
    
    /**
     * Find reviews by target
     */
    List<Review> findByTargetId(Long targetId);
    
    /**
     * Find reviews by target
     */
    List<Review> findByTarget(User target);
    
    /**
     * Find reviews by target type
     */
    List<Review> findByTargetType(Review.ReviewTargetType targetType);
    
    /**
     * Find reviews by target and target type
     */
    List<Review> findByTargetIdAndTargetType(Long targetId, Review.ReviewTargetType targetType);
    
    /**
     * Find reviews by rating range
     */
    List<Review> findByRatingBetween(BigDecimal minRating, BigDecimal maxRating);
    
    /**
     * Find reviews by rating
     */
    List<Review> findByRating(BigDecimal rating);
    
    /**
     * Find verified reviews
     */
    List<Review> findByIsVerified(Boolean isVerified);
    
    /**
     * Find reviews by reviewer and target
     */
    @Query("SELECT r FROM Review r WHERE r.reviewer.id = :reviewerId AND r.target.id = :targetId")
    List<Review> findByReviewerAndTarget(@Param("reviewerId") Long reviewerId, @Param("targetId") Long targetId);
    
    /**
     * Check if user has reviewed target
     */
    @Query("SELECT COUNT(r) > 0 FROM Review r WHERE r.reviewer.id = :reviewerId AND r.target.id = :targetId")
    boolean hasUserReviewedTarget(@Param("reviewerId") Long reviewerId, @Param("targetId") Long targetId);
    
    /**
     * Get average rating for a target
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.target.id = :targetId AND r.isVerified = true")
    BigDecimal getAverageRatingByTarget(@Param("targetId") Long targetId);
    
    /**
     * Get average rating for a target by type
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.target.id = :targetId AND r.targetType = :targetType AND r.isVerified = true")
    BigDecimal getAverageRatingByTargetAndType(@Param("targetId") Long targetId, @Param("targetType") Review.ReviewTargetType targetType);
    
    /**
     * Count reviews for a target
     */
    @Query("SELECT COUNT(r) FROM Review r WHERE r.target.id = :targetId AND r.isVerified = true")
    Long countReviewsByTarget(@Param("targetId") Long targetId);
    
    /**
     * Count reviews for a target by type
     */
    @Query("SELECT COUNT(r) FROM Review r WHERE r.target.id = :targetId AND r.targetType = :targetType AND r.isVerified = true")
    Long countReviewsByTargetAndType(@Param("targetId") Long targetId, @Param("targetType") Review.ReviewTargetType targetType);
    
    /**
     * Get rating distribution for a target
     */
    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.target.id = :targetId AND r.isVerified = true GROUP BY r.rating ORDER BY r.rating")
    List<Object[]> getRatingDistributionByTarget(@Param("targetId") Long targetId);
    
    /**
     * Find recent reviews
     */
    @Query("SELECT r FROM Review r WHERE r.isVerified = true ORDER BY r.createdAt DESC")
    List<Review> findRecentReviews();
    
    /**
     * Find recent reviews for a target
     */
    @Query("SELECT r FROM Review r WHERE r.target.id = :targetId AND r.isVerified = true ORDER BY r.createdAt DESC")
    List<Review> findRecentReviewsByTarget(@Param("targetId") Long targetId);
    
    /**
     * Find reviews by reviewer with pagination
     */
    @Query("SELECT r FROM Review r WHERE r.reviewer.id = :reviewerId ORDER BY r.createdAt DESC")
    List<Review> findReviewsByReviewerOrderByCreatedAtDesc(@Param("reviewerId") Long reviewerId);
    
    /**
     * Find reviews by target with pagination
     */
    @Query("SELECT r FROM Review r WHERE r.target.id = :targetId AND r.isVerified = true ORDER BY r.createdAt DESC")
    List<Review> findReviewsByTargetOrderByCreatedAtDesc(@Param("targetId") Long targetId);
    
    /**
     * Get top rated targets
     */
    @Query("SELECT r.target.id, AVG(r.rating) as avgRating, COUNT(r) as reviewCount " +
           "FROM Review r WHERE r.isVerified = true " +
           "GROUP BY r.target.id " +
           "HAVING COUNT(r) >= :minReviews " +
           "ORDER BY avgRating DESC, reviewCount DESC")
    List<Object[]> getTopRatedTargets(@Param("minReviews") Long minReviews);
    
    /**
     * Get review statistics for a user
     */
    @Query("SELECT COUNT(r), AVG(r.rating), MIN(r.rating), MAX(r.rating) " +
           "FROM Review r WHERE r.target.id = :targetId AND r.isVerified = true")
    Object[] getReviewStatsForTarget(@Param("targetId") Long targetId);
}
