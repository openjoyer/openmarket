package com.openjoyer.openmarket.contracts.events.payment;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequestedEvent(
        UUID orderId,
        String userId,
        BigDecimal amount
) {}
