package com.roommateai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roommateai.model.Match;
import com.roommateai.model.User;
import com.roommateai.repository.MatchRepository;
import com.roommateai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Roommate Matching Service
 * Implements weighted compatibility algorithm for roommate matching
 */
@Service
public class RoommateMatchingService {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Calculate compatibility score between two users
     * Uses weighted algorithm: 0.3*Budget + 0.2*Lifestyle + 0.2*Sleep + 0.3*Personality
     */
    public BigDecimal calculateCompatibilityScore(User user1, User user2) {
        try {
            Map<String, Object> prefs1 = parsePreferences(user1.getPreferencesJson());
            Map<String, Object> prefs2 = parsePreferences(user2.getPreferencesJson());

            double budgetScore = calculateBudgetCompatibility(prefs1, prefs2);
            double lifestyleScore = calculateLifestyleCompatibility(prefs1, prefs2);
            double sleepScore = calculateSleepCompatibility(prefs1, prefs2);
            double personalityScore = calculatePersonalityCompatibility(prefs1, prefs2);

            // Weighted average: 0.3*Budget + 0.2*Lifestyle + 0.2*Sleep + 0.3*Personality
            double totalScore = (0.3 * budgetScore) + (0.2 * lifestyleScore) + 
                               (0.2 * sleepScore) + (0.3 * personalityScore);

            return BigDecimal.valueOf(Math.round(totalScore * 100.0) / 100.0);

        } catch (Exception e) {
            // Return default score if preferences can't be parsed
            return BigDecimal.valueOf(50.0);
        }
    }

    /**
     * Calculate budget compatibility (0-100)
     */
    private double calculateBudgetCompatibility(Map<String, Object> prefs1, Map<String, Object> prefs2) {
        try {
            Integer budget1 = (Integer) prefs1.get("budget");
            Integer budget2 = (Integer) prefs2.get("budget");

            if (budget1 == null || budget2 == null) {
                return 50.0; // Default score
            }

            double diff = Math.abs(budget1 - budget2);
            double avgBudget = (budget1 + budget2) / 2.0;
            double percentageDiff = (diff / avgBudget) * 100;

            // Score decreases as budget difference increases
            return Math.max(0, 100 - percentageDiff);

        } catch (Exception e) {
            return 50.0;
        }
    }

    /**
     * Calculate lifestyle compatibility (0-100)
     */
    private double calculateLifestyleCompatibility(Map<String, Object> prefs1, Map<String, Object> prefs2) {
        try {
            String cleanliness1 = (String) prefs1.get("cleanliness");
            String cleanliness2 = (String) prefs2.get("cleanliness");
            String smoking1 = (String) prefs1.get("smoking");
            String smoking2 = (String) prefs2.get("smoking");

            double score = 50.0; // Base score

            // Cleanliness compatibility
            if (cleanliness1 != null && cleanliness2 != null) {
                if (cleanliness1.equals(cleanliness2)) {
                    score += 25.0;
                } else if (isCompatibleCleanliness(cleanliness1, cleanliness2)) {
                    score += 15.0;
                }
            }

            // Smoking compatibility
            if (smoking1 != null && smoking2 != null) {
                if (smoking1.equals(smoking2)) {
                    score += 25.0;
                } else if ("no".equals(smoking1) && "no".equals(smoking2)) {
                    score += 25.0;
                }
            }

            return Math.min(100.0, score);

        } catch (Exception e) {
            return 50.0;
        }
    }

    /**
     * Calculate sleep schedule compatibility (0-100)
     */
    private double calculateSleepCompatibility(Map<String, Object> prefs1, Map<String, Object> prefs2) {
        try {
            String sleep1 = (String) prefs1.get("sleep");
            String sleep2 = (String) prefs2.get("sleep");

            if (sleep1 == null || sleep2 == null) {
                return 50.0;
            }

            if (sleep1.equals(sleep2)) {
                return 100.0;
            } else if (isCompatibleSleep(sleep1, sleep2)) {
                return 75.0;
            } else {
                return 25.0;
            }

        } catch (Exception e) {
            return 50.0;
        }
    }

    /**
     * Calculate personality compatibility (0-100)
     */
    private double calculatePersonalityCompatibility(Map<String, Object> prefs1, Map<String, Object> prefs2) {
        try {
            @SuppressWarnings("unchecked")
            List<String> interests1 = (List<String>) prefs1.get("interests");
            @SuppressWarnings("unchecked")
            List<String> interests2 = (List<String>) prefs2.get("interests");

            if (interests1 == null || interests2 == null || interests1.isEmpty() || interests2.isEmpty()) {
                return 50.0;
            }

            // Calculate Jaccard similarity
            Set<String> set1 = new HashSet<>(interests1);
            Set<String> set2 = new HashSet<>(interests2);

            Set<String> intersection = new HashSet<>(set1);
            intersection.retainAll(set2);

            Set<String> union = new HashSet<>(set1);
            union.addAll(set2);

            if (union.isEmpty()) {
                return 50.0;
            }

            double jaccardSimilarity = (double) intersection.size() / union.size();
            return jaccardSimilarity * 100.0;

        } catch (Exception e) {
            return 50.0;
        }
    }

