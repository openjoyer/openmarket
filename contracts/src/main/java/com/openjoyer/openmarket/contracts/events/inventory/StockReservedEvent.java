package com.openjoyer.openmarket.contracts.events.inventory;

import java.time.Instant;
import java.util.UUID;

public record StockReservedEvent(
        UUID orderId,
        Instant reservedAt
) {}
