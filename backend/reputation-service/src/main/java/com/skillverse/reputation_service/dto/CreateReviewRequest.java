package com.skillverse.reputation_service.dto;

import java.util.Map;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateReviewRequest {
    
    @NotNull
    private UUID reviewerId;

    @NotNull
    private UUID targetUserId;

    @NotNull
    private UUID bookingId; 

    private int rating;
    private String title;
    private String body;
    private Boolean isVerified;
    private Map<String, Object> meta;

}
