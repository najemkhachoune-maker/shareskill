package com.skillverse.authservice.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "keycloak_user_id")
    private String keycloakUserId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true)
    private String username;

    @Column(nullable = true)
    private String password;

    @Column(length = 20)
    private String phone;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "profile_picture_url", columnDefinition = "TEXT")
    private String profilePictureUrl;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_email_verified")
    @Builder.Default
    private Boolean isEmailVerified = false;

    @Column(name = "has_learner_profile")
    @Builder.Default
    private Boolean hasLearnerProfile = false;

    @Column(name = "has_teacher_profile")
    @Builder.Default
    private Boolean hasTeacherProfile = false;

    @Column(name = "learner_level")
    @Builder.Default
    private Integer learnerLevel = 1;

    @Column(name = "teacher_level")
    @Builder.Default
    private Integer teacherLevel = 1;

    @Column(name = "available_tokens")
    @Builder.Default
    private Integer availableTokens = 0;

    @Column(name = "total_tokens_earned")
    @Builder.Default
    private Integer totalTokensEarned = 0;

    @Column(name = "quests_completed")
    @Builder.Default
    private Integer questsCompleted = 0;

    @Column(name = "students_taught")
    @Builder.Default
    private Integer studentsTaught = 0;

    @Column(name = "average_rating_as_learner", precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal averageRatingAsLearner = BigDecimal.ZERO;

    @Column(name = "average_rating_as_teacher", precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal averageRatingAsTeacher = BigDecimal.ZERO;

    @Column(name = "gdpr_consent_date")
    private LocalDateTime gdprConsentDate;

    @Column(name = "data_anonymized")
    @Builder.Default
    private Boolean dataAnonymized = false;

    @Column(name = "encrypted_personal_data", columnDefinition = "TEXT")
    private String encryptedPersonalData;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
