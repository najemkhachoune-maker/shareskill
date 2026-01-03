package com.skillverse.matchingservice.controller;

import com.skillverse.matchingservice.dto.FindMatchesRequest;
import com.skillverse.matchingservice.dto.ProfileDTO;
import com.skillverse.matchingservice.entity.Match;
import com.skillverse.matchingservice.service.MatchingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matching")
public class MatchingController {

    private final MatchingService matchingService;

    public MatchingController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @GetMapping("/health")
    public String health() {
        return "Matching Service is up and running";
    }

    @PostMapping("/find")
    public List<Match> findMatches(@RequestBody FindMatchesRequest request) {
        return matchingService.findMatches(request.getUserProfile(), request.getAllProfiles());
    }

    @GetMapping("/users/{userId}")
    public List<Match> getUserMatches(@PathVariable Long userId) {
        return matchingService.getUserMatches(userId);
    }
}
