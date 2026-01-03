package com.skillverse.reputation_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skillverse.reputation_service.entity.ReputationScore;

@Repository
public interface ReputationScoreRepository extends JpaRepository<ReputationScore, UUID> {
}
