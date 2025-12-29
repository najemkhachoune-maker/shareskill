package com.booking.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(
    name = "BOOKING_SLOTS",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_resource_slot_start",
        columnNames = {"resource_id", "slot_start_at"}
    )
)
public class BookingSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resource_id", nullable = false)
    private Long resourceId;

    @Column(name = "slot_start_at", nullable = false)
    private OffsetDateTime slotStartAt;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    protected BookingSlot() {}

    public BookingSlot(Long resourceId, OffsetDateTime slotStartAt, Booking booking) {
        this.resourceId = resourceId;
        this.slotStartAt = slotStartAt;
        this.booking = booking;
    }

    public Long getId() { return id; }
    public Long getResourceId() { return resourceId; }
    public OffsetDateTime getSlotStartAt() { return slotStartAt; }
    public Booking getBooking() { return booking; }
}