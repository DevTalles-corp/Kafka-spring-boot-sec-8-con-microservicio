package com.bistro.reservations.events;

import org.springframework.modulith.events.Externalized;

import java.time.LocalDateTime;

@Externalized("reservation-cancelled::#{#this.reservationId()}")
public record ReservationCancelled(
        Long reservationId,
        String reservationCode,
        String customerEmail,
        LocalDateTime occurredAt
) {
}
