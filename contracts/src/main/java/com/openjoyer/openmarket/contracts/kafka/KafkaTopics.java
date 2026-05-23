package com.openjoyer.openmarket.contracts.kafka;

public final class KafkaTopics {
    public static final String ORDER_CREATED = "order.created";
    public static final String ORDER_CANCELLED = "order.cancelled";
    public static final String ORDER_COMPLETED = "order.completed";

    public static final String PAYMENT_SUCCEEDED = "payment.succeeded";
    public static final String PAYMENT_FAILED = "payment.failed";

    public static final String STOCK_RESERVATION_REQUESTED = "stock.reservation.requested";
    public static final String STOCK_RESERVATION_SUCCEEDED = "stock.reservation.succeeded";
    public static final String STOCK_RESERVATION_FAILED = "stock.reservation.failed";

    private KafkaTopics(){}
}