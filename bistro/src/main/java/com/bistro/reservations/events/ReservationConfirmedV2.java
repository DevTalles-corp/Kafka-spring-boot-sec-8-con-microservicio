package com.bistro.reservations.events;

import org.springframework.modulith.events.Externalized;

import java.time.LocalDateTime;

@Externalized("reservation-confirmed-v2::#{#this.reservationId()}")
public record ReservationConfirmedV2(
        Long reservationId,
        String reservationCode,
        String customerEmail,
        String customerName,
        String assignedTable,
        LocalDateTime reservationTime,
        LocalDateTime occurredAt
) {
}
