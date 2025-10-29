package com.roommateai.service;

import com.roommateai.dto.RentalRequest;
import com.roommateai.dto.RentalResponse;
import com.roommateai.model.Rental;
import com.roommateai.model.User;
import com.roommateai.repository.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Rental Service
 * Business logic for rental management
 */
@Service
public class RentalService {

    @Autowired
    private RentalRepository rentalRepository;

    /**
     * Create a new rental
     */
    public RentalResponse createRental(RentalRequest rentalRequest, User owner) {
        Rental rental = new Rental();
        rental.setOwner(owner);
        rental.setTitle(rentalRequest.getTitle());
        rental.setDescription(rentalRequest.getDescription());
        rental.setRent(rentalRequest.getRent());
        rental.setDeposit(rentalRequest.getDeposit());
        rental.setAddress(rentalRequest.getAddress());
        rental.setCity(rentalRequest.getCity());
        rental.setState(rentalRequest.getState());
        rental.setPincode(rentalRequest.getPincode());
        rental.setLatitude(rentalRequest.getLatitude());
        rental.setLongitude(rentalRequest.getLongitude());
        rental.setAmenitiesJson(rentalRequest.getAmenitiesJson());
        rental.setImagesJson(rentalRequest.getImagesJson());
        rental.setPropertyType(Rental.PropertyType.valueOf(rentalRequest.getPropertyType()));
        rental.setRoomType(Rental.RoomType.valueOf(rentalRequest.getRoomType()));
        rental.setAvailableFrom(rentalRequest.getAvailableFrom());
        rental.setAvailableUntil(rentalRequest.getAvailableUntil());
        rental.setIsAvailable(true);
        rental.setIsVerified(false); // Admin verification required

        Rental savedRental = rentalRepository.save(rental);
        return convertToResponse(savedRental);
    }

