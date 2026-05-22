package com.openjoyer.openmarket.order_service.domain.exceptions;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String orderId) {
        super("order not found, id: " + orderId);
    }
}
