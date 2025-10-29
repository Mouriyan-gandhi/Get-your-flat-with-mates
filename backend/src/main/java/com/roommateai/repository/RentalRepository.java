package com.roommateai.repository;

import com.roommateai.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Rental Repository
 * Data access layer for Rental entity
 */
@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    
    /**
     * Find rentals by owner
     */
    List<Rental> findByOwnerId(Long ownerId);
    
    /**
     * Find available rentals
     */
    List<Rental> findByIsAvailable(Boolean isAvailable);
    
    /**
     * Find rentals by city
     */
    List<Rental> findByCity(String city);
    
    /**
     * Find rentals by city and state
     */
    List<Rental> findByCityAndState(String city, String state);
    
    /**
     * Find rentals by property type
     */
    List<Rental> findByPropertyType(Rental.PropertyType propertyType);
    
    /**
     * Find rentals by room type
     */
    List<Rental> findByRoomType(Rental.RoomType roomType);
    
    /**
     * Find rentals within price range
     */
    List<Rental> findByRentBetween(BigDecimal minRent, BigDecimal maxRent);
    
    /**
     * Find verified rentals
     */
    List<Rental> findByIsVerified(Boolean isVerified);
    
    /**
     * Search rentals by title or description
     */
    @Query("SELECT r FROM Rental r WHERE " +
           "(LOWER(r.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "r.isAvailable = true")
    List<Rental> searchByTitleOrDescription(@Param("searchTerm") String searchTerm);
    
    /**
     * Find rentals near coordinates (within radius)
     */
    @Query("SELECT r FROM Rental r WHERE " +
           "r.latitude IS NOT NULL AND r.longitude IS NOT NULL AND " +
           "r.isAvailable = true AND " +
           "(6371 * acos(cos(radians(:lat)) * cos(radians(r.latitude)) * " +
           "cos(radians(r.longitude) - radians(:lng)) + " +
           "sin(radians(:lat)) * sin(radians(r.latitude)))) <= :radius")
    List<Rental> findNearbyRentals(@Param("lat") BigDecimal latitude, 
                                  @Param("lng") BigDecimal longitude, 
                                  @Param("radius") Double radiusKm);
    
    /**
     * Find rentals by multiple criteria
     */
    @Query("SELECT r FROM Rental r WHERE " +
           "(:city IS NULL OR r.city = :city) AND " +
           "(:state IS NULL OR r.state = :state) AND " +
           "(:minRent IS NULL OR r.rent >= :minRent) AND " +
           "(:maxRent IS NULL OR r.rent <= :maxRent) AND " +
           "(:propertyType IS NULL OR r.propertyType = :propertyType) AND " +
           "(:roomType IS NULL OR r.roomType = :roomType) AND " +
           "r.isAvailable = true")
    List<Rental> findByMultipleCriteria(@Param("city") String city,
                                        @Param("state") String state,
                                        @Param("minRent") BigDecimal minRent,
                                        @Param("maxRent") BigDecimal maxRent,
                                        @Param("propertyType") Rental.PropertyType propertyType,
                                        @Param("roomType") Rental.RoomType roomType);
}
