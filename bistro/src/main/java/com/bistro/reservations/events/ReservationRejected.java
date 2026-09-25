package com.bistro.reservations.events;

import org.springframework.modulith.events.Externalized;

import java.time.LocalDateTime;

@Externalized("reservation-rejected::#{#this.reservationId()}")
public record ReservationRejected(
        Long reservationId,
        String reservationCode,
        String customerEmail,
        String reason,
        LocalDateTime occurredAt
) {
}
