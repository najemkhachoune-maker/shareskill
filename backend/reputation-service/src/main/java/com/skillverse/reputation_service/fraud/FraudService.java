package com.skillverse.reputation_service.fraud;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skillverse.reputation_service.entity.Flag;
import com.skillverse.reputation_service.entity.Review;
import com.skillverse.reputation_service.repository.FlagRepository;
import com.skillverse.reputation_service.repository.ReviewRepository;
import com.skillverse.reputation_service.service.ReputationService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FraudService {
    private final ReviewRepository reviewRepository;
    private final ReputationService reputationService;
    private final FlagRepository flagRepository;
    
    private static final int MAX_REVIEWS_PER_MINUTE = 5;
    
    /**
     * User reports a review as fraudulent
     */
    @Transactional
    public Flag reportReview(UUID reviewId, UUID reporterId, String reason) {
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);
        if (reviewOpt.isEmpty()) {
            throw new EntityNotFoundException("Review not found");
        }
        
        Flag flag = Flag.builder()
            .id(UUID.randomUUID())
            .reviewId(reviewId)
            .reporterId(reporterId)
            .reason(reason)
            .status("open")
            .createdAt(OffsetDateTime.now())
            .build();
        
        flagRepository.save(flag);
        
        Review review = reviewOpt.get();
        review.setFlagsCount(review.getFlagsCount() + 1);
        
        reviewRepository.save(review);
        
        reputationService.computeReputation(review.getTargetUserId());
        
        return flag;
    }
    
    /**
     * Detect suspicious spam behavior (>5 reviews per minute)
     */
    @Transactional
    public void checkFraud(UUID reviewerId) {
        OffsetDateTime oneMinuteAgo = OffsetDateTime.now().minusMinutes(1);
        List<Review> recentReviews = reviewRepository.findByReviewerIdAndCreatedAtAfter(
            reviewerId, oneMinuteAgo
        );
        
        if (recentReviews.size() > MAX_REVIEWS_PER_MINUTE) {
            // Penalize each target user
            for (Review review : recentReviews) {
                reputationService.computeReputation(review.getTargetUserId());
            }
        }
    }
    
    /**
     * Duplicate review = same reviewer + same target + same booking
     */
    public boolean isDuplicateReview(Review review) {
        UUID reviewer = review.getReviewerId();
        UUID target = review.getTargetUserId();
        UUID booking = review.getBookingId();
        
        if (booking == null) {
            // Custom query required for NULL
            return reviewRepository.existsDuplicateWithoutBooking(reviewer, target);
        }
        
        return reviewRepository.existsByReviewerIdAndTargetUserIdAndBookingId(
            reviewer, target, booking
        );
    }
}