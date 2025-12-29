package com.skillverse.reputation_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;

import com.skillverse.reputation_service.repository.ReviewRepository;
import com.skillverse.reputation_service.service.ReputationService;


@SpringBootTest
@ActiveProfiles("test")
class ReputationServiceApplicationTests {

    @Autowired
    private ReviewRepository reviewRepository; // ✅ now will NOT be null

    @Autowired
    private ReputationService reputationService;

    public ReputationServiceApplicationTests() {
    }

    @Test
    void contextLoads() {
        org.junit.jupiter.api.Assertions.assertNotNull(reviewRepository);
        org.junit.jupiter.api.Assertions.assertNotNull(reputationService);
    }
}
