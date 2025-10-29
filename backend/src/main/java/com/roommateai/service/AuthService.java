package com.roommateai.service;

import com.roommateai.config.JwtTokenUtil;
import com.roommateai.dto.SignupRequest;
import com.roommateai.model.User;
import com.roommateai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Authentication Service
 * Handles user registration, login, and JWT token management
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * Check if email already exists
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Create a new user
     */
    public User createUser(SignupRequest signupRequest) {
        User user = new User();
        user.setName(signupRequest.getName());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setCollege(signupRequest.getCollege());
        user.setPhone(signupRequest.getPhone());
        user.setBio(signupRequest.getBio());
        user.setIsVerified(true); // Auto-verify college email users
        user.setIsActive(true);
        user.setRole(User.UserRole.STUDENT);

        return userRepository.save(user);
    }

    /**
     * Authenticate user with email and password
     */
    public User authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email).orElse(null);
        
        if (user != null && passwordEncoder.matches(password, user.getPassword()) && user.getIsActive()) {
            return user;
        }
        
        return null;
    }

    /**
     * Generate JWT token for user
     */
    public String generateJwtToken(User user) {
        return jwtTokenUtil.generateToken(user);
    }

    /**
     * Validate JWT token and return user
     */
    public User validateToken(String token) {
        try {
            if (jwtTokenUtil.validateToken(token)) {
                String email = jwtTokenUtil.getUsernameFromToken(token);
                return userRepository.findByEmail(email).orElse(null);
            }
        } catch (Exception e) {
            // Token is invalid or expired
        }
        return null;
    }

    /**
     * Get user by email
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    /**
     * Get user by ID
     */
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Update user profile
     */
    public User updateUser(User user) {
        return userRepository.save(user);
    }
}
