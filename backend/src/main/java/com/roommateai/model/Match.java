package com.roommateai.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Match entity representing roommate compatibility scores
 * Uses weighted algorithm to calculate compatibility between students
 */
@Entity
@Table(name = "matches")
@EntityListeners(AuditingEntityListener.class)
public class Match {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "User1 is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1;
    
    @NotNull(message = "User2 is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;
    
    @NotNull(message = "Compatibility score is required")
    @DecimalMin(value = "0.0", message = "Score must be non-negative")
    @DecimalMax(value = "100.0", message = "Score must not exceed 100")
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal compatibilityScore;
    
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchStatus status = MatchStatus.PENDING;
    
    private LocalDateTime matchedAt;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // Relationships
    @OneToOne(mappedBy = "match", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ChatRoom chatRoom;
    
    // Constructors
    public Match() {}
    
    public Match(User user1, User user2, BigDecimal compatibilityScore) {
        this.user1 = user1;
        this.user2 = user2;
        this.compatibilityScore = compatibilityScore;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser1() { return user1; }
    public void setUser1(User user1) { this.user1 = user1; }
    
    public User getUser2() { return user2; }
    public void setUser2(User user2) { this.user2 = user2; }
    
    public BigDecimal getCompatibilityScore() { return compatibilityScore; }
    public void setCompatibilityScore(BigDecimal compatibilityScore) { this.compatibilityScore = compatibilityScore; }
    
    public MatchStatus getStatus() { return status; }
    public void setStatus(MatchStatus status) { this.status = status; }
    
    public LocalDateTime getMatchedAt() { return matchedAt; }
    public void setMatchedAt(LocalDateTime matchedAt) { this.matchedAt = matchedAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public ChatRoom getChatRoom() { return chatRoom; }
    public void setChatRoom(ChatRoom chatRoom) { this.chatRoom = chatRoom; }
    
    @Override
    public String toString() {
        return "Match{" +
                "id=" + id +
                ", user1=" + (user1 != null ? user1.getName() : "null") +
                ", user2=" + (user2 != null ? user2.getName() : "null") +
                ", compatibilityScore=" + compatibilityScore +
                ", status=" + status +
                '}';
    }
}

/**
 * Match status enum
 */
enum MatchStatus {
    PENDING, LIKED, MATCHED, REJECTED
}
