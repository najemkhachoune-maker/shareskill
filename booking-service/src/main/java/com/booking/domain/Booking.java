package com.booking.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "BOOKINGS")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="resource_id", nullable=false)
    private Long resourceId;

    @Column(name="customer_name", nullable=false, length=200)
    private String customerName;

    @Column(name="start_at", nullable=false)
    private OffsetDateTime startAt;

    @Column(name="end_at", nullable=false)
    private OffsetDateTime endAt;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable=false, length=20)
    private BookingStatus status;

    @Column(name="created_at", nullable=false)
    private OffsetDateTime createdAt;

    @Column(name="updated_at", nullable=false)
    private OffsetDateTime updatedAt;

    protected Booking() {}

    public Booking(Long resourceId, String customerName, OffsetDateTime startAt, OffsetDateTime endAt) {
        this.resourceId = resourceId;
        this.customerName = customerName;
        this.startAt = startAt;
        this.endAt = endAt;
        this.status = BookingStatus.CONFIRMED;
    }

    @PrePersist
    void prePersist() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) this.status = BookingStatus.CONFIRMED;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    public Long getId() { return id; }
    public Long getResourceId() { return resourceId; }
    public String getCustomerName() { return customerName; }
    public OffsetDateTime getStartAt() { return startAt; }
    public OffsetDateTime getEndAt() { return endAt; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
}