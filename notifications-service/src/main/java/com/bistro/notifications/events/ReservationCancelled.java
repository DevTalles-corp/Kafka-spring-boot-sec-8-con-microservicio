package com.bistro.notifications.events;

import java.time.LocalDateTime;

public record ReservationCancelled(
        Long reservationId,
        String reservationCode,
        String customerEmail,
        LocalDateTime occurredAt
) {
}
