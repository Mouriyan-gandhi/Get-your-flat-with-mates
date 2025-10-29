package com.roommateai.repository;

import com.roommateai.model.Match;
import com.roommateai.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Match Repository
 * Data access layer for Match entity
 */
@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    
    /**
     * Find matches by user1
     */
    List<Match> findByUser1Id(Long user1Id);
    
    /**
     * Find matches by user2
     */
    List<Match> findByUser2Id(Long user2Id);
    
    /**
     * Find matches by user (either as user1 or user2)
     */
    @Query("SELECT m FROM Match m WHERE m.user1.id = :userId OR m.user2.id = :userId")
    List<Match> findByUserId(@Param("userId") Long userId);
    
    /**
     * Find match between two specific users
     */
    @Query("SELECT m FROM Match m WHERE " +
           "(m.user1.id = :user1Id AND m.user2.id = :user2Id) OR " +
           "(m.user1.id = :user2Id AND m.user2.id = :user1Id)")
    Optional<Match> findMatchBetweenUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);
    
    /**
     * Find matches by status
     */
    List<Match> findByStatus(Match.MatchStatus status);
    
    /**
     * Find matches by user and status
     */
    @Query("SELECT m FROM Match m WHERE " +
           "(m.user1.id = :userId OR m.user2.id = :userId) AND m.status = :status")
    List<Match> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Match.MatchStatus status);
    
    /**
     * Find pending matches for a user
     */
    @Query("SELECT m FROM Match m WHERE " +
           "(m.user1.id = :userId OR m.user2.id = :userId) AND m.status = 'PENDING'")
    List<Match> findPendingMatchesForUser(@Param("userId") Long userId);
    
    /**
     * Find liked matches for a user
     */
    @Query("SELECT m FROM Match m WHERE " +
           "(m.user1.id = :userId OR m.user2.id = :userId) AND m.status = 'LIKED'")
    List<Match> findLikedMatchesForUser(@Param("userId") Long userId);
    
    /**
     * Find matched pairs for a user
     */
    @Query("SELECT m FROM Match m WHERE " +
           "(m.user1.id = :userId OR m.user2.id = :userId) AND m.status = 'MATCHED'")
    List<Match> findMatchedPairsForUser(@Param("userId") Long userId);
    
    /**
     * Find top compatible users for matching
     */
    @Query("SELECT u FROM User u WHERE u.id != :userId AND u.isActive = true AND u.role = 'STUDENT' " +
           "AND u.id NOT IN (SELECT CASE WHEN m.user1.id = :userId THEN m.user2.id ELSE m.user1.id END " +
           "FROM Match m WHERE m.user1.id = :userId OR m.user2.id = :userId) " +
           "ORDER BY u.createdAt DESC")
    List<User> findPotentialMatches(@Param("userId") Long userId);
    
    /**
     * Count matches by status for a user
     */
    @Query("SELECT COUNT(m) FROM Match m WHERE " +
           "(m.user1.id = :userId OR m.user2.id = :userId) AND m.status = :status")
    Long countMatchesByStatus(@Param("userId") Long userId, @Param("status") Match.MatchStatus status);
}
