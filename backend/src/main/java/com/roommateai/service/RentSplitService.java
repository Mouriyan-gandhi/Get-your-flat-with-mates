package com.roommateai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roommateai.dto.RentSplitCalculationRequest;
import com.roommateai.dto.RentSplitCalculationResponse;
import com.roommateai.dto.RentSplitRequest;
import com.roommateai.dto.RentSplitResponse;
import com.roommateai.model.RentSplit;
import com.roommateai.model.Rental;
import com.roommateai.model.User;
import com.roommateai.repository.RentSplitRepository;
import com.roommateai.repository.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Rent Split Service
 * Handles rent splitting calculations and management
 */
@Service
public class RentSplitService {

    @Autowired
    private RentSplitRepository rentSplitRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Calculate rent split without saving
     */
    public RentSplitCalculationResponse calculateRentSplit(RentSplitCalculationRequest request) {
        BigDecimal totalAmount = request.getTotalRent().add(request.getUtilities());
        BigDecimal perPersonAmount = totalAmount.divide(
            BigDecimal.valueOf(request.getMembersCount()), 2, RoundingMode.HALF_UP);

        RentSplitCalculationResponse response = new RentSplitCalculationResponse();
        response.setTotalAmount(totalAmount);
        response.setPerPersonAmount(perPersonAmount);
        response.setMembersCount(request.getMembersCount());
        response.setCalculationMethod("Equal Split");

        // Create split breakdown
        List<Map<String, Object>> breakdown = new ArrayList<>();
        for (int i = 1; i <= request.getMembersCount(); i++) {
            Map<String, Object> memberSplit = new HashMap<>();
            memberSplit.put("memberNumber", i);
            memberSplit.put("amount", perPersonAmount);
            memberSplit.put("percentage", BigDecimal.valueOf(100.0).divide(
                BigDecimal.valueOf(request.getMembersCount()), 2, RoundingMode.HALF_UP));
            breakdown.add(memberSplit);
        }

        response.setSplitBreakdown(breakdown);

        // Handle custom splits if provided
        if (request.getCustomSplits() != null && !request.getCustomSplits().isEmpty()) {
            response = calculateCustomSplit(request);
        }

        return response;
    }

    /**
     * Calculate custom rent split
     */
    private RentSplitCalculationResponse calculateCustomSplit(RentSplitCalculationRequest request) {
        BigDecimal totalAmount = request.getTotalRent().add(request.getUtilities());
        
        RentSplitCalculationResponse response = new RentSplitCalculationResponse();
        response.setTotalAmount(totalAmount);
        response.setMembersCount(request.getMembersCount());
        response.setCalculationMethod("Custom Split");

        List<Map<String, Object>> breakdown = new ArrayList<>();
        BigDecimal totalCustomAmount = BigDecimal.ZERO;

        for (Map<String, Object> customSplit : request.getCustomSplits()) {
            BigDecimal amount = new BigDecimal(customSplit.get("amount").toString());
            totalCustomAmount = totalCustomAmount.add(amount);
            
            Map<String, Object> memberSplit = new HashMap<>();
            memberSplit.put("memberNumber", customSplit.get("memberNumber"));
            memberSplit.put("amount", amount);
            memberSplit.put("percentage", amount.divide(totalAmount, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)));
            breakdown.add(memberSplit);
        }

        // Validate that custom splits equal total amount
        if (totalCustomAmount.compareTo(totalAmount) != 0) {
            throw new RuntimeException("Custom split amounts must equal total amount");
        }

        response.setSplitBreakdown(breakdown);
        response.setPerPersonAmount(totalAmount.divide(
            BigDecimal.valueOf(request.getMembersCount()), 2, RoundingMode.HALF_UP));

