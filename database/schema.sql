-- RoomMate.AI Database Schema
-- MySQL 8.0+ compatible schema for student rental platform

-- Create database
CREATE DATABASE IF NOT EXISTS roommate_ai CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE roommate_ai;

-- Users table - Student profiles and preferences
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    college VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    profile_image_url VARCHAR(500),
    bio TEXT,
    preferences_json JSON,
    is_verified BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    role ENUM('STUDENT', 'ADMIN', 'LANDLORD') DEFAULT 'STUDENT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_email (email),
    INDEX idx_college (college),
    INDEX idx_active (is_active)
);

-- Rentals table - Property listings
CREATE TABLE rentals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    rent DECIMAL(10,2) NOT NULL,
    deposit DECIMAL(10,2),
    address TEXT NOT NULL,
    city VARCHAR(50) NOT NULL,
    state VARCHAR(50) NOT NULL,
    pincode VARCHAR(10),
    latitude DECIMAL(10,8),
    longitude DECIMAL(11,8),
    amenities_json JSON,
    images_json JSON,
    property_type ENUM('HOSTEL', 'PG', 'APARTMENT', 'HOUSE') NOT NULL,
    room_type ENUM('SINGLE', 'SHARED', 'DOUBLE', 'TRIPLE') NOT NULL,
    available_from DATE,
    available_until DATE,
    is_available BOOLEAN DEFAULT TRUE,
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_owner (owner_id),
    INDEX idx_location (city, state),
    INDEX idx_price (rent),
    INDEX idx_available (is_available),
    INDEX idx_coordinates (latitude, longitude)
);

-- Matches table - Roommate compatibility scores
CREATE TABLE matches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user1_id BIGINT NOT NULL,
    user2_id BIGINT NOT NULL,
    compatibility_score DECIMAL(5,2) NOT NULL,
    status ENUM('PENDING', 'LIKED', 'MATCHED', 'REJECTED') DEFAULT 'PENDING',
    matched_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user1_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (user2_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_match (user1_id, user2_id),
    INDEX idx_user1 (user1_id),
    INDEX idx_user2 (user2_id),
    INDEX idx_status (status),
    INDEX idx_score (compatibility_score)
);

-- Reviews table - Rating system for landlords and roommates
CREATE TABLE reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reviewer_id BIGINT NOT NULL,
    target_id BIGINT NOT NULL,
    target_type ENUM('USER', 'RENTAL') NOT NULL,
    rating DECIMAL(2,1) NOT NULL CHECK (rating >= 1.0 AND rating <= 5.0),
    comment TEXT,
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (reviewer_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (target_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_reviewer (reviewer_id),
    INDEX idx_target (target_id),
    INDEX idx_rating (rating),
    INDEX idx_target_type (target_type)
);

-- Rent splits table - Bill division records
CREATE TABLE rent_splits (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rental_id BIGINT NOT NULL,
    total_rent DECIMAL(10,2) NOT NULL,
    utilities DECIMAL(10,2) DEFAULT 0,
    members_count INT NOT NULL,
    per_person DECIMAL(10,2) NOT NULL,
    split_details_json JSON,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (rental_id) REFERENCES rentals(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_rental (rental_id),
    INDEX idx_created_by (created_by)
);

-- Posts table - Community feed
CREATE TABLE posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    category ENUM('BUY_SELL', 'ROOMMATE_SEARCH', 'ANNOUNCEMENT', 'GENERAL') NOT NULL,
    images_json JSON,
    likes_count INT DEFAULT 0,
    comments_count INT DEFAULT 0,
    is_pinned BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user (user_id),
    INDEX idx_category (category),
    INDEX idx_active (is_active),
    INDEX idx_created (created_at)
);

-- Post likes table - Track who liked which posts
CREATE TABLE post_likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_like (post_id, user_id),
    INDEX idx_post (post_id),
    INDEX idx_user (user_id)
);

-- Post comments table - Comments on community posts
CREATE TABLE post_comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    parent_comment_id BIGINT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_comment_id) REFERENCES post_comments(id) ON DELETE CASCADE,
    INDEX idx_post (post_id),
    INDEX idx_user (user_id),
    INDEX idx_parent (parent_comment_id)
);

-- Chat rooms table - Firebase chat room metadata
CREATE TABLE chat_rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    match_id BIGINT NOT NULL,
    firebase_room_id VARCHAR(100) UNIQUE NOT NULL,
    last_message TEXT,
    last_message_at TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (match_id) REFERENCES matches(id) ON DELETE CASCADE,
    INDEX idx_match (match_id),
    INDEX idx_firebase_room (firebase_room_id),
    INDEX idx_active (is_active)
);

-- Insert sample data for testing
INSERT INTO users (name, email, password, college, phone, preferences_json, is_verified) VALUES
('John Doe', 'john.doe@srmuniv.ac.in', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'SRM University', '9876543210', '{"budget": 8000, "cleanliness": "high", "smoking": "no", "sleep": "early", "genderPref": "any", "interests": ["coding", "music"]}', TRUE),
('Jane Smith', 'jane.smith@vit.ac.in', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'VIT University', '9876543211', '{"budget": 10000, "cleanliness": "medium", "smoking": "no", "sleep": "late", "genderPref": "female", "interests": ["art", "reading"]}', TRUE),
('Admin User', 'admin@roommateai.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Admin College', '9876543212', '{}', TRUE);

-- Update admin role
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@roommateai.com';

-- Sample rental data
INSERT INTO rentals (owner_id, title, description, rent, deposit, address, city, state, pincode, latitude, longitude, amenities_json, property_type, room_type, available_from) VALUES
(1, 'Cozy PG near SRM Campus', 'Beautiful PG with AC rooms, WiFi, and home-cooked meals. Perfect for students!', 8500.00, 5000.00, 'Near SRM University Gate', 'Chennai', 'Tamil Nadu', '603203', 12.8236, 80.0435, '["AC", "WiFi", "Meals", "Laundry", "Parking"]', 'PG', 'SINGLE', '2024-02-01'),
(2, 'Modern Hostel with Gym', 'New hostel with modern amenities including gym, study room, and 24/7 security.', 12000.00, 8000.00, 'VIT University Area', 'Vellore', 'Tamil Nadu', '632014', 12.9702, 79.1559, '["AC", "WiFi", "Gym", "Study Room", "Security", "Laundry"]', 'HOSTEL', 'DOUBLE', '2024-02-15');

-- Sample community posts
INSERT INTO posts (user_id, title, content, category) VALUES
(1, 'Looking for Roommate near SRM', 'Hi! I am looking for a roommate to share a 2BHK apartment near SRM University. Budget around 8k per person. Contact me if interested!', 'ROOMMATE_SEARCH'),
(2, 'Selling Study Table', 'Selling my study table in good condition. Price negotiable. Contact for details.', 'BUY_SELL'),
(1, 'Campus Event Announcement', 'There will be a tech fest next week. All students are invited to participate!', 'ANNOUNCEMENT');

-- Create indexes for better performance
CREATE INDEX idx_users_college_active ON users(college, is_active);
CREATE INDEX idx_rentals_location_price ON rentals(city, state, rent);
CREATE INDEX idx_matches_score_status ON matches(compatibility_score, status);
CREATE INDEX idx_posts_category_active ON posts(category, is_active);
CREATE INDEX idx_reviews_target_rating ON reviews(target_id, target_type, rating);