    /**
     * Check if cleanliness preferences are compatible
     */
    private boolean isCompatibleCleanliness(String clean1, String clean2) {
        return ("high".equals(clean1) && "medium".equals(clean2)) ||
               ("medium".equals(clean1) && "high".equals(clean2));
    }

    /**
     * Check if sleep schedules are compatible
     */
    private boolean isCompatibleSleep(String sleep1, String sleep2) {
        return ("early".equals(sleep1) && "normal".equals(sleep2)) ||
               ("normal".equals(sleep1) && "early".equals(sleep2)) ||
               ("normal".equals(sleep1) && "late".equals(sleep2)) ||
               ("late".equals(sleep1) && "normal".equals(sleep2));
    }

    /**
     * Parse user preferences JSON
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> parsePreferences(String preferencesJson) {
        try {
            if (preferencesJson == null || preferencesJson.trim().isEmpty()) {
                return new HashMap<>();
            }
            return objectMapper.readValue(preferencesJson, Map.class);
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    /**
     * Find potential matches for a user
     */
    public List<User> findPotentialMatches(Long userId) {
        List<User> potentialUsers = matchRepository.findPotentialMatches(userId);
        
        // Calculate compatibility scores and sort
        return potentialUsers.stream()
                .map(user -> {
                    User currentUser = userRepository.findById(userId).orElse(null);
                    if (currentUser != null) {
                        BigDecimal score = calculateCompatibilityScore(currentUser, user);
                        // Store score in a temporary field or use a wrapper
                        return new UserWithScore(user, score);
                    }
                    return new UserWithScore(user, BigDecimal.ZERO);
                })
                .sorted((u1, u2) -> u2.getScore().compareTo(u1.getScore()))
                .limit(20) // Limit to top 20 matches
                .map(UserWithScore::getUser)
                .collect(Collectors.toList());
    }

    /**
     * Create or update a match between two users
     */
    public Match createOrUpdateMatch(Long user1Id, Long user2Id) {
        User user1 = userRepository.findById(user1Id).orElse(null);
        User user2 = userRepository.findById(user2Id).orElse(null);

        if (user1 == null || user2 == null) {
            throw new RuntimeException("One or both users not found");
        }

        // Check if match already exists
        Optional<Match> existingMatch = matchRepository.findMatchBetweenUsers(user1Id, user2Id);
        
        if (existingMatch.isPresent()) {
            Match match = existingMatch.get();
            match.setStatus(Match.MatchStatus.LIKED);
            return matchRepository.save(match);
        } else {
            // Create new match
            BigDecimal compatibilityScore = calculateCompatibilityScore(user1, user2);
            Match match = new Match(user1, user2, compatibilityScore);
            match.setStatus(Match.MatchStatus.LIKED);
            return matchRepository.save(match);
        }
    }

    /**
     * Process a like action
     */
    public Match processLike(Long userId, Long targetUserId) {
        Match match = createOrUpdateMatch(userId, targetUserId);
        
        // Check if both users have liked each other
        Optional<Match> reverseMatch = matchRepository.findMatchBetweenUsers(targetUserId, userId);
        
        if (reverseMatch.isPresent() && reverseMatch.get().getStatus() == Match.MatchStatus.LIKED) {
            // Both users liked each other - create a match!
            match.setStatus(Match.MatchStatus.MATCHED);
            match.setMatchedAt(LocalDateTime.now());
            reverseMatch.get().setStatus(Match.MatchStatus.MATCHED);
            reverseMatch.get().setMatchedAt(LocalDateTime.now());
            
            matchRepository.save(match);
            matchRepository.save(reverseMatch.get());
        }
        
        return match;
    }

    /**
     * Process a pass action
     */
    public Match processPass(Long userId, Long targetUserId) {
        Optional<Match> existingMatch = matchRepository.findMatchBetweenUsers(userId, targetUserId);
        
        if (existingMatch.isPresent()) {
            Match match = existingMatch.get();
            match.setStatus(Match.MatchStatus.REJECTED);
            return matchRepository.save(match);
        } else {
            // Create a rejected match to avoid showing this user again
            User user1 = userRepository.findById(userId).orElse(null);
            User user2 = userRepository.findById(targetUserId).orElse(null);
            
            if (user1 != null && user2 != null) {
                Match match = new Match(user1, user2, BigDecimal.ZERO);
                match.setStatus(Match.MatchStatus.REJECTED);
                return matchRepository.save(match);
            }
        }
        
        return null;
    }

    /**
     * Get matches for a user
     */
    public List<Match> getMatchesForUser(Long userId) {
        return matchRepository.findByUserId(userId);
    }

    /**
     * Get matched pairs for a user
     */
    public List<Match> getMatchedPairs(Long userId) {
        return matchRepository.findMatchedPairsForUser(userId);
    }

    /**
     * Helper class to store user with compatibility score
     */
    private static class UserWithScore {
        private final User user;
        private final BigDecimal score;

        public UserWithScore(User user, BigDecimal score) {
            this.user = user;
            this.score = score;
        }

        public User getUser() { return user; }
        public BigDecimal getScore() { return score; }
    }
}
