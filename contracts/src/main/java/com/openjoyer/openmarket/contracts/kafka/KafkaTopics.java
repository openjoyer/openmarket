package com.openjoyer.openmarket.contracts.kafka;

public final class KafkaTopics {
    public static final String ORDER_CREATED = "order.created";
    public static final String ORDER_CANCELLED = "order.cancelled";
    public static final String ORDER_COMPLETED = "order.completed";

    public static final String PAYMENT_REQUESTED = "payment.requested";
    public static final String PAYMENT_SUCCEEDED = "payment.succeeded";
    public static final String PAYMENT_FAILED = "payment.failed";
    public static final String PAYMENT_REFUND_REQUESTED = "payment.refund.requested";

    public static final String STOCK_RESERVATION_REQUESTED = "stock.reservation.requested";
    public static final String STOCK_RESERVATION_SUCCEEDED = "stock.reservation.succeeded";
    public static final String STOCK_RESERVATION_FAILED = "stock.reservation.failed";
    public static final String STOCK_RELEASE_REQUESTED = "stock.release.requested";
    public static final String STOCK_RELEASE_SUCCEEDED = "stock.release.succeeded";

    public static final String SHIPMENT_REQUESTED = "shipment.requested";
    public static final String SHIPMENT_SUCCEEDED = "shipment.succeeded";
    public static final String SHIPMENT_FAILED = "shipment.failed";

    public static final String PRODUCT_CREATED = "product.created";
    public static final String PRODUCT_UPDATED = "product.updated";
    public static final String PRODUCT_DELETED = "product.deleted";
    public static final String PRODUCT_REPLENISHED = "product.replenished";

    private KafkaTopics(){}
}