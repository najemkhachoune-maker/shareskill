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
@Table(name = "badges")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Badge {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    private String name;
    private String description;

    @Column(columnDefinition = "jsonb")
    private String criteria;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}
