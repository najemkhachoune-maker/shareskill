package com.skillverse.reputation_service.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "flags")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Flag {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "review_id", columnDefinition = "uuid", nullable = false)
    private UUID reviewId;

    @Column(name = "reporter_id", columnDefinition = "uuid")
    private UUID reporterId;

    @Column(columnDefinition = "text")
    private String reason;
    
    @Builder.Default
    private String status = "open";

    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}
