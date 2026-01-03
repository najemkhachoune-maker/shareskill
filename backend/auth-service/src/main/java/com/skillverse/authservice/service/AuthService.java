package com.skillverse.authservice.service;

import com.skillverse.authservice.event.EventPublisher;
import com.skillverse.authservice.event.EventType;
import com.skillverse.authservice.model.User;
import com.skillverse.authservice.model.UserSession;
import com.skillverse.authservice.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final KeycloakService keycloakService;
    private final UserService userService;
    private final UserSessionRepository sessionRepository;
    private final EventPublisher eventPublisher;
    private final com.skillverse.authservice.util.JwtUtil jwtUtil;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    /**
     * Register new user
     */
    public User register(String email, String username, String password, String firstName, String lastName) {
        // Check if user exists
        if (userService.existsByEmail(email)) {
            throw new RuntimeException("User already exists");
        }

        // Create user in our database
        User user = User.builder()
                // .keycloakUserId(UUID.randomUUID().toString())
                .email(email)
                .username(username)
                .password(passwordEncoder.encode(password))
                .firstName(firstName)
                .lastName(lastName)
                .isActive(true)
                .isEmailVerified(false) // TODO: Email verification
                .gdprConsentDate(LocalDateTime.now())
                .build();

        return userService.createUser(user);
    }

    /**
     * Login user and create session
     */
    public Map<String, Object> login(String email, String password) {
        // Get user from database
        User user = userService.getUserByEmail(email);

        if (user.getPassword() == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Generate tokens
        Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("roles", java.util.List.of("ROLE_USER"));
        claims.put("userId", user.getId().toString());

        String accessToken = jwtUtil.generateToken(email, claims, 86400000); // 24h
        String refreshToken = jwtUtil.generateToken(email, claims, 604800000); // 7 days

        // Create session
        UserSession session = UserSession.builder()
                .user(user)
                .token(accessToken)
                .refreshToken(refreshToken)
                .isActive(true)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();
        sessionRepository.save(session);

        // Update last login
        userService.updateLastLogin(user.getId());

        // Publish event
        eventPublisher.publishUserEvent(
                EventType.USER_LOGIN,
                user.getId(),
                user.getEmail(),
                Map.of("loginTime", LocalDateTime.now()));

        log.info("User logged in: {}", email);

        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "expiresIn", 86400,
                "userId", user.getId());
    }

    /**
     * Refresh access token
     */
    public Map<String, Object> refresh(String refreshToken) {
        // Validate session
        UserSession session = sessionRepository.findByRefreshTokenAndIsActiveTrue(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        // Validate token signature
        if (jwtUtil.isTokenExpired(refreshToken)) {
            throw new RuntimeException("Refresh token expired");
        }

        String email = jwtUtil.extractUsername(refreshToken);
        User user = userService.getUserByEmail(email);

        // Generate new tokens
        Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("roles", java.util.List.of("ROLE_USER"));
        claims.put("userId", user.getId().toString());

        String newAccessToken = jwtUtil.generateToken(email, claims, 86400000);
        String newRefreshToken = jwtUtil.generateToken(email, claims, 604800000); // Rotate refresh token

        // Update session
        session.setToken(newAccessToken);
        session.setRefreshToken(newRefreshToken);
        session.setExpiresAt(LocalDateTime.now().plusDays(1));
        sessionRepository.save(session);

        return Map.of(
                "accessToken", newAccessToken,
                "refreshToken", newRefreshToken,
                "expiresIn", 86400);
    }

    /**
     * Logout user and invalidate session
     */
    public void logout(String token) {
        UserSession session = sessionRepository.findByTokenAndIsActiveTrue(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        // Invalidate session
        session.setIsActive(false);
        sessionRepository.save(session);

        // No Keycloak logout needed

        // Publish event
        eventPublisher.publishUserEvent(
                EventType.USER_LOGOUT,
                session.getUser().getId(),
                session.getUser().getEmail(),
                Map.of("logoutTime", LocalDateTime.now()));

        log.info("User logged out: {}", session.getUser().getEmail());
    }

    /**
     * Validate token for other microservices
     */
    public boolean validateToken(String token) {
        try {
            return !jwtUtil.isTokenExpired(token) && sessionRepository.findByTokenAndIsActiveTrue(token).isPresent();
        } catch (Exception e) {
            return false;
        }
    }
}