package com.roommateai.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Signup Request DTO
 */
public class SignupRequest {
    
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    @NotBlank(message = "College is required")
    @Size(max = 100, message = "College name must not exceed 100 characters")
    private String college;
    
    @Size(max = 15, message = "Phone number must not exceed 15 characters")
    private String phone;
    
    @Size(max = 500, message = "Bio must not exceed 500 characters")
    private String bio;
    
    // Constructors
    public SignupRequest() {}
    
    public SignupRequest(String name, String email, String password, String college) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.college = college;
    }
    
    // Getters and Setters
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
    
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
}
