package com.openjoyer.openmarket.order_service.domain.exceptions;

import java.util.UUID;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(UUID orderId) {
        super("order not found, id: " + orderId);
    }
}
