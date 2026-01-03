package com.skillverse.authservice.service;

import com.skillverse.authservice.event.EventPublisher;
import com.skillverse.authservice.exception.UserNotFoundException;
import com.skillverse.authservice.model.User;
import com.skillverse.authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = User.builder()
                .id(userId)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .learnerLevel(1)
                .teacherLevel(1)
                .build();
    }

    @Test
    void createUser_ShouldCreateUserSuccessfully() {
        // Arrange
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User createdUser = userService.createUser(testUser);

        // Assert
        assertNotNull(createdUser);
        assertEquals("test@example.com", createdUser.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
        verify(eventPublisher, times(1)).publishUserEvent(any(), any(), any(), any());
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        User foundUser = userService.getUserById(userId);

        // Assert
        assertNotNull(foundUser);
        assertEquals(userId, foundUser.getId());
        assertEquals("test@example.com", foundUser.getEmail());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserByEmail_ShouldReturnUser_WhenUserExists() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        User foundUser = userService.getUserByEmail("test@example.com");

        // Assert
        assertNotNull(foundUser);
        assertEquals("test@example.com", foundUser.getEmail());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void updateUser_ShouldUpdateUserSuccessfully() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User updateData = User.builder()
                .id(userId)
                .firstName("Jane")
                .lastName("Smith")
                .bio("Updated bio")
                .build();

        // Act
        User updatedUser = userService.updateUser(updateData);

        // Assert
        assertNotNull(updatedUser);
        verify(userRepository, times(1)).save(any(User.class));
        verify(eventPublisher, times(1)).publishUserEvent(any(), any(), any(), any());
    }

    @Test
    void deleteUser_ShouldAnonymizeUser() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository, times(1)).save(any(User.class));
        verify(eventPublisher, times(1)).publishUserEvent(any(), any(), any(), any());
    }

    @Test
    void updateLearnerLevel_ShouldCalculateLevelCorrectly() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.updateLearnerLevel(userId, 250);

        // Assert
        verify(userRepository, times(1)).save(any(User.class));
    }
}
