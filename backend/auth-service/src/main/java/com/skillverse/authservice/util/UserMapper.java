package com.skillverse.authservice.util;

import org.springframework.stereotype.Component;

import com.skillverse.authservice.dto.UserDTO;
import com.skillverse.authservice.model.User;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Setter
@Getter
@Component
@Slf4j

public class UserMapper {

    public static UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(user.getPhone())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .bio(user.getBio())
                .profilePictureUrl(user.getProfilePictureUrl())
                .isActive(user.getIsActive())
                .isEmailVerified(user.getIsEmailVerified())
                .hasLearnerProfile(user.getHasLearnerProfile())
                .hasTeacherProfile(user.getHasTeacherProfile())
                .learnerLevel(user.getLearnerLevel())
                .teacherLevel(user.getTeacherLevel())
                .availableTokens(user.getAvailableTokens())
                .totalTokensEarned(user.getTotalTokensEarned())
                .questsCompleted(user.getQuestsCompleted())
                .studentsTaught(user.getStudentsTaught())
                .averageRatingAsLearner(user.getAverageRatingAsLearner())
                .averageRatingAsTeacher(user.getAverageRatingAsTeacher())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }
}
