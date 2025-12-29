package com.booking;

import com.booking.domain.BookingSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface BookingSlotRepository extends JpaRepository<BookingSlot, Long> {

    @Query("""
        select s.slotStartAt
        from BookingSlot s
        where s.resourceId = :resourceId
          and s.slotStartAt >= :from
          and s.slotStartAt < :to
    """)
    List<OffsetDateTime> findBookedSlotStarts(
            @Param("resourceId") Long resourceId,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to
    );

    @Modifying
    @Query("delete from BookingSlot s where s.booking.id = :bookingId")
    int deleteByBookingId(@Param("bookingId") Long bookingId);
}