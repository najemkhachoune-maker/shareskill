package com.skillverse.authservice.repository;

import com.skillverse.authservice.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {
    
    Optional<UserSession> findByTokenAndIsActiveTrue(String token);
    
    Optional<UserSession> findByRefreshTokenAndIsActiveTrue(String refreshToken);
    
    // Méthode supprimée car non implémentable sans requête personnalisée

}