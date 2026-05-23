package com.openjoyer.openmarket.order_service.application.port;

import com.openjoyer.openmarket.contracts.events.inventory.StockReservationRequestEvent;

public interface EventPublisherPort {
    void publishStockReservationRequestEvent(StockReservationRequestEvent event);
}
