package com.roommateai.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Rent Split Request DTO
 */
public class RentSplitRequest {
    
    @NotNull(message = "Rental ID is required")
    private Long rentalId;
    
    @NotNull(message = "Total rent is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total rent must be greater than 0")
    private BigDecimal totalRent;
    
    @DecimalMin(value = "0.0", message = "Utilities must be non-negative")
    private BigDecimal utilities = BigDecimal.ZERO;
    
    @NotNull(message = "Members count is required")
    @Min(value = 1, message = "Members count must be at least 1")
    private Integer membersCount;
    
    private String splitDetailsJson;
    
    // Constructors
    public RentSplitRequest() {}
    
    public RentSplitRequest(Long rentalId, BigDecimal totalRent, BigDecimal utilities, Integer membersCount) {
        this.rentalId = rentalId;
        this.totalRent = totalRent;
        this.utilities = utilities;
        this.membersCount = membersCount;
    }
    
    // Getters and Setters
    public Long getRentalId() { return rentalId; }
    public void setRentalId(Long rentalId) { this.rentalId = rentalId; }
    
    public BigDecimal getTotalRent() { return totalRent; }
    public void setTotalRent(BigDecimal totalRent) { this.totalRent = totalRent; }
    
    public BigDecimal getUtilities() { return utilities; }
    public void setUtilities(BigDecimal utilities) { this.utilities = utilities; }
    
    public Integer getMembersCount() { return membersCount; }
    public void setMembersCount(Integer membersCount) { this.membersCount = membersCount; }
    
    public String getSplitDetailsJson() { return splitDetailsJson; }
    public void setSplitDetailsJson(String splitDetailsJson) { this.splitDetailsJson = splitDetailsJson; }
}

/**
 * Rent Split Response DTO
 */
class RentSplitResponse {
    
    private Long id;
    private Long rentalId;
    private String rentalTitle;
    private BigDecimal totalRent;
    private BigDecimal utilities;
    private Integer membersCount;
    private BigDecimal perPerson;
    private String splitDetailsJson;
    private String createdByName;
    private String createdAt;
    private String updatedAt;
    
    // Constructors
    public RentSplitResponse() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getRentalId() { return rentalId; }
    public void setRentalId(Long rentalId) { this.rentalId = rentalId; }
    
    public String getRentalTitle() { return rentalTitle; }
    public void setRentalTitle(String rentalTitle) { this.rentalTitle = rentalTitle; }
    
    public BigDecimal getTotalRent() { return totalRent; }
    public void setTotalRent(BigDecimal totalRent) { this.totalRent = totalRent; }
    
    public BigDecimal getUtilities() { return utilities; }
    public void setUtilities(BigDecimal utilities) { this.utilities = utilities; }
    
    public Integer getMembersCount() { return membersCount; }
    public void setMembersCount(Integer membersCount) { this.membersCount = membersCount; }
    
    public BigDecimal getPerPerson() { return perPerson; }
    public void setPerPerson(BigDecimal perPerson) { this.perPerson = perPerson; }
    
    public String getSplitDetailsJson() { return splitDetailsJson; }
    public void setSplitDetailsJson(String splitDetailsJson) { this.splitDetailsJson = splitDetailsJson; }
    
    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}

/**
 * Rent Split Calculation Request DTO
 */
class RentSplitCalculationRequest {
    
    @NotNull(message = "Total rent is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total rent must be greater than 0")
    private BigDecimal totalRent;
    
    @DecimalMin(value = "0.0", message = "Utilities must be non-negative")
    private BigDecimal utilities = BigDecimal.ZERO;
    
    @NotNull(message = "Members count is required")
    @Min(value = 1, message = "Members count must be at least 1")
    private Integer membersCount;
    
    private List<Map<String, Object>> customSplits;
    
    // Constructors
    public RentSplitCalculationRequest() {}
    
    // Getters and Setters
    public BigDecimal getTotalRent() { return totalRent; }
    public void setTotalRent(BigDecimal totalRent) { this.totalRent = totalRent; }
    
    public BigDecimal getUtilities() { return utilities; }
    public void setUtilities(BigDecimal utilities) { this.utilities = utilities; }
    
    public Integer getMembersCount() { return membersCount; }
    public void setMembersCount(Integer membersCount) { this.membersCount = membersCount; }
    
    public List<Map<String, Object>> getCustomSplits() { return customSplits; }
    public void setCustomSplits(List<Map<String, Object>> customSplits) { this.customSplits = customSplits; }
}

/**
 * Rent Split Calculation Response DTO
 */
class RentSplitCalculationResponse {
    
    private BigDecimal totalAmount;
    private BigDecimal perPersonAmount;
    private Integer membersCount;
    private List<Map<String, Object>> splitBreakdown;
    private String calculationMethod;
    
    // Constructors
    public RentSplitCalculationResponse() {}
    
    // Getters and Setters
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public BigDecimal getPerPersonAmount() { return perPersonAmount; }
    public void setPerPersonAmount(BigDecimal perPersonAmount) { this.perPersonAmount = perPersonAmount; }
    
    public Integer getMembersCount() { return membersCount; }
    public void setMembersCount(Integer membersCount) { this.membersCount = membersCount; }
    
    public List<Map<String, Object>> getSplitBreakdown() { return splitBreakdown; }
    public void setSplitBreakdown(List<Map<String, Object>> splitBreakdown) { this.splitBreakdown = splitBreakdown; }
    
    public String getCalculationMethod() { return calculationMethod; }
    public void setCalculationMethod(String calculationMethod) { this.calculationMethod = calculationMethod; }
}
