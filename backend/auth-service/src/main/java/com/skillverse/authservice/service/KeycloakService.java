package com.skillverse.authservice.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Setter
@Getter
@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakService {

    @Value("${keycloak.auth-server-url:http://localhost:8180}")
    private String keycloakUrl;

    @Value("${keycloak.realm:skillverse}")
    private String realm;

    @Value("${keycloak.resource:skillverse-client}")
    private String clientId;

    @Value("${keycloak.credentials.secret:secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Authenticate user with Keycloak and get access token
     */
    public Map<String, Object> authenticateUser(String username, String password) {
        // Mock authentication for development without Keycloak
        log.warn("Mocking Keycloak authentication for {}", username);
        return Map.of(
                "access_token", "mock_access_token_" + UUID.randomUUID(),
                "refresh_token", "mock_refresh_token_" + UUID.randomUUID(),
                "expires_in", 3600,
                "refresh_expires_in", 86400,
                "token_type", "Bearer",
                "not-before-policy", 0,
                "session_state", UUID.randomUUID().toString(),
                "scope", "profile email");
        /*
         * String tokenUrl = keycloakUrl + "/realms/" + realm +
         * "/protocol/openid-connect/token";
         * 
         * HttpHeaders headers = new HttpHeaders();
         * headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
         * 
         * MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
         * body.add("grant_type", "password");
         * body.add("client_id", clientId);
         * body.add("client_secret", clientSecret);
         * body.add("username", username);
         * body.add("password", password);
         * 
         * HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body,
         * headers);
         * 
         * try {
         * ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request,
         * Map.class);
         * return response.getBody();
         * } catch (Exception e) {
         * log.error("Failed to authenticate user with Keycloak: {}", e.getMessage());
         * throw new RuntimeException("Authentication failed");
         * }
         */
    }

    /**
     * Refresh access token using refresh token
     */
    public Map<String, Object> refreshToken(String refreshToken) {
        // Mock refresh
        log.warn("Mocking Keycloak token refresh");
        return Map.of(
                "access_token", "mock_access_token_refreshed_" + UUID.randomUUID(),
                "refresh_token", "mock_refresh_token_refreshed_" + UUID.randomUUID(),
                "expires_in", 3600,
                "refresh_expires_in", 86400,
                "token_type", "Bearer");
        /*
         * String tokenUrl = keycloakUrl + "/realms/" + realm +
         * "/protocol/openid-connect/token";
         * 
         * HttpHeaders headers = new HttpHeaders();
         * headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
         * 
         * MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
         * body.add("grant_type", "refresh_token");
         * body.add("client_id", clientId);
         * body.add("client_secret", clientSecret);
         * body.add("refresh_token", refreshToken);
         * 
         * HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body,
         * headers);
         * 
         * try {
         * ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request,
         * Map.class);
         * return response.getBody();
         * } catch (Exception e) {
         * log.error("Failed to refresh token: {}", e.getMessage());
         * throw new RuntimeException("Token refresh failed");
         * }
         */
    }

    /**
     * Register new user in Keycloak
     */
    public String registerUser(String email, String password, String firstName, String lastName) {
        log.info("Registering user in Keycloak: {}", email);
        // This would use Keycloak Admin REST API to create user
        // For simplicity, returning a mock UUID
        return UUID.randomUUID().toString();
    }

    /**
     * Logout user from Keycloak
     */
    public void logoutUser(String refreshToken) {
        // Mock logout
        log.warn("Mocking Keycloak logout");
        /*
         * String logoutUrl = keycloakUrl + "/realms/" + realm +
         * "/protocol/openid-connect/logout";
         * 
         * HttpHeaders headers = new HttpHeaders();
         * headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
         * 
         * MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
         * body.add("client_id", clientId);
         * body.add("client_secret", clientSecret);
         * body.add("refresh_token", refreshToken);
         * 
         * HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body,
         * headers);
         * 
         * try {
         * restTemplate.postForEntity(logoutUrl, request, String.class);
         * log.info("User logged out from Keycloak");
         * } catch (Exception e) {
         * log.error("Failed to logout user from Keycloak: {}", e.getMessage());
         * }
         */
    }
}
