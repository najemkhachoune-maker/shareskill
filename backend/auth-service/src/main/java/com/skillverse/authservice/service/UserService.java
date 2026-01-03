package com.skillverse.authservice.service;

import com.skillverse.authservice.event.EventPublisher;
import com.skillverse.authservice.event.EventType;
import com.skillverse.authservice.exception.UserNotFoundException;
import com.skillverse.authservice.model.User;
import com.skillverse.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final EventPublisher eventPublisher;

    /**
     * Create new user
     */
    public User createUser(User user) {
        // Les timestamps sont gérés automatiquement par @PrePersist dans User.java
        User savedUser = userRepository.save(user);

        // Publish event
        Map<String, Object> payload = new HashMap<>();
        payload.put("firstName", savedUser.getFirstName());
        payload.put("lastName", savedUser.getLastName());
        payload.put("learnerLevel", savedUser.getLearnerLevel());
        payload.put("teacherLevel", savedUser.getTeacherLevel());

        try {
            eventPublisher.publishUserEvent(
                    EventType.USER_CREATED,
                    savedUser.getId(),
                    savedUser.getEmail(),
                    payload);
        } catch (Exception e) {
            log.error("Failed to publish USER_CREATED event for user: {}", savedUser.getEmail(), e);
        }

        log.info("Created user: {}", savedUser.getEmail());
        return savedUser;
    }

    /**
     * Get user by ID with caching
     */
    @Cacheable(value = "users", key = "#userId")
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
    }

    /**
     * Get user by email
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    /**
     * Get user by Keycloak ID
     */
    public User getUserByKeycloakId(String keycloakUserId) {
        return userRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found with Keycloak ID: " + keycloakUserId));
    }

    /**
     * Update user profile
     */
    @CacheEvict(value = "users", key = "#user.id")
    public User updateUser(User user) {
        User existingUser = getUserById(user.getId());

        // Update fields
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setBio(user.getBio());
        existingUser.setPhone(user.getPhone());
        existingUser.setProfilePictureUrl(user.getProfilePictureUrl());
        // updatedAt est géré automatiquement par @PreUpdate

        User updatedUser = userRepository.save(existingUser);

        // Publish event
        Map<String, Object> payload = new HashMap<>();
        payload.put("firstName", updatedUser.getFirstName());
        payload.put("lastName", updatedUser.getLastName());

        eventPublisher.publishUserEvent(
                EventType.USER_UPDATED,
                updatedUser.getId(),
                updatedUser.getEmail(),
                payload);

        log.info("Updated user: {}", updatedUser.getEmail());
        return updatedUser;
    }

    /**
     * Update last login timestamp
     */
    @CacheEvict(value = "users", key = "#userId")
    public void updateLastLogin(UUID userId) {
        User user = getUserById(userId);
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * Delete user (GDPR compliance - anonymization)
     */
    @CacheEvict(value = "users", key = "#userId")
    public void deleteUser(UUID userId) {
        User user = getUserById(userId);

        // Anonymize instead of deleting (GDPR)
        user.setDataAnonymized(true);
        user.setEmail("anonymized_" + userId + "@deleted.com");
        user.setFirstName("Anonymized");
        user.setLastName("User");
        user.setBio(null);
        user.setPhone(null);
        user.setProfilePictureUrl(null);
        user.setIsActive(false);

        userRepository.save(user);

        // Publish event
        eventPublisher.publishUserEvent(
                EventType.USER_DELETED,
                userId,
                user.getEmail(),
                Map.of("anonymized", true));

        log.info("Anonymized user: {}", userId);
    }

    /**
     * Calculate user level based on points
     */
    public void updateLearnerLevel(UUID userId, int totalPoints) {
        User user = getUserById(userId);
        int newLevel = (totalPoints / 100) + 1;
        user.setLearnerLevel(newLevel);
        userRepository.save(user);
    }

    public void updateTeacherLevel(UUID userId, int totalPoints) {
        User user = getUserById(userId);
        int newLevel = (totalPoints / 100) + 1;
        user.setTeacherLevel(newLevel);
        userRepository.save(user);
    }
}
