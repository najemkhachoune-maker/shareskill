package com.skillverse.reputation_service.repository;

import com.skillverse.reputation_service.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByTargetUserIdOrderByCreatedAtDesc(UUID targetUserId);
    
    List<Review> findByTargetUserId(UUID userId);
    
    List<Review> findByTargetUserIdAndCreatedAtAfter(UUID targetUserId, OffsetDateTime after);
    
    long countByTargetUserId(UUID targetUserId);
    
    List<Review> findByReviewerId(UUID reviewerId);
    
    List<Review> findByReviewerIdAndCreatedAtAfter(UUID reviewerId, OffsetDateTime after);
    
    boolean existsByReviewerIdAndTargetUserIdAndBookingId(
        UUID reviewerId, 
        UUID targetUserId, 
        UUID bookingId
    );
    
    // Fix NULL booking detection
    @Query("""
        SELECT COUNT(r) > 0 
        FROM Review r 
        WHERE r.reviewerId = ?1 
          AND r.targetUserId = ?2 
          AND r.bookingId IS NULL
    """)
    boolean existsDuplicateWithoutBooking(UUID reviewerId, UUID targetUserId);
    
    // Calculate average rating
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.targetUserId = ?1")
    Double findAverageRatingByTargetUserId(UUID userId);
    
    // Sum all flagsCount for a user
    @Query("SELECT COALESCE(SUM(r.flagsCount), 0) FROM Review r WHERE r.targetUserId = ?1")
    long sumFlagsByTargetUser(UUID userId);
}