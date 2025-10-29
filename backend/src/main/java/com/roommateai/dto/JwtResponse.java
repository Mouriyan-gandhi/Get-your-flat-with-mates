package com.roommateai.dto;

/**
 * JWT Response DTO
 */
public class JwtResponse {
    
    private String token;
    private String type = "Bearer";
    private Long id;
    private String name;
    private String email;
    private String college;
    private String role;
    
    // Constructors
    public JwtResponse() {}
    
    public JwtResponse(String token, Long id, String name, String email, String college, String role) {
        this.token = token;
        this.id = id;
        this.name = name;
        this.email = email;
        this.college = college;
        this.role = role;
    }
    
    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