        return response;
    }

    /**
     * Create and save a rent split
     */
    public RentSplitResponse createRentSplit(RentSplitRequest request, User createdBy) {
        Rental rental = rentalRepository.findById(request.getRentalId()).orElse(null);
        if (rental == null) {
            throw new RuntimeException("Rental not found");
        }

        RentSplit rentSplit = new RentSplit();
        rentSplit.setRental(rental);
        rentSplit.setTotalRent(request.getTotalRent());
        rentSplit.setUtilities(request.getUtilities());
        rentSplit.setMembersCount(request.getMembersCount());
        rentSplit.setCreatedBy(createdBy);
        rentSplit.setSplitDetailsJson(request.getSplitDetailsJson());

        // Calculate per person amount
        rentSplit.setPerPerson(rentSplit.calculatePerPerson());

        RentSplit savedRentSplit = rentSplitRepository.save(rentSplit);
        return convertToResponse(savedRentSplit);
    }

    /**
     * Get rent splits for a rental
     */
    public List<RentSplitResponse> getRentSplitsByRental(Long rentalId) {
        List<RentSplit> rentSplits = rentSplitRepository.findByRentalId(rentalId);
        return rentSplits.stream()
                .map(this::convertToResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Get rent splits created by a user
     */
    public List<RentSplitResponse> getRentSplitsByUser(Long userId) {
        List<RentSplit> rentSplits = rentSplitRepository.findByCreatedById(userId);
        return rentSplits.stream()
                .map(this::convertToResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Get latest rent split for a rental
     */
    public Optional<RentSplitResponse> getLatestRentSplit(Long rentalId) {
        RentSplit rentSplit = rentSplitRepository.findLatestRentSplitByRental(rentalId);
        return rentSplit != null ? Optional.of(convertToResponse(rentSplit)) : Optional.empty();
    }

    /**
     * Update a rent split
     */
    public Optional<RentSplitResponse> updateRentSplit(Long id, RentSplitRequest request, User user) {
        Optional<RentSplit> optionalRentSplit = rentSplitRepository.findById(id);
        
        if (optionalRentSplit.isPresent()) {
            RentSplit rentSplit = optionalRentSplit.get();
            
            // Check if user can update this rent split
            if (!rentSplit.getCreatedBy().getId().equals(user.getId()) && 
                !user.getRole().equals(User.UserRole.ADMIN)) {
                return Optional.empty();
            }

            // Update fields
            rentSplit.setTotalRent(request.getTotalRent());
            rentSplit.setUtilities(request.getUtilities());
            rentSplit.setMembersCount(request.getMembersCount());
            rentSplit.setSplitDetailsJson(request.getSplitDetailsJson());

            RentSplit updatedRentSplit = rentSplitRepository.save(rentSplit);
            return Optional.of(convertToResponse(updatedRentSplit));
        }
        
        return Optional.empty();
    }

    /**
     * Delete a rent split
     */
    public boolean deleteRentSplit(Long id, User user) {
        Optional<RentSplit> optionalRentSplit = rentSplitRepository.findById(id);
        
        if (optionalRentSplit.isPresent()) {
            RentSplit rentSplit = optionalRentSplit.get();
            
            // Check if user can delete this rent split
            if (rentSplit.getCreatedBy().getId().equals(user.getId()) || 
                user.getRole().equals(User.UserRole.ADMIN)) {
                rentSplitRepository.delete(rentSplit);
                return true;
            }
        }
        
        return false;
    }

    /**
     * Get rent split statistics
     */
    public Map<String, Object> getRentSplitStats(Long userId) {
        List<RentSplit> userRentSplits = rentSplitRepository.findByCreatedById(userId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRentSplits", userRentSplits.size());
        
        if (!userRentSplits.isEmpty()) {
            BigDecimal totalAmount = userRentSplits.stream()
                    .map(rs -> rs.getTotalRent().add(rs.getUtilities()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal averagePerPerson = userRentSplits.stream()
                    .map(RentSplit::getPerPerson)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(userRentSplits.size()), 2, RoundingMode.HALF_UP);
            
            stats.put("totalAmountManaged", totalAmount);
            stats.put("averagePerPerson", averagePerPerson);
        }
        
        return stats;
    }

    /**
     * Convert RentSplit entity to RentSplitResponse DTO
     */
    private RentSplitResponse convertToResponse(RentSplit rentSplit) {
        RentSplitResponse response = new RentSplitResponse();
        response.setId(rentSplit.getId());
        response.setRentalId(rentSplit.getRental().getId());
        response.setRentalTitle(rentSplit.getRental().getTitle());
        response.setTotalRent(rentSplit.getTotalRent());
        response.setUtilities(rentSplit.getUtilities());
        response.setMembersCount(rentSplit.getMembersCount());
        response.setPerPerson(rentSplit.getPerPerson());
        response.setSplitDetailsJson(rentSplit.getSplitDetailsJson());
        response.setCreatedByName(rentSplit.getCreatedBy().getName());
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
        response.setCreatedAt(rentSplit.getCreatedAt().format(formatter));
        response.setUpdatedAt(rentSplit.getUpdatedAt().format(formatter));
        
        return response;
    }

    /**
     * Calculate rent split with different methods
     */
    public RentSplitCalculationResponse calculateRentSplitWithMethod(
            RentSplitCalculationRequest request, String method) {
        
        switch (method.toLowerCase()) {
            case "equal":
                return calculateRentSplit(request);
            case "proportional":
                return calculateProportionalSplit(request);
            case "room_size":
                return calculateRoomSizeSplit(request);
            default:
                return calculateRentSplit(request);
        }
    }

    /**
     * Calculate proportional split based on income
     */
    private RentSplitCalculationResponse calculateProportionalSplit(RentSplitCalculationRequest request) {
        // This would require income data from user preferences
        // For now, return equal split
        return calculateRentSplit(request);
    }

    /**
     * Calculate split based on room size
     */
    private RentSplitCalculationResponse calculateRoomSizeSplit(RentSplitCalculationRequest request) {
        // This would require room size data
        // For now, return equal split
        return calculateRentSplit(request);
    }
}
