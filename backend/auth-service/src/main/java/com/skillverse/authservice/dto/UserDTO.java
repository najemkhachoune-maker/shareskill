package com.skillverse.authservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private UUID id;
    private String email;
    private String username;
    private String phone;
    private String firstName;
    private String lastName;
    private String bio;
    private String profilePictureUrl;
    private Boolean isActive;
    private Boolean isEmailVerified;
    private Boolean hasLearnerProfile;
    private Boolean hasTeacherProfile;
    private Integer learnerLevel;
    private Integer teacherLevel;
    private Integer availableTokens;
    private Integer totalTokensEarned;
    private Integer questsCompleted;
    private Integer studentsTaught;
    private BigDecimal averageRatingAsLearner;
    private BigDecimal averageRatingAsTeacher;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}
