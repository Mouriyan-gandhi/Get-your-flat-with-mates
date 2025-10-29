package com.roommateai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main Spring Boot application class for RoomMate.AI backend
 * 
 * Features:
 * - Student-only rental platform
 * - AI-powered search with Gemini API
 * - Roommate matching system
 * - Real-time chat with Firebase
 * - Rent splitting calculator
 * - Review and rating system
 */
@SpringBootApplication
@EnableJpaAuditing
public class RoommateAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RoommateAiApplication.class, args);
        System.out.println("🏠 RoomMate.AI Backend Server Started!");
        System.out.println("📡 API available at: http://localhost:8080/api");
        System.out.println("🎓 Ready to serve college students!");
    }
}
