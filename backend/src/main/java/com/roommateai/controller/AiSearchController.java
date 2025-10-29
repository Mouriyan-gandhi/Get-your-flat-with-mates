package com.roommateai.controller;

import com.roommateai.dto.AiSearchRequest;
import com.roommateai.dto.RentalResponse;
import com.roommateai.model.User;
import com.roommateai.service.AuthService;
import com.roommateai.service.GeminiService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI Search Controller
 * Handles natural language search using Gemini AI
 */
@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AiSearchController {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private AuthService authService;

    /**
     * AI-powered natural language search
     * Example: "Find a room near SRM under ₹10k with AC and 2 beds"
     */
    @PostMapping("/search")
    public ResponseEntity<?> aiSearch(@Valid @RequestBody AiSearchRequest searchRequest,
                                     @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            // Set user's college for context
            searchRequest.setUserCollege(user.getCollege());

            // Process search using Gemini AI
            List<RentalResponse> results = geminiService.processNaturalLanguageSearch(searchRequest);

            Map<String, Object> response = new HashMap<>();
            response.put("query", searchRequest.getQuery());
            response.put("results", results);
            response.put("count", results.size());
            response.put("message", "AI search completed successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "AI search failed");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Quick AI search with query parameter
     */
    @GetMapping("/search")
    public ResponseEntity<?> quickAiSearch(@RequestParam String q,
                                          @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            AiSearchRequest searchRequest = new AiSearchRequest(q, user.getCollege());
            List<RentalResponse> results = geminiService.processNaturalLanguageSearch(searchRequest);

            Map<String, Object> response = new HashMap<>();
            response.put("query", q);
            response.put("results", results);
            response.put("count", results.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "AI search failed");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get AI search suggestions
     */
    @GetMapping("/suggestions")
    public ResponseEntity<Map<String, Object>> getSearchSuggestions() {
        Map<String, Object> suggestions = new HashMap<>();
        
        suggestions.put("examples", List.of(
            "Find a room near SRM under ₹10k with AC",
            "PG with WiFi and meals under ₹8000",
            "Shared room near VIT with gym access",
            "Single room in Chennai with parking",
            "Hostel near college with 24/7 security"
        ));
        
        suggestions.put("amenities", List.of(
            "AC", "WiFi", "Meals", "Gym", "Parking", "Laundry", 
            "Security", "Study Room", "Common Area", "Kitchen"
        ));
        
        suggestions.put("propertyTypes", List.of(
            "HOSTEL", "PG", "APARTMENT", "HOUSE"
        ));
        
        suggestions.put("roomTypes", List.of(
            "SINGLE", "SHARED", "DOUBLE", "TRIPLE"
        ));
        
        return ResponseEntity.ok(suggestions);
    }
}
