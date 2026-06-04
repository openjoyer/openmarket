package com.openjoyer.openmarket.contracts.events.inventory;

import java.util.UUID;

public record StockReleaseRequestedEvent(
        UUID orderId
) {}
