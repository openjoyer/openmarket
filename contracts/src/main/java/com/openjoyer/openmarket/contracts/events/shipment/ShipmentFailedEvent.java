package com.openjoyer.openmarket.contracts.events.shipment;

import java.time.Instant;
import java.util.UUID;

public record ShipmentFailedEvent (
        UUID orderId,
        Instant failedAt
){}
