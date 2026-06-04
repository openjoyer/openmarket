package com.openjoyer.openmarket.contracts.events.order;

import java.time.Instant;
import java.util.UUID;

public record OrderCompletedEvent(
        UUID orderId,
        Instant completedAt
) {}
