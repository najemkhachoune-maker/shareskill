package com.skillverse.reputation_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skillverse.reputation_service.entity.Badge;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, UUID> {

    public Optional<Badge> findByName(String name);
    
 }
