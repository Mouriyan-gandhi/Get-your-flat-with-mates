package com.roommateai.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * AI Search Request DTO
 */
public class AiSearchRequest {
    
    private String query;
    private String userCollege;
    
    // Constructors
    public AiSearchRequest() {}
    
    public AiSearchRequest(String query, String userCollege) {
        this.query = query;
        this.userCollege = userCollege;
    }
    
    // Getters and Setters
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    
    public String getUserCollege() { return userCollege; }
    public void setUserCollege(String userCollege) { this.userCollege = userCollege; }
}

/**
 * AI Search Response DTO
 */
class AiSearchResponse {
    
    private String college;
    private BigDecimal maxPrice;
    private BigDecimal minPrice;
    private List<String> amenities;
    private Integer distance;
    private String propertyType;
    private String roomType;
    private String city;
    private String state;
    
    // Constructors
    public AiSearchResponse() {}
    
    // Getters and Setters
    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }
    
    public BigDecimal getMaxPrice() { return maxPrice; }
    public void setMaxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; }
    
    public BigDecimal getMinPrice() { return minPrice; }
    public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }
    
    public List<String> getAmenities() { return amenities; }
    public void setAmenities(List<String> amenities) { this.amenities = amenities; }
    
    public Integer getDistance() { return distance; }
    public void setDistance(Integer distance) { this.distance = distance; }
    
    public String getPropertyType() { return propertyType; }
    public void setPropertyType(String propertyType) { this.propertyType = propertyType; }
    
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
}