    /**
     * Get all available rentals
     */
    public List<RentalResponse> getAllAvailableRentals() {
        List<Rental> rentals = rentalRepository.findByIsAvailable(true);
        return rentals.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get rental by ID
     */
    public Optional<RentalResponse> getRentalById(Long id) {
        return rentalRepository.findById(id)
                .map(this::convertToResponse);
    }

    /**
     * Get rentals by owner
     */
    public List<RentalResponse> getRentalsByOwner(User owner) {
        List<Rental> rentals = rentalRepository.findByOwnerId(owner.getId());
        return rentals.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search rentals by city
     */
    public List<RentalResponse> searchRentalsByCity(String city) {
        List<Rental> rentals = rentalRepository.findByCity(city);
        return rentals.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search rentals by price range
     */
    public List<RentalResponse> searchRentalsByPriceRange(BigDecimal minRent, BigDecimal maxRent) {
        List<Rental> rentals = rentalRepository.findByRentBetween(minRent, maxRent);
        return rentals.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search rentals by multiple criteria
     */
    public List<RentalResponse> searchRentalsByCriteria(String city, String state, 
                                                        BigDecimal minRent, BigDecimal maxRent,
                                                        String propertyType, String roomType) {
        Rental.PropertyType propType = propertyType != null ? 
                Rental.PropertyType.valueOf(propertyType) : null;
        Rental.RoomType roomTypeEnum = roomType != null ? 
                Rental.RoomType.valueOf(roomType) : null;

        List<Rental> rentals = rentalRepository.findByMultipleCriteria(
                city, state, minRent, maxRent, propType, roomTypeEnum);
        
        return rentals.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search rentals by text
     */
    public List<RentalResponse> searchRentalsByText(String searchTerm) {
        List<Rental> rentals = rentalRepository.searchByTitleOrDescription(searchTerm);
        return rentals.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Find nearby rentals
     */
    public List<RentalResponse> findNearbyRentals(BigDecimal latitude, BigDecimal longitude, Double radiusKm) {
        List<Rental> rentals = rentalRepository.findNearbyRentals(latitude, longitude, radiusKm);
        return rentals.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update rental
     */
    public Optional<RentalResponse> updateRental(Long id, RentalRequest rentalRequest, User owner) {
        Optional<Rental> optionalRental = rentalRepository.findById(id);
        
        if (optionalRental.isPresent()) {
            Rental rental = optionalRental.get();
            
            // Check if user owns this rental or is admin
            if (!rental.getOwner().getId().equals(owner.getId()) && 
                !owner.getRole().equals(User.UserRole.ADMIN)) {
                return Optional.empty();
            }

            // Update fields
            rental.setTitle(rentalRequest.getTitle());
            rental.setDescription(rentalRequest.getDescription());
            rental.setRent(rentalRequest.getRent());
            rental.setDeposit(rentalRequest.getDeposit());
            rental.setAddress(rentalRequest.getAddress());
            rental.setCity(rentalRequest.getCity());
            rental.setState(rentalRequest.getState());
            rental.setPincode(rentalRequest.getPincode());
            rental.setLatitude(rentalRequest.getLatitude());
            rental.setLongitude(rentalRequest.getLongitude());
            rental.setAmenitiesJson(rentalRequest.getAmenitiesJson());
            rental.setImagesJson(rentalRequest.getImagesJson());
            rental.setPropertyType(Rental.PropertyType.valueOf(rentalRequest.getPropertyType()));
            rental.setRoomType(Rental.RoomType.valueOf(rentalRequest.getRoomType()));
            rental.setAvailableFrom(rentalRequest.getAvailableFrom());
            rental.setAvailableUntil(rentalRequest.getAvailableUntil());

            Rental updatedRental = rentalRepository.save(rental);
            return Optional.of(convertToResponse(updatedRental));
        }
        
        return Optional.empty();
    }

    /**
     * Delete rental
     */
    public boolean deleteRental(Long id, User owner) {
        Optional<Rental> optionalRental = rentalRepository.findById(id);
        
        if (optionalRental.isPresent()) {
            Rental rental = optionalRental.get();
            
            // Check if user owns this rental or is admin
            if (rental.getOwner().getId().equals(owner.getId()) || 
                owner.getRole().equals(User.UserRole.ADMIN)) {
                rentalRepository.delete(rental);
                return true;
            }
        }
        
        return false;
    }

    /**
     * Verify rental (Admin only)
     */
    public boolean verifyRental(Long id) {
        Optional<Rental> optionalRental = rentalRepository.findById(id);
        
        if (optionalRental.isPresent()) {
            Rental rental = optionalRental.get();
            rental.setIsVerified(true);
            rentalRepository.save(rental);
            return true;
        }
        
        return false;
    }

    /**
     * Convert Rental entity to RentalResponse DTO
     */
    private RentalResponse convertToResponse(Rental rental) {
        RentalResponse response = new RentalResponse();
        response.setId(rental.getId());
        response.setOwnerId(rental.getOwner().getId());
        response.setOwnerName(rental.getOwner().getName());
        response.setTitle(rental.getTitle());
        response.setDescription(rental.getDescription());
        response.setRent(rental.getRent());
        response.setDeposit(rental.getDeposit());
        response.setAddress(rental.getAddress());
        response.setCity(rental.getCity());
        response.setState(rental.getState());
        response.setPincode(rental.getPincode());
        response.setLatitude(rental.getLatitude());
        response.setLongitude(rental.getLongitude());
        response.setAmenitiesJson(rental.getAmenitiesJson());
        response.setImagesJson(rental.getImagesJson());
        response.setPropertyType(rental.getPropertyType().name());
        response.setRoomType(rental.getRoomType().name());
        response.setAvailableFrom(rental.getAvailableFrom());
        response.setAvailableUntil(rental.getAvailableUntil());
        response.setIsAvailable(rental.getIsAvailable());
        response.setIsVerified(rental.getIsVerified());
        response.setCreatedAt(rental.getCreatedAt());
        response.setUpdatedAt(rental.getUpdatedAt());
        
        return response;
    }
}
