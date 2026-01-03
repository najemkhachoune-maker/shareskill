package com.skillverse.reputation_service.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillverse.reputation_service.dto.ReputationResponse;
import com.skillverse.reputation_service.service.ReputationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reputation")
@RequiredArgsConstructor
public class ReputationController {

    private final ReputationService reputationService;

    @GetMapping("/health")
    public String health() {
        return "Reputation Service is up and running";
    }

    @GetMapping("/{userId}/reputation")
    public ResponseEntity<ReputationResponse> getReputation(@PathVariable UUID userId) {
        return reputationService.getReputation(userId)
                .map(r -> ResponseEntity.ok(
                        new ReputationResponse(userId, r.getScore(), r.getComponents())))
                .orElse(ResponseEntity.ok(
                        new ReputationResponse(userId, 0.0, Map.of())));
    }
}
