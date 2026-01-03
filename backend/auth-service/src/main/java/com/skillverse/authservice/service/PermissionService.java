package com.skillverse.authservice.service;

import com.skillverse.authservice.model.UserPermission;
import com.skillverse.authservice.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final UserService userService;

    /**
     * Grant permission to user
     */
    public void grantPermission(UUID userId, String permission) {
        // Verify user exists
        userService.getUserById(userId);
        
        // Check if permission already exists
        if (!permissionRepository.existsByUserIdAndPermission(userId, permission)) {
            UserPermission userPermission = UserPermission.builder()
                    .user(userService.getUserById(userId))
                    .permission(permission)
                    .build();
            permissionRepository.save(userPermission);
            log.info("Granted permission {} to user {}", permission, userId);
        }
    }

    /**
     * Revoke permission from user
     */
    public void revokePermission(UUID userId, String permission) {
        permissionRepository.deleteByUserIdAndPermission(userId, permission);
        log.info("Revoked permission {} from user {}", permission, userId);
    }

    /**
     * Get all permissions for user
     */
    public List<String> getUserPermissions(UUID userId) {
        return permissionRepository.findByUserId(userId).stream()
                .map(UserPermission::getPermission)
                .toList();
    }

    /**
     * Check if user has specific permission
     */
    public boolean hasPermission(UUID userId, String permission) {
        return permissionRepository.existsByUserIdAndPermission(userId, permission);
    }
}
