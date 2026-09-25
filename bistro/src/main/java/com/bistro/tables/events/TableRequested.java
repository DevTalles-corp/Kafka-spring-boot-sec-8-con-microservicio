package com.bistro.tables.events;

import java.time.LocalDateTime;

public record TableRequested(
        Long reservationId,
        Integer partySize,
        LocalDateTime occurredAt
) {
}
