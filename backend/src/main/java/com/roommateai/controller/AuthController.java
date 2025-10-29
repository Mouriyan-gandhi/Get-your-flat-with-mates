package com.roommateai.controller;

import com.roommateai.dto.JwtResponse;
import com.roommateai.dto.LoginRequest;
import com.roommateai.dto.SignupRequest;
import com.roommateai.model.User;
import com.roommateai.service.AuthService;
import com.roommateai.service.JwtUserDetailsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Authentication Controller
 * Handles user registration and login with college email validation
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    /**
     * User registration endpoint
     * Only allows college email domains
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        try {
            // Validate college email domain
            if (!userDetailsService.isValidCollegeEmail(signupRequest.getEmail())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid college email");
                error.put("message", "Please use your college email address (.edu, .ac.in, etc.)");
                return ResponseEntity.badRequest().body(error);
            }

            // Check if email already exists
            if (authService.existsByEmail(signupRequest.getEmail())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Email already exists");
                error.put("message", "An account with this email already exists");
                return ResponseEntity.badRequest().body(error);
            }

            // Create new user
            User user = authService.createUser(signupRequest);
            
            // Generate JWT token
            String token = authService.generateJwtToken(user);
            
            // Return JWT response
            JwtResponse jwtResponse = new JwtResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCollege(),
                user.getRole().name()
            );

            return ResponseEntity.ok(jwtResponse);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Registration failed");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * User login endpoint
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate user
            User user = authService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
            
            if (user == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid credentials");
                error.put("message", "Email or password is incorrect");
                return ResponseEntity.badRequest().body(error);
            }

            // Generate JWT token
            String token = authService.generateJwtToken(user);
            
            // Return JWT response
            JwtResponse jwtResponse = new JwtResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCollege(),
                user.getRole().name()
            );

            return ResponseEntity.ok(jwtResponse);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Login failed");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Validate JWT token endpoint
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid token format");
                return ResponseEntity.badRequest().body(error);
            }

            String token = authHeader.substring(7);
            User user = authService.validateToken(token);
            
            if (user == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid token");
                return ResponseEntity.badRequest().body(error);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("user", Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "college", user.getCollege(),
                "role", user.getRole().name()
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Token validation failed");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "RoomMate.AI Auth Service");
        return ResponseEntity.ok(response);
    }
}
