package com.skillverse.reputation_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skillverse.reputation_service.entity.Flag;

@Repository
public interface FlagRepository extends JpaRepository<Flag, UUID> {
    long countByReviewId(UUID reviewId);
    List<Flag> findByReviewId(UUID reviewId);
    List<Flag> findByReporterId(UUID reporterId);
}
