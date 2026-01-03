package com.skillverse.authservice.service;

import com.skillverse.authservice.event.EventPublisher;
import com.skillverse.authservice.model.User;
import com.skillverse.authservice.model.UserSession;
import com.skillverse.authservice.repository.UserSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private KeycloakService keycloakService;

    @Mock
    private UserService userService;

    @Mock
    private UserSessionRepository sessionRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private Map<String, Object> mockTokens;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .build();

        mockTokens = new HashMap<>();
        mockTokens.put("access_token", "mock-access-token");
        mockTokens.put("refresh_token", "mock-refresh-token");
        mockTokens.put("expires_in", 3600);
    }

    @Test
    void register_ShouldCreateUserSuccessfully() {
        // Arrange
        String keycloakUserId = UUID.randomUUID().toString();
        when(keycloakService.registerUser(any(), any(), any(), any())).thenReturn(keycloakUserId);
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        // Act
        User registeredUser = authService.register(
                "test@example.com",
                "testuser",
                "password123",
                "John",
                "Doe");

        // Assert
        assertNotNull(registeredUser);
        assertEquals("test@example.com", registeredUser.getEmail());
        verify(keycloakService, times(1)).registerUser(any(), any(), any(), any());
        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void login_ShouldAuthenticateUserSuccessfully() {
        // Arrange
        when(keycloakService.authenticateUser("test@example.com", "password123"))
                .thenReturn(mockTokens);
        when(userService.getUserByEmail("test@example.com")).thenReturn(testUser);
        when(sessionRepository.save(any(UserSession.class))).thenReturn(new UserSession());

        // Act
        Map<String, Object> result = authService.login("test@example.com", "password123");

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey("accessToken"));
        assertTrue(result.containsKey("refreshToken"));
        verify(keycloakService, times(1)).authenticateUser(any(), any());
        verify(sessionRepository, times(1)).save(any(UserSession.class));
        verify(eventPublisher, times(1)).publishUserEvent(any(), any(), any(), any());
    }

    @Test
    void logout_ShouldInvalidateSession() {
        // Arrange
        String token = "valid-token";
        UserSession session = UserSession.builder()
                .token(token)
                .refreshToken("refresh-token")
                .user(testUser)
                .isActive(true)
                .build();

        when(sessionRepository.findByTokenAndIsActiveTrue(token)).thenReturn(Optional.of(session));
        when(sessionRepository.save(any(UserSession.class))).thenReturn(session);

        // Act
        authService.logout(token);

        // Assert
        verify(sessionRepository, times(1)).save(any(UserSession.class));
        verify(keycloakService, times(1)).logoutUser(any());
        verify(eventPublisher, times(1)).publishUserEvent(any(), any(), any(), any());
    }

    @Test
    void validateToken_ShouldReturnTrue_WhenTokenIsValid() {
        // Arrange
        String validToken = "valid-token";
        UserSession session = UserSession.builder()
                .token(validToken)
                .isActive(true)
                .build();

        when(sessionRepository.findByTokenAndIsActiveTrue(validToken))
                .thenReturn(Optional.of(session));

        // Act
        boolean isValid = authService.validateToken(validToken);

        // Assert
        assertTrue(isValid);
        verify(sessionRepository, times(1)).findByTokenAndIsActiveTrue(validToken);
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenTokenIsInvalid() {
        // Arrange
        String invalidToken = "invalid-token";
        when(sessionRepository.findByTokenAndIsActiveTrue(invalidToken))
                .thenReturn(Optional.empty());

        // Act
        boolean isValid = authService.validateToken(invalidToken);

        // Assert
        assertFalse(isValid);
        verify(sessionRepository, times(1)).findByTokenAndIsActiveTrue(invalidToken);
    }
}
