package com.bistro.notifications.events;

import java.time.LocalDateTime;

public record ReservationCreated(
        Long reservationId,
        Integer partySize,
        LocalDateTime occurredAt
) {
}
