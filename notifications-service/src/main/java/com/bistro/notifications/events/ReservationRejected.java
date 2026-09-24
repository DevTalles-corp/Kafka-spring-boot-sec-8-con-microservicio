package com.bistro.notifications.events;

import java.time.LocalDateTime;

public record ReservationRejected(
        Long reservationId,
        String reservationCode,
        String customerEmail,
        String reason,
        LocalDateTime occurredAt
) {
}
