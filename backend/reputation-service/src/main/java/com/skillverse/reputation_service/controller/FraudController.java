package com.skillverse.reputation_service.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skillverse.reputation_service.entity.Flag;
import com.skillverse.reputation_service.fraud.FraudService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class FraudController {
    private final FraudService fraudService;
    
    @PostMapping("/{id}/flag")
    public Flag flagReview(
        @PathVariable UUID id,
        @RequestParam UUID reporterId,
        @RequestParam(required = false) String reason
    ) {
        return fraudService.reportReview(id, reporterId, reason);
    }
}