package com.roommateai.controller;

import com.roommateai.dto.MatchResponse;
import com.roommateai.model.Match;
import com.roommateai.model.User;
import com.roommateai.service.AuthService;
import com.roommateai.service.RoommateMatchingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Roommate Matching Controller
 * REST endpoints for roommate matching functionality
 */
@RestController
@RequestMapping("/api/matches")
@CrossOrigin(origins = "*")
public class MatchController {

    @Autowired
    private RoommateMatchingService matchingService;

    @Autowired
    private AuthService authService;

    /**
     * Get potential matches for a user
     */
    @GetMapping("/potential")
    public ResponseEntity<?> getPotentialMatches(@RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            List<User> potentialMatches = matchingService.findPotentialMatches(user.getId());
            List<MatchResponse> responses = potentialMatches.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("matches", responses);
            response.put("count", responses.size());
            response.put("message", "Potential matches retrieved successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get potential matches");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Like a potential roommate
     */
    @PostMapping("/like")
    public ResponseEntity<?> likeUser(@Valid @RequestBody Map<String, Long> request,
                                     @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            Long targetUserId = request.get("targetUserId");
            if (targetUserId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Target user ID is required"));
            }

            Match match = matchingService.processLike(user.getId(), targetUserId);
            MatchResponse response = convertMatchToResponse(match);

            Map<String, Object> result = new HashMap<>();
            result.put("match", response);
            result.put("isMatch", match.getStatus() == Match.MatchStatus.MATCHED);
            result.put("message", match.getStatus() == Match.MatchStatus.MATCHED ? 
                "It's a match! 🎉" : "Like recorded successfully");

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to process like");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Pass on a potential roommate
     */
    @PostMapping("/pass")
    public ResponseEntity<?> passUser(@Valid @RequestBody Map<String, Long> request,
                                     @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            Long targetUserId = request.get("targetUserId");
            if (targetUserId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Target user ID is required"));
            }

            Match match = matchingService.processPass(user.getId(), targetUserId);

            Map<String, Object> result = new HashMap<>();
            result.put("message", "Pass recorded successfully");
            result.put("matchId", match != null ? match.getId() : null);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to process pass");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get all matches for a user
     */
    @GetMapping("/my-matches")
    public ResponseEntity<?> getMyMatches(@RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            List<Match> matches = matchingService.getMatchesForUser(user.getId());
            List<MatchResponse> responses = matches.stream()
                    .map(this::convertMatchToResponse)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("matches", responses);
            response.put("count", responses.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get matches");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get matched pairs (mutual likes)
     */
    @GetMapping("/matched")
    public ResponseEntity<?> getMatchedPairs(@RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            List<Match> matchedPairs = matchingService.getMatchedPairs(user.getId());
            List<MatchResponse> responses = matchedPairs.stream()
                    .map(this::convertMatchToResponse)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("matches", responses);
            response.put("count", responses.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get matched pairs");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get match statistics for a user
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getMatchStats(@RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            List<Match> allMatches = matchingService.getMatchesForUser(user.getId());
            List<Match> matchedPairs = matchingService.getMatchedPairs(user.getId());

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalMatches", allMatches.size());
            stats.put("matchedPairs", matchedPairs.size());
            stats.put("likesGiven", allMatches.stream()
                    .filter(m -> m.getStatus() == Match.MatchStatus.LIKED || m.getStatus() == Match.MatchStatus.MATCHED)
                    .count());
            stats.put("passesGiven", allMatches.stream()
                    .filter(m -> m.getStatus() == Match.MatchStatus.REJECTED)
                    .count());

            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get match statistics");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Convert User to MatchResponse
     */
    private MatchResponse convertToResponse(User user) {
        MatchResponse response = new MatchResponse();
        response.setUserId(user.getId());
        response.setUserName(user.getName());
        response.setUserEmail(user.getEmail());
        response.setUserCollege(user.getCollege());
        response.setUserBio(user.getBio());
        response.setUserProfileImageUrl(user.getProfileImageUrl());
        response.setPreferencesJson(user.getPreferencesJson());
        return response;
    }

    /**
     * Convert Match to MatchResponse
     */
    private MatchResponse convertMatchToResponse(Match match) {
        MatchResponse response = new MatchResponse();
        response.setId(match.getId());
        response.setCompatibilityScore(match.getCompatibilityScore());
        response.setStatus(match.getStatus().name());
        response.setMatchedAt(match.getMatchedAt());
        response.setCreatedAt(match.getCreatedAt());

        // Set user information (the other user in the match)
        User otherUser = match.getUser1();
        response.setUserId(otherUser.getId());
        response.setUserName(otherUser.getName());
        response.setUserEmail(otherUser.getEmail());
        response.setUserCollege(otherUser.getCollege());
        response.setUserBio(otherUser.getBio());
        response.setUserProfileImageUrl(otherUser.getProfileImageUrl());
        response.setPreferencesJson(otherUser.getPreferencesJson());

        return response;
    }
}
