package com.roommateai.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

/**
 * User entity representing college students
 * Only students with verified college email domains can register
 */
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    @Column(nullable = false)
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    @Column(unique = true, nullable = false)
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(nullable = false)
    private String password;
    
    @NotBlank(message = "College is required")
    @Size(max = 100, message = "College name must not exceed 100 characters")
    @Column(nullable = false)
    private String college;
    
    @Size(max = 15, message = "Phone number must not exceed 15 characters")
    private String phone;
    
    @Size(max = 500, message = "Profile image URL must not exceed 500 characters")
    private String profileImageUrl;
    
    @Column(columnDefinition = "TEXT")
    private String bio;
    
    @Column(columnDefinition = "JSON")
    private String preferencesJson;
    
    @Column(nullable = false)
    private Boolean isVerified = false;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.STUDENT;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // Relationships
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Rental> ownedRentals;
    
    @OneToMany(mappedBy = "reviewer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviewsGiven;
    
    @OneToMany(mappedBy = "target", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviewsReceived;
    
    @OneToMany(mappedBy = "user1", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Match> matchesAsUser1;
    
    @OneToMany(mappedBy = "user2", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Match> matchesAsUser2;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Post> posts;
    
    // Constructors
    public User() {}
    
    public User(String name, String email, String password, String college) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.college = college;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    
    public String getPreferencesJson() { return preferencesJson; }
    public void setPreferencesJson(String preferencesJson) { this.preferencesJson = preferencesJson; }
    
    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Relationship getters and setters
    public List<Rental> getOwnedRentals() { return ownedRentals; }
    public void setOwnedRentals(List<Rental> ownedRentals) { this.ownedRentals = ownedRentals; }
    
    public List<Review> getReviewsGiven() { return reviewsGiven; }
    public void setReviewsGiven(List<Review> reviewsGiven) { this.reviewsGiven = reviewsGiven; }
    
    public List<Review> getReviewsReceived() { return reviewsReceived; }
    public void setReviewsReceived(List<Review> reviewsReceived) { this.reviewsReceived = reviewsReceived; }
    
    public List<Match> getMatchesAsUser1() { return matchesAsUser1; }
    public void setMatchesAsUser1(List<Match> matchesAsUser1) { this.matchesAsUser1 = matchesAsUser1; }
    
    public List<Match> getMatchesAsUser2() { return matchesAsUser2; }
    public void setMatchesAsUser2(List<Match> matchesAsUser2) { this.matchesAsUser2 = matchesAsUser2; }
    
    public List<Post> getPosts() { return posts; }
    public void setPosts(List<Post> posts) { this.posts = posts; }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", college='" + college + '\'' +
                ", role=" + role +
                '}';
    }
}

/**
 * User roles enum
 */
enum UserRole {
    STUDENT, ADMIN, LANDLORD
}
