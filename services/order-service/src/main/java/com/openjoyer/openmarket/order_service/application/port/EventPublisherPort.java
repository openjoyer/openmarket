package com.openjoyer.openmarket.order_service.application.port;

import com.openjoyer.openmarket.contracts.events.inventory.StockReleaseRequestedEvent;
import com.openjoyer.openmarket.contracts.events.inventory.StockReservationRequestEvent;
import com.openjoyer.openmarket.contracts.events.payment.PaymentRefundRequestedEvent;
import com.openjoyer.openmarket.contracts.events.payment.PaymentRequestedEvent;
import com.openjoyer.openmarket.contracts.events.shipment.ShipmentRequestedEvent;

public interface EventPublisherPort {
    void publishStockReservationRequestEvent(StockReservationRequestEvent event);
    void publishPaymentRequestEvent(PaymentRequestedEvent event);
    void publishShipmentRequestEvent(ShipmentRequestedEvent event);

    void publishStockReleaseRequestEvent(StockReleaseRequestedEvent event);
    void publishPaymentRefundRequestEvent(PaymentRefundRequestedEvent event);
}
