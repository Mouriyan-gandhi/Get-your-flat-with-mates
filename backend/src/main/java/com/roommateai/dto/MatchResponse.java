package com.roommateai.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Match Response DTO
 */
public class MatchResponse {
    
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private String userCollege;
    private String userBio;
    private String userProfileImageUrl;
    private BigDecimal compatibilityScore;
    private String status;
    private LocalDateTime matchedAt;
    private LocalDateTime createdAt;
    private String preferencesJson;
    
    // Constructors
    public MatchResponse() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    
    public String getUserCollege() { return userCollege; }
    public void setUserCollege(String userCollege) { this.userCollege = userCollege; }
    
    public String getUserBio() { return userBio; }
    public void setUserBio(String userBio) { this.userBio = userBio; }
    
    public String getUserProfileImageUrl() { return userProfileImageUrl; }
    public void setUserProfileImageUrl(String userProfileImageUrl) { this.userProfileImageUrl = userProfileImageUrl; }
    
    public BigDecimal getCompatibilityScore() { return compatibilityScore; }
    public void setCompatibilityScore(BigDecimal compatibilityScore) { this.compatibilityScore = compatibilityScore; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getMatchedAt() { return matchedAt; }
    public void setMatchedAt(LocalDateTime matchedAt) { this.matchedAt = matchedAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getPreferencesJson() { return preferencesJson; }
    public void setPreferencesJson(String preferencesJson) { this.preferencesJson = preferencesJson; }
}

/**
 * Like Request DTO
 */
class LikeRequest {
    
    private Long targetUserId;
    
    // Constructors
    public LikeRequest() {}
    
    public LikeRequest(Long targetUserId) {
        this.targetUserId = targetUserId;
    }
    
    // Getters and Setters
    public Long getTargetUserId() { return targetUserId; }
    public void setTargetUserId(Long targetUserId) { this.targetUserId = targetUserId; }
}

/**
 * Pass Request DTO
 */
class PassRequest {
    
    private Long targetUserId;
    
    // Constructors
    public PassRequest() {}
    
    public PassRequest(Long targetUserId) {
        this.targetUserId = targetUserId;
    }
    
    // Getters and Setters
    public Long getTargetUserId() { return targetUserId; }
    public void setTargetUserId(Long targetUserId) { this.targetUserId = targetUserId; }
}
