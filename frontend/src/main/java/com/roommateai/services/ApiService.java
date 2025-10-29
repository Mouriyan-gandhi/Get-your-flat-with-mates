package com.roommateai.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * API Service
 * Handles HTTP communication with the backend
 */
public class ApiService {
    
    private static final String BASE_URL = "http://localhost:8080";
    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public ApiService() {
        this.httpClient = HttpClients.createDefault();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Perform GET request
     */
    public String get(String endpoint) throws IOException {
        return get(endpoint, null);
    }
    
    /**
     * Perform GET request with authorization
     */
    public String get(String endpoint, String token) throws IOException {
        HttpGet request = new HttpGet(BASE_URL + endpoint);
        
        if (token != null) {
            request.setHeader("Authorization", "Bearer " + token);
        }
        
        request.setHeader("Content-Type", "application/json");
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            return new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
        }
    }
    
    /**
     * Perform POST request
     */
    public String post(String endpoint, Object data) throws IOException {
        return post(endpoint, data, null);
    }
    
    /**
     * Perform POST request with authorization
     */
    public String post(String endpoint, Object data, String token) throws IOException {
        HttpPost request = new HttpPost(BASE_URL + endpoint);
        
        if (token != null) {
            request.setHeader("Authorization", "Bearer " + token);
        }
        
        String jsonData = objectMapper.writeValueAsString(data);
        StringEntity entity = new StringEntity(jsonData, ContentType.APPLICATION_JSON);
        request.setEntity(entity);
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            return new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
        }
    }
    
    /**
     * Perform PUT request
     */
    public String put(String endpoint, Object data, String token) throws IOException {
        HttpPut request = new HttpPut(BASE_URL + endpoint);
        
        if (token != null) {
            request.setHeader("Authorization", "Bearer " + token);
        }
        
        String jsonData = objectMapper.writeValueAsString(data);
        StringEntity entity = new StringEntity(jsonData, ContentType.APPLICATION_JSON);
        request.setEntity(entity);
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            return new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
        }
    }
    
    /**
     * Perform DELETE request
     */
    public String delete(String endpoint, String token) throws IOException {
        HttpDelete request = new HttpDelete(BASE_URL + endpoint);
        
        if (token != null) {
            request.setHeader("Authorization", "Bearer " + token);
        }
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            return new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
        }
    }
    
    /**
     * Close HTTP client
     */
    public void close() throws IOException {
        httpClient.close();
    }
}
