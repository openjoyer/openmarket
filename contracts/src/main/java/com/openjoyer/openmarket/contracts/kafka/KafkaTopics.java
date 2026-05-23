package com.openjoyer.openmarket.contracts.kafka;

public final class KafkaTopics {
    public static final String ORDER_CREATED = "order.created";
    public static final String ORDER_CANCELLED = "order.cancelled";
    public static final String ORDER_COMPLETED = "order.completed";

    public static final String PAYMENT_SUCCEEDED = "payment.succeeded";

    private KafkaTopics(){}
}