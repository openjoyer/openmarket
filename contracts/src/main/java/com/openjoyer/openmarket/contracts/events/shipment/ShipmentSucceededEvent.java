package com.openjoyer.openmarket.contracts.events.shipment;

import java.time.Instant;
import java.util.UUID;

public record ShipmentSucceededEvent(
        UUID orderId,
        Instant deliveredAt
){}
