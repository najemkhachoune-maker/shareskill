package com.skillverse.authservice.repository;

import com.skillverse.authservice.model.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<UserPermission, UUID> {
    
    List<UserPermission> findByUserId(UUID userId);
    
    boolean existsByUserIdAndPermission(UUID userId, String permission);
    
    void deleteByUserIdAndPermission(UUID userId, String permission);
}
