package com.roommateai.repository;

import com.roommateai.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * User Repository
 * Data access layer for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Find users by college
     */
    List<User> findByCollege(String college);
    
    /**
     * Find active users by college
     */
    List<User> findByCollegeAndIsActive(String college, Boolean isActive);
    
    /**
     * Find users by role
     */
    List<User> findByRole(User.UserRole role);
    
    /**
     * Find active users
     */
    List<User> findByIsActive(Boolean isActive);
    
    /**
     * Search users by name (case insensitive)
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%')) AND u.isActive = true")
    List<User> findByNameContainingIgnoreCase(@Param("name") String name);
    
    /**
     * Find users by college and name search
     */
    @Query("SELECT u FROM User u WHERE u.college = :college AND LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%')) AND u.isActive = true")
    List<User> findByCollegeAndNameContainingIgnoreCase(@Param("college") String college, @Param("name") String name);
}
