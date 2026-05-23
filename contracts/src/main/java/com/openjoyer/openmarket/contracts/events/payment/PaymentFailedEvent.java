package com.openjoyer.openmarket.contracts.events.payment;

import java.util.UUID;

public record PaymentFailedEvent(UUID orderId){}
