package com.openjoyer.openmarket.order_service.application.port;

import com.openjoyer.openmarket.contracts.events.inventory.StockReservationRequestEvent;
import com.openjoyer.openmarket.contracts.events.payment.PaymentRequestedEvent;

public interface EventPublisherPort {
    void publishStockReservationRequestEvent(StockReservationRequestEvent event);
    void publishPaymentRequestEvent(PaymentRequestedEvent event);
}
