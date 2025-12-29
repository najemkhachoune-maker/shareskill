package com.booking.service;

import com.booking.BookingRepository;
import com.booking.BookingSlotRepository;
import com.booking.ResourceRepository;
import com.booking.domain.*;
import com.booking.exception.ConflictException;
import com.booking.exception.NotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class BookingService {

    private static final int SLOT_MINUTES = 15;

    private final BookingRepository bookingRepository;
    private final BookingSlotRepository bookingSlotRepository;
    private final ResourceRepository resourceRepository;

    public BookingService(BookingRepository bookingRepository,
                          BookingSlotRepository bookingSlotRepository,
                          ResourceRepository resourceRepository) {
        this.bookingRepository = bookingRepository;
        this.bookingSlotRepository = bookingSlotRepository;
        this.resourceRepository = resourceRepository;
    }

    public record CreateBookingRequest(Long resourceId, String customerName, OffsetDateTime startAt, OffsetDateTime endAt) {}
    public record CreateBookingResponse(Long bookingId, BookingStatus status) {}
    public record AvailabilityResponse(Long resourceId, OffsetDateTime from, OffsetDateTime to, int slotMinutes, List<OffsetDateTime> freeSlots) {}

    @Transactional(readOnly = true)
    public Booking getById(Long bookingId) {
        if (bookingId == null) throw new IllegalArgumentException("bookingId is required");
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));
    }

    @Transactional
    public CreateBookingResponse createBooking(CreateBookingRequest req) {
        if (req.resourceId() == null) throw new IllegalArgumentException("resourceId is required");
        if (req.customerName() == null || req.customerName().isBlank()) throw new IllegalArgumentException("customerName is required");
        if (req.startAt() == null || req.endAt() == null) throw new IllegalArgumentException("startAt and endAt are required");

        resourceRepository.findById(req.resourceId())
                .orElseThrow(() -> new NotFoundException("Resource not found: " + req.resourceId()));

        OffsetDateTime startUtc = req.startAt().withOffsetSameInstant(ZoneOffset.UTC);
        OffsetDateTime endUtc   = req.endAt().withOffsetSameInstant(ZoneOffset.UTC);

        if (!startUtc.isBefore(endUtc)) throw new IllegalArgumentException("startAt must be before endAt");

        long minutes = Duration.between(startUtc, endUtc).toMinutes();
        if (minutes <= 0 || minutes % SLOT_MINUTES != 0) {
            throw new IllegalArgumentException("Duration must be a positive multiple of 15 minutes");
        }

        Booking booking = new Booking(req.resourceId(), req.customerName().trim(), startUtc, endUtc);
        booking = bookingRepository.save(booking);

        List<BookingSlot> slots = new ArrayList<>();
        OffsetDateTime cursor = startUtc;
        while (cursor.isBefore(endUtc)) {
            slots.add(new BookingSlot(req.resourceId(), cursor, booking));
            cursor = cursor.plusMinutes(SLOT_MINUTES);
        }

        try {
            bookingSlotRepository.saveAll(slots);
            bookingSlotRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("Time slot already booked for this resource");
        }

        return new CreateBookingResponse(booking.getId(), booking.getStatus());
    }

    @Transactional(readOnly = true)
    public AvailabilityResponse getAvailability(Long resourceId, OffsetDateTime from, OffsetDateTime to) {
        if (resourceId == null) throw new IllegalArgumentException("resourceId is required");
        if (from == null || to == null) throw new IllegalArgumentException("from and to are required");

        resourceRepository.findById(resourceId)
                .orElseThrow(() -> new NotFoundException("Resource not found: " + resourceId));

        OffsetDateTime fromUtc = from.withOffsetSameInstant(ZoneOffset.UTC);
        OffsetDateTime toUtc   = to.withOffsetSameInstant(ZoneOffset.UTC);
        if (!fromUtc.isBefore(toUtc)) throw new IllegalArgumentException("from must be before to");

        List<OffsetDateTime> all = new ArrayList<>();
        OffsetDateTime cursor = fromUtc;
        while (cursor.isBefore(toUtc)) {
            all.add(cursor);
            cursor = cursor.plusMinutes(SLOT_MINUTES);
        }

        List<OffsetDateTime> booked = bookingSlotRepository.findBookedSlotStarts(resourceId, fromUtc, toUtc);
        Set<OffsetDateTime> bookedSet = new HashSet<>(booked);

        List<OffsetDateTime> free = new ArrayList<>();
        for (OffsetDateTime s : all) {
            if (!bookedSet.contains(s)) free.add(s);
        }

        return new AvailabilityResponse(resourceId, fromUtc, toUtc, SLOT_MINUTES, free);
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));

        if (booking.getStatus() == BookingStatus.CANCELED) return;

        booking.setStatus(BookingStatus.CANCELED);
        bookingRepository.save(booking);

        bookingSlotRepository.deleteByBookingId(bookingId);
    }
}
