package com.skillverse.reputation_service.entity;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "reviewer_id", columnDefinition = "uuid", nullable = false)
    private UUID reviewerId;

    @Column(name = "target_user_id", columnDefinition = "uuid", nullable = false)
    private UUID targetUserId;

    @Column(name = "booking_id", columnDefinition = "uuid", nullable = false)
    private UUID bookingId;

    @Column(nullable = false)
    private int rating;

    private String title;

    @Column(columnDefinition = "text")
    private String body;

    @Column(name = "is_verified")
    @Builder.Default
    @SuppressWarnings("unused")
    private boolean isVerified = false;

    @Column(name = "flags_count")
    @Builder.Default
    private int flagsCount = 0;
    
    @Column(name = "meta", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private Map<String, Object> meta = new HashMap<>();

    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}