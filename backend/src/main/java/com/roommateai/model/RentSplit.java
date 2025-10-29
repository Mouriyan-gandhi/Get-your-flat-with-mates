package com.roommateai.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * RentSplit entity for automatic bill division among roommates
 * Calculates per-person cost including utilities
 */
@Entity
@Table(name = "rent_splits")
@EntityListeners(AuditingEntityListener.class)
public class RentSplit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Rental is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_id", nullable = false)
    private Rental rental;
    
    @NotNull(message = "Total rent is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total rent must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalRent;
    
    @DecimalMin(value = "0.0", message = "Utilities must be non-negative")
    @Column(precision = 10, scale = 2)
    private BigDecimal utilities = BigDecimal.ZERO;
    
    @NotNull(message = "Members count is required")
    @Min(value = 1, message = "Members count must be at least 1")
    @Column(nullable = false)
    private Integer membersCount;
    
    @NotNull(message = "Per person amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Per person amount must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal perPerson;
    
    @Column(columnDefinition = "JSON")
    private String splitDetailsJson;
    
    @NotNull(message = "Created by is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructors
    public RentSplit() {}
    
    public RentSplit(Rental rental, BigDecimal totalRent, BigDecimal utilities, 
                     Integer membersCount, User createdBy) {
        this.rental = rental;
        this.totalRent = totalRent;
        this.utilities = utilities;
        this.membersCount = membersCount;
        this.createdBy = createdBy;
        this.perPerson = calculatePerPerson();
    }
    
    // Business logic method
    public BigDecimal calculatePerPerson() {
        if (membersCount == null || membersCount <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal totalAmount = totalRent.add(utilities != null ? utilities : BigDecimal.ZERO);
        return totalAmount.divide(BigDecimal.valueOf(membersCount), 2, BigDecimal.ROUND_HALF_UP);
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Rental getRental() { return rental; }
    public void setRental(Rental rental) { this.rental = rental; }
    
    public BigDecimal getTotalRent() { return totalRent; }
    public void setTotalRent(BigDecimal totalRent) { 
        this.totalRent = totalRent;
        this.perPerson = calculatePerPerson();
    }
    
    public BigDecimal getUtilities() { return utilities; }
    public void setUtilities(BigDecimal utilities) { 
        this.utilities = utilities;
        this.perPerson = calculatePerPerson();
    }
    
    public Integer getMembersCount() { return membersCount; }
    public void setMembersCount(Integer membersCount) { 
        this.membersCount = membersCount;
        this.perPerson = calculatePerPerson();
    }
    
    public BigDecimal getPerPerson() { return perPerson; }
    public void setPerPerson(BigDecimal perPerson) { this.perPerson = perPerson; }
    
    public String getSplitDetailsJson() { return splitDetailsJson; }
    public void setSplitDetailsJson(String splitDetailsJson) { this.splitDetailsJson = splitDetailsJson; }
    
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @Override
    public String toString() {
        return "RentSplit{" +
                "id=" + id +
                ", totalRent=" + totalRent +
                ", utilities=" + utilities +
                ", membersCount=" + membersCount +
                ", perPerson=" + perPerson +
                '}';
    }
}
