package com.roommateai.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Rental entity representing property listings
 * Students can post and search for rentals near their college
 */
@Entity
@Table(name = "rentals")
@EntityListeners(AuditingEntityListener.class)
public class Rental {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Owner is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    @Column(nullable = false)
    private String title;
    
    @NotBlank(message = "Description is required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;
    
    @NotNull(message = "Rent amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Rent must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal rent;
    
    @DecimalMin(value = "0.0", message = "Deposit must be non-negative")
    @Column(precision = 10, scale = 2)
    private BigDecimal deposit;
    
    @NotBlank(message = "Address is required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String address;
    
    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City name must not exceed 50 characters")
    @Column(nullable = false)
    private String city;
    
    @NotBlank(message = "State is required")
    @Size(max = 50, message = "State name must not exceed 50 characters")
    @Column(nullable = false)
    private String state;
    
    @Size(max = 10, message = "Pincode must not exceed 10 characters")
    private String pincode;
    
    @DecimalMin(value = "-90.0", message = "Invalid latitude")
    @DecimalMax(value = "90.0", message = "Invalid latitude")
    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;
    
    @DecimalMin(value = "-180.0", message = "Invalid longitude")
    @DecimalMax(value = "180.0", message = "Invalid longitude")
    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;
    
    @Column(columnDefinition = "JSON")
    private String amenitiesJson;
    
    @Column(columnDefinition = "JSON")
    private String imagesJson;
    
    @NotNull(message = "Property type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyType propertyType;
    
    @NotNull(message = "Room type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType roomType;
    
    private LocalDate availableFrom;
    
    private LocalDate availableUntil;
    
    @Column(nullable = false)
    private Boolean isAvailable = true;
    
    @Column(nullable = false)
    private Boolean isVerified = false;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // Relationships
    @OneToMany(mappedBy = "rental", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RentSplit> rentSplits;
    
    @OneToMany(mappedBy = "target", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews;
    
    // Constructors
    public Rental() {}
    
    public Rental(User owner, String title, String description, BigDecimal rent, 
                  String address, String city, String state, PropertyType propertyType, RoomType roomType) {
        this.owner = owner;
        this.title = title;
        this.description = description;
        this.rent = rent;
        this.address = address;
        this.city = city;
        this.state = state;
        this.propertyType = propertyType;
        this.roomType = roomType;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getRent() { return rent; }
    public void setRent(BigDecimal rent) { this.rent = rent; }
    
    public BigDecimal getDeposit() { return deposit; }
    public void setDeposit(BigDecimal deposit) { this.deposit = deposit; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }
    
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    
    public String getAmenitiesJson() { return amenitiesJson; }
    public void setAmenitiesJson(String amenitiesJson) { this.amenitiesJson = amenitiesJson; }
    
    public String getImagesJson() { return imagesJson; }
    public void setImagesJson(String imagesJson) { this.imagesJson = imagesJson; }
    
    public PropertyType getPropertyType() { return propertyType; }
    public void setPropertyType(PropertyType propertyType) { this.propertyType = propertyType; }
    
    public RoomType getRoomType() { return roomType; }
    public void setRoomType(RoomType roomType) { this.roomType = roomType; }
    
    public LocalDate getAvailableFrom() { return availableFrom; }
    public void setAvailableFrom(LocalDate availableFrom) { this.availableFrom = availableFrom; }
    
    public LocalDate getAvailableUntil() { return availableUntil; }
    public void setAvailableUntil(LocalDate availableUntil) { this.availableUntil = availableUntil; }
    
    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }
    
    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Relationship getters and setters
    public List<RentSplit> getRentSplits() { return rentSplits; }
    public void setRentSplits(List<RentSplit> rentSplits) { this.rentSplits = rentSplits; }
    
    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }
    
    @Override
    public String toString() {
        return "Rental{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", rent=" + rent +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", propertyType=" + propertyType +
                ", roomType=" + roomType +
                '}';
    }
}

/**
 * Property type enum
 */
enum PropertyType {
    HOSTEL, PG, APARTMENT, HOUSE
}

/**
 * Room type enum
 */
enum RoomType {
    SINGLE, SHARED, DOUBLE, TRIPLE
}
