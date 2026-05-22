package com.openjoyer.openmarket.order_service.domain.model;

public enum OrderStatus {
    CREATED,
    PAID,
    PACKED,
    IN_DELIVERY,
    DELIVERED,

    RECEIVED,
    CANCELED,
    REFUND_CREATED,
    REFUNDED,
    EXPIRED
}
