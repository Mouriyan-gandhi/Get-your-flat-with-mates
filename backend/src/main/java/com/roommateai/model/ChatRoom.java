package com.roommateai.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * ChatRoom entity for Firebase chat room metadata
 * Links matches to Firebase chat rooms for real-time messaging
 */
@Entity
@Table(name = "chat_rooms")
@EntityListeners(AuditingEntityListener.class)
public class ChatRoom {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Match is required")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;
    
    @NotBlank(message = "Firebase room ID is required")
    @Size(max = 100, message = "Firebase room ID must not exceed 100 characters")
    @Column(unique = true, nullable = false)
    private String firebaseRoomId;
    
    @Column(columnDefinition = "TEXT")
    private String lastMessage;
    
    private LocalDateTime lastMessageAt;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructors
    public ChatRoom() {}
    
    public ChatRoom(Match match, String firebaseRoomId) {
        this.match = match;
        this.firebaseRoomId = firebaseRoomId;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Match getMatch() { return match; }
    public void setMatch(Match match) { this.match = match; }
    
    public String getFirebaseRoomId() { return firebaseRoomId; }
    public void setFirebaseRoomId(String firebaseRoomId) { this.firebaseRoomId = firebaseRoomId; }
    
    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
    
    public LocalDateTime getLastMessageAt() { return lastMessageAt; }
    public void setLastMessageAt(LocalDateTime lastMessageAt) { this.lastMessageAt = lastMessageAt; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @Override
    public String toString() {
        return "ChatRoom{" +
                "id=" + id +
                ", firebaseRoomId='" + firebaseRoomId + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
