package com.roommateai.repository;

import com.roommateai.model.RentSplit;
import com.roommateai.model.Rental;
import com.roommateai.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Rent Split Repository
 * Data access layer for RentSplit entity
 */
@Repository
public interface RentSplitRepository extends JpaRepository<RentSplit, Long> {
    
    /**
     * Find rent splits by rental
     */
    List<RentSplit> findByRentalId(Long rentalId);
    
    /**
     * Find rent splits by rental
     */
    List<RentSplit> findByRental(Rental rental);
    
    /**
     * Find rent splits by creator
     */
    List<RentSplit> findByCreatedById(Long createdById);
    
    /**
     * Find rent splits by creator
     */
    List<RentSplit> findByCreatedBy(User createdBy);
    
    /**
     * Find active rent splits for a rental
     */
    @Query("SELECT rs FROM RentSplit rs WHERE rs.rental.id = :rentalId ORDER BY rs.createdAt DESC")
    List<RentSplit> findActiveRentSplitsByRental(@Param("rentalId") Long rentalId);
    
    /**
     * Find latest rent split for a rental
     */
    @Query("SELECT rs FROM RentSplit rs WHERE rs.rental.id = :rentalId ORDER BY rs.createdAt DESC LIMIT 1")
    RentSplit findLatestRentSplitByRental(@Param("rentalId") Long rentalId);
    
    /**
     * Find rent splits by members count
     */
    List<RentSplit> findByMembersCount(Integer membersCount);
    
    /**
     * Find rent splits within price range
     */
    @Query("SELECT rs FROM RentSplit rs WHERE rs.perPerson BETWEEN :minAmount AND :maxAmount")
    List<RentSplit> findByPerPersonBetween(@Param("minAmount") Double minAmount, @Param("maxAmount") Double maxAmount);
    
    /**
     * Count rent splits by rental
     */
    Long countByRentalId(Long rentalId);
    
    /**
     * Count rent splits by creator
     */
    Long countByCreatedById(Long createdById);
}
