package com.booking.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "RESOURCES")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=200)
    private String name;

    @Column(nullable=false, length=64)
    private String timezone;

    @Column(name="created_at", nullable=false)
    private OffsetDateTime createdAt;

    protected Resource() {}

    @PrePersist
    void prePersist() {
        this.createdAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getTimezone() { return timezone; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}