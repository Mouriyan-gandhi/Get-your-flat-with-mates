package com.roommateai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roommateai.dto.AiSearchRequest;
import com.roommateai.dto.RentalResponse;
import com.roommateai.model.Rental;
import com.roommateai.repository.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Gemini AI Service
 * Integrates with Google Gemini API for natural language search processing
 */
@Service
public class GeminiService {

    @Autowired
    private RentalRepository rentalRepository;

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.api-url}")
    private String apiUrl;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public GeminiService() {
        this.webClient = WebClient.builder().build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Process natural language search query using Gemini AI
     */
    public List<RentalResponse> processNaturalLanguageSearch(AiSearchRequest searchRequest) {
        try {
            // Generate prompt for Gemini
            String prompt = generateSearchPrompt(searchRequest);
            
            // Call Gemini API
            String geminiResponse = callGeminiApi(prompt);
            
            // Parse Gemini response to structured filters
            Map<String, Object> filters = parseGeminiResponse(geminiResponse);
            
            // Apply filters to search rentals
            return searchRentalsWithFilters(filters);
            
        } catch (Exception e) {
            // Fallback to basic text search if AI fails
            return fallbackTextSearch(searchRequest.getQuery());
        }
    }

    /**
     * Generate search prompt for Gemini API
     */
    private String generateSearchPrompt(AiSearchRequest searchRequest) {
        return String.format("""
            You are a rental search assistant for college students. 
            Parse this search query and extract structured filters in JSON format.
            
            User Query: "%s"
            User College: "%s"
            
            Extract the following information and return ONLY a valid JSON object:
            {
                "college": "college name if mentioned, otherwise null",
                "maxPrice": number or null,
                "minPrice": number or null,
                "amenities": ["list", "of", "amenities"] or null,
                "distance": number in km or null,
                "propertyType": "HOSTEL/PG/APARTMENT/HOUSE" or null,
                "roomType": "SINGLE/SHARED/DOUBLE/TRIPLE" or null,
                "city": "city name" or null,
                "state": "state name" or null
            }
            
            Guidelines:
            - If college is mentioned, prioritize rentals near that college
            - Convert price mentions to numbers (e.g., "10k" = 10000)
            - Extract amenities like "AC", "WiFi", "Meals", "Gym", "Parking"
            - Distance should be in kilometers
            - Property types: HOSTEL, PG, APARTMENT, HOUSE
            - Room types: SINGLE, SHARED, DOUBLE, TRIPLE
            - Return null for fields not mentioned
            """, searchRequest.getQuery(), searchRequest.getUserCollege());
    }

    /**
     * Call Gemini API
     */
    private String callGeminiApi(String prompt) {
        try {
            Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of(
                    "parts", List.of(Map.of("text", prompt))
                )),
                "generationConfig", Map.of(
                    "temperature", 0.1,
                    "topK", 1,
                    "topP", 0.8,
                    "maxOutputTokens", 1000
                )
            );

            String response = webClient.post()
                    .uri(apiUrl + "?key=" + apiKey)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return extractTextFromResponse(response);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to call Gemini API: " + e.getMessage());
        }
    }

    /**
     * Extract text content from Gemini response
     */
    private String extractTextFromResponse(String response) {
        try {
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode candidates = rootNode.path("candidates");
            
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).path("content").path("parts");
                if (content.isArray() && content.size() > 0) {
                    return content.get(0).path("text").asText();
                }
            }
            
            throw new RuntimeException("Invalid response format from Gemini API");
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Gemini response: " + e.getMessage());
        }
    }

    /**
     * Parse Gemini response to structured filters
     */
    private Map<String, Object> parseGeminiResponse(String response) {
        try {
            // Clean the response (remove markdown formatting if present)
            String cleanResponse = response.replaceAll("```json", "").replaceAll("```", "").trim();
            
            @SuppressWarnings("unchecked")
            Map<String, Object> filters = objectMapper.readValue(cleanResponse, Map.class);
            
            return filters;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response: " + e.getMessage());
        }
    }

    /**
     * Search rentals using AI-extracted filters
     */
    private List<RentalResponse> searchRentalsWithFilters(Map<String, Object> filters) {
        List<Rental> rentals = new ArrayList<>();
        
        // Get all available rentals first
        List<Rental> allRentals = rentalRepository.findByIsAvailable(true);
        
        // Apply filters
        for (Rental rental : allRentals) {
            if (matchesFilters(rental, filters)) {
                rentals.add(rental);
            }
        }
        
        // Convert to response DTOs
        return rentals.stream()
                .map(this::convertToResponse)
                .toList();
    }

    /**
     * Check if rental matches AI-extracted filters
     */
    private boolean matchesFilters(Rental rental, Map<String, Object> filters) {
        // College filter
        if (filters.get("college") != null) {
            String college = filters.get("college").toString().toLowerCase();
            if (!rental.getOwner().getCollege().toLowerCase().contains(college)) {
                return false;
            }
        }
        
        // Price filters
        if (filters.get("maxPrice") != null) {
            BigDecimal maxPrice = new BigDecimal(filters.get("maxPrice").toString());
            if (rental.getRent().compareTo(maxPrice) > 0) {
                return false;
            }
        }
        
        if (filters.get("minPrice") != null) {
            BigDecimal minPrice = new BigDecimal(filters.get("minPrice").toString());
            if (rental.getRent().compareTo(minPrice) < 0) {
                return false;
            }
        }
        
        // Property type filter
        if (filters.get("propertyType") != null) {
            String propertyType = filters.get("propertyType").toString();
            if (!rental.getPropertyType().name().equals(propertyType)) {
                return false;
            }
        }
        
        // Room type filter
        if (filters.get("roomType") != null) {
            String roomType = filters.get("roomType").toString();
            if (!rental.getRoomType().name().equals(roomType)) {
                return false;
            }
        }
        
        // City filter
        if (filters.get("city") != null) {
            String city = filters.get("city").toString().toLowerCase();
            if (!rental.getCity().toLowerCase().contains(city)) {
                return false;
            }
        }
        
        // State filter
        if (filters.get("state") != null) {
            String state = filters.get("state").toString().toLowerCase();
            if (!rental.getState().toLowerCase().contains(state)) {
                return false;
            }
        }
        
        // Amenities filter
        if (filters.get("amenities") != null) {
            @SuppressWarnings("unchecked")
            List<String> requiredAmenities = (List<String>) filters.get("amenities");
            if (rental.getAmenitiesJson() != null) {
                String amenities = rental.getAmenitiesJson().toLowerCase();
                for (String amenity : requiredAmenities) {
                    if (!amenities.contains(amenity.toLowerCase())) {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Fallback text search when AI fails
     */
    private List<RentalResponse> fallbackTextSearch(String query) {
        List<Rental> rentals = rentalRepository.searchByTitleOrDescription(query);
        return rentals.stream()
                .map(this::convertToResponse)
                .toList();
    }

    /**
     * Convert Rental entity to RentalResponse DTO
     */
    private RentalResponse convertToResponse(Rental rental) {
        RentalResponse response = new RentalResponse();
        response.setId(rental.getId());
        response.setOwnerId(rental.getOwner().getId());
        response.setOwnerName(rental.getOwner().getName());
        response.setTitle(rental.getTitle());
        response.setDescription(rental.getDescription());
        response.setRent(rental.getRent());
        response.setDeposit(rental.getDeposit());
        response.setAddress(rental.getAddress());
        response.setCity(rental.getCity());
        response.setState(rental.getState());
        response.setPincode(rental.getPincode());
        response.setLatitude(rental.getLatitude());
        response.setLongitude(rental.getLongitude());
        response.setAmenitiesJson(rental.getAmenitiesJson());
        response.setImagesJson(rental.getImagesJson());
        response.setPropertyType(rental.getPropertyType().name());
        response.setRoomType(rental.getRoomType().name());
        response.setAvailableFrom(rental.getAvailableFrom());
        response.setAvailableUntil(rental.getAvailableUntil());
        response.setIsAvailable(rental.getIsAvailable());
        response.setIsVerified(rental.getIsVerified());
        response.setCreatedAt(rental.getCreatedAt());
        response.setUpdatedAt(rental.getUpdatedAt());
        
        return response;
    }
}
