package com.skillverse.reputation_service.service;

import com.skillverse.reputation_service.entity.ReputationScore;
import com.skillverse.reputation_service.entity.Review;
import com.skillverse.reputation_service.repository.ReputationScoreRepository;
import com.skillverse.reputation_service.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReputationService {
    private final ReviewRepository reviewRepository;
    private final ReputationScoreRepository reputationScoreRepository;
    
    @Transactional
    public ReputationScore computeReputation(UUID userId) {
        List<Review> reviews = reviewRepository.findByTargetUserIdOrderByCreatedAtDesc(userId);
        
        double avgRating = reviews.isEmpty() ? 0.0 : 
            reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        int nReviews = reviews.size();
        
        double ratingComponent = (avgRating / 5.0) * 50.0;

        double countComponent = nReviews > 0 ? 
            Math.min(Math.log10((double) nReviews + 1) / 2.0, 1.0) * 10.0 : 0.0;

        
        OffsetDateTime ninetyDaysAgo = OffsetDateTime.now().minusDays(90);
        long recentCount = reviews.stream()
            .filter(r -> r.getCreatedAt() != null && r.getCreatedAt().isAfter(ninetyDaysAgo))
            .count();

        double activityComponent = Math.min(recentCount / 10.0, 1.0) * 15.0;
        
        int flagsTotal = reviews.stream().mapToInt(Review::getFlagsCount).sum();
        double fraudPenalty = nReviews == 0 ? 0.0 :
            Math.min(flagsTotal / (double) nReviews, 1.0) * 25.0;

        
        double bonus = 0.0;
        double score = Math.max(0.0, Math.min(100.0, 
            ratingComponent + countComponent + activityComponent - fraudPenalty + bonus));
        
        Map<String, Object> components = new LinkedHashMap<>();
        components.put("ratingComponent", ratingComponent);
        components.put("countComponent", countComponent);
        components.put("activityComponent", activityComponent);
        components.put("fraudPenalty", fraudPenalty);
        components.put("bonus", bonus);
        components.put("avgRating", avgRating);
        components.put("nReviews", nReviews);
        components.put("flagsTotal", flagsTotal);
        
        ReputationScore rs = ReputationScore.builder()
            .userId(userId)
            .score(score)
            .components(components) // pass Map directly
            .updatedAt(OffsetDateTime.now())
            .build();
        
        reputationScoreRepository.save(rs);
        return rs;
    }
    
    public Optional<ReputationScore> getReputation(UUID userId) {
        return reputationScoreRepository.findById(userId);
    }
}