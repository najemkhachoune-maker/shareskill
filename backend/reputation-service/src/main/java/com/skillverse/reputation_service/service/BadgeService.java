package com.skillverse.reputation_service.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillverse.reputation_service.entity.Badge;
import com.skillverse.reputation_service.entity.Review;
import com.skillverse.reputation_service.entity.UserBadge;
import com.skillverse.reputation_service.repository.BadgeRepository;
import com.skillverse.reputation_service.repository.ReviewRepository;
import com.skillverse.reputation_service.repository.UserBadgeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final ReviewRepository reviewRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    private static final String CRITERIA_MIN_REVIEWS = "minReviews";
    private static final String CRITERIA_MIN_AVG_RATING = "minAvgRating";
    private static final String CRITERIA_MAX_FLAGS = "maxFlags";
    private static final String CRITERIA_MIN_VERIFIED = "minVerified";

    // =========================
    // PUBLIC API
    // =========================
    @Transactional
    public void evaluateBadges(UUID userId) {

        List<Badge> badges = badgeRepository.findAll();

        Set<UUID> alreadyGranted = userBadgeRepository.findByUserId(userId)
                .stream()
                .map(UserBadge::getBadgeId)
                .collect(Collectors.toSet());

        List<Review> given = reviewRepository.findByReviewerId(userId);
        List<Review> received = reviewRepository.findByTargetUserId(userId);

        for (Badge badge : badges) {
            if (alreadyGranted.contains(badge.getId())) continue;

            if (meetsCriteria(badge, given, received)) {
                grantBadge(userId, badge);
            }
        }
    }

    // =========================
    // CORE RULE ENGINE
    // =========================
    private boolean meetsCriteria(
            Badge badge,
            List<Review> given,
            List<Review> received
    ) {
        try {
            Map<String, Object> c = mapper.readValue(
                    badge.getCriteria(),
                    new TypeReference<>() {}
            );

            String type = (String) c.get("type");
            String side = (String) c.get("side");

            List<Review> reviews = "given".equals(side) ? given : received;

            return switch (type) {
                case "review_count" -> reviewCount(reviews, c);
                case "avg_rating" -> avgRating(reviews, c);
                case "flags" -> flags(reviews, c);
                case "verified" -> verified(reviews, c);
                default -> false;
            };

        } catch (JsonProcessingException e) {
            return false;
        }
    }

    // =========================
    // RULE IMPLEMENTATIONS
    // =========================
 
    private boolean reviewCount(List<Review> reviews, Map<String, Object> c) {
        int min = ((Number) c.get(CRITERIA_MIN_REVIEWS)).intValue();
        return reviews.size() >= min;
    }

    private boolean avgRating(List<Review> reviews, Map<String, Object> c) {
        int minReviews = ((Number) c.get(CRITERIA_MIN_REVIEWS)).intValue();
        double minAvg = ((Number) c.get(CRITERIA_MIN_AVG_RATING)).doubleValue();

        if (reviews.size() < minReviews) return false;

        double avg = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0);

        return avg >= minAvg;
    }


    private boolean flags(List<Review> reviews, Map<String, Object> c) {
        int minReviews = ((Number) c.get(CRITERIA_MIN_REVIEWS)).intValue();
        int maxFlags = ((Number) c.get(CRITERIA_MAX_FLAGS)).intValue();

        if (reviews.size() < minReviews) return false;

        int totalFlags = reviews.stream()
                .mapToInt(Review::getFlagsCount)
                .sum();

        return totalFlags <= maxFlags;
    }


    private boolean verified(List<Review> reviews, Map<String, Object> c) {
        int minVerified = ((Number) c.get(CRITERIA_MIN_VERIFIED)).intValue();

        long count = reviews.stream()
                .filter(r -> Boolean.TRUE.equals(r.isVerified()))
                .count();

        return count >= minVerified;
    }


    // =========================
    // GRANT
    // =========================
    private void grantBadge(UUID userId, Badge badge) {
        userBadgeRepository.save(
                UserBadge.builder()
                        .id(UUID.randomUUID())
                        .userId(userId)
                        .badgeId(badge.getId())
                        .grantedAt(OffsetDateTime.now())
                        .meta(Map.of())
                        .build()
        );
    }

    // =========================
    // READ
    // =========================
    public List<Badge> getUserBadges(UUID userId) {
        List<UUID> badgeIds = userBadgeRepository.findByUserId(userId)
                .stream()
                .map(UserBadge::getBadgeId)
                .toList();

        return badgeIds.isEmpty()
                ? List.of()
                : badgeRepository.findAllById(badgeIds);
    }
}
