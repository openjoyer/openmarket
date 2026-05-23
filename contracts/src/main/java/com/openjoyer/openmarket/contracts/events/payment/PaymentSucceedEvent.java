package com.openjoyer.openmarket.contracts.events.payment;

import java.time.Instant;
import java.util.UUID;

public record PaymentSucceedEvent (
        UUID orderId,
        Instant paidAt
) {}
