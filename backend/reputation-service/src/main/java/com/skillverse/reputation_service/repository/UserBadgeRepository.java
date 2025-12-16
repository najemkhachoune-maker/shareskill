package com.skillverse.reputation_service.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skillverse.reputation_service.entity.UserBadge;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, UUID> {
    List<UserBadge> findByUserId(UUID userId);

    Optional<UserBadge> findByUserIdAndBadgeId(UUID userId, UUID badgeId);
}
