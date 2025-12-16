package com.skillverse.reputation_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillverse.reputation_service.entity.Badge;
import com.skillverse.reputation_service.service.BadgeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class BadgeController {

    private final BadgeService badgeService;

    @GetMapping("/{userId}/badges")
    public ResponseEntity<List<Badge>> getUserBadges(@PathVariable UUID userId) {
        return ResponseEntity.ok(badgeService.getUserBadges(userId));
    }
}
                    
