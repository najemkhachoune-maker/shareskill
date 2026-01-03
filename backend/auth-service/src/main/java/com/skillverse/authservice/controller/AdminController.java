package com.skillverse.authservice.controller;

import com.skillverse.authservice.dto.PermissionRequest;
import com.skillverse.authservice.dto.UserDTO;
import com.skillverse.authservice.model.User;
import com.skillverse.authservice.service.PermissionService;
import com.skillverse.authservice.service.UserService;
import com.skillverse.authservice.util.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin endpoints")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final PermissionService permissionService;

    @PostMapping("/users/{id}/permissions")
    @Operation(summary = "Grant permission to user")
    public ResponseEntity<Map<String, String>> grantPermission(
            @PathVariable UUID id,
            @Valid @RequestBody PermissionRequest request) {
        permissionService.grantPermission(id, request.getPermission());
        return ResponseEntity.ok(Map.of("message", "Permission granted successfully"));
    }

    @DeleteMapping("/users/{id}/permissions/{permission}")
    @Operation(summary = "Revoke permission from user")
    public ResponseEntity<Map<String, String>> revokePermission(
            @PathVariable UUID id,
            @PathVariable String permission) {
        permissionService.revokePermission(id, permission);
        return ResponseEntity.ok(Map.of("message", "Permission revoked successfully"));
    }

    @GetMapping("/users/{id}/permissions")
    @Operation(summary = "Get user permissions")
    public ResponseEntity<List<String>> getUserPermissions(@PathVariable UUID id) {
        List<String> permissions = permissionService.getUserPermissions(id);
        return ResponseEntity.ok(permissions);
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete user (GDPR anonymization)")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User anonymized successfully"));
    }
}
