package com.skillverse.reputation_service.controller;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillverse.reputation_service.entity.Review;
import com.skillverse.reputation_service.repository.ReviewRepository;
import com.skillverse.reputation_service.service.BadgeService;
import com.skillverse.reputation_service.service.ReputationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reputation/simulation")
@RequiredArgsConstructor
public class SimulationController {

    private final ReviewRepository reviewRepository;
    private final BadgeService badgeService;
    private final ReputationService reputationService;

    @PostMapping("/competence/{userId}")
    public ResponseEntity<String> simulateCompetence(@PathVariable UUID userId) {
        // Create 4 dummy reviews to trigger the 'Competent Professional' badge (min 4
        // reviews)
        for (int i = 1; i <= 4; i++) {
            Review review = Review.builder()
                    .id(UUID.randomUUID())
                    .reviewerId(UUID.randomUUID()) // Random reviewer
                    .targetUserId(userId)
                    .bookingId(UUID.randomUUID()) // Fake booking
                    .rating(5)
                    .title("Simulation Review " + i)
                    .body("Simulated review to verify competence rule.")
                    .isVerified(true)
                    .flagsCount(0)
                    .createdAt(OffsetDateTime.now())
                    .meta(Map.of("simulation", true))
                    .build();

            reviewRepository.save(review);
        }

        // Trigger updates
        reputationService.computeReputation(userId);
        badgeService.evaluateBadges(userId);

        return ResponseEntity.ok("Simulated 4 interactions. Badge should be granted.");
    }
}
