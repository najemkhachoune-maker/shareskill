package com.skillverse.reputation_service.controller;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillverse.reputation_service.dto.CreateReviewRequest;
import com.skillverse.reputation_service.entity.Review;
import com.skillverse.reputation_service.fraud.FraudService;
import com.skillverse.reputation_service.repository.ReviewRepository;
import com.skillverse.reputation_service.service.BadgeService;
import com.skillverse.reputation_service.service.ReputationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final ReputationService reputationService;
    private final FraudService fraudService;
    private final BadgeService badgeService;

    @PostMapping
    public ResponseEntity<Object> createReview(@Valid @RequestBody CreateReviewRequest req) {

        if (req.getReviewerId() == null || req.getTargetUserId() == null) {
            return ResponseEntity.badRequest()
                    .body("reviewerId and targetUserId are required");
        }

        if (req.getRating() < 1 || req.getRating() > 5) {
            return ResponseEntity.badRequest()
                    .body("rating must be between 1 and 5");
        }

        // 🚨 HARD RULE: booking is mandatory
        if (req.getBookingId() == null) {
            return ResponseEntity.badRequest()
                    .body("Review rejected: bookingId is required");
        }

        Review review = Review.builder()
                .id(UUID.randomUUID())
                .reviewerId(req.getReviewerId())
                .targetUserId(req.getTargetUserId())
                .bookingId(req.getBookingId())
                .rating(req.getRating())
                .title(req.getTitle())
                .body(req.getBody())
                .isVerified(Boolean.TRUE.equals(req.getIsVerified()))
                .meta(req.getMeta() != null ? req.getMeta() : Map.of())
                .flagsCount(0)
                .createdAt(OffsetDateTime.now())
                .build();

        // Duplicate protection
        if (fraudService.isDuplicateReview(review)) {
            return ResponseEntity.badRequest()
                    .body("Duplicate review detected");
        }

        // ✅ Save review ONLY if booking exists
        reviewRepository.save(review);

        // ✅ Update TARGET reputation only
        reputationService.computeReputation(req.getTargetUserId());

        // ✅ Evaluate badges
        badgeService.evaluateBadges(req.getTargetUserId());

        return ResponseEntity.status(201).body(review);
    }

    
    @GetMapping("/users/{userId}/reviews")
    public ResponseEntity<List<Review>> getUserReviews(@PathVariable UUID userId) {
        return ResponseEntity.ok(
                reviewRepository.findByTargetUserIdOrderByCreatedAtDesc(userId)
        );
    }
}

