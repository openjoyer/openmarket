package com.openjoyer.openmarket.inventory_service.application.port;

import com.openjoyer.openmarket.contracts.events.inventory.StockFailedEvent;
import com.openjoyer.openmarket.contracts.events.inventory.StockReleasedEvent;
import com.openjoyer.openmarket.contracts.events.inventory.StockReservedEvent;

public interface EventPublisherPort {
    void publishStockReserved(StockReservedEvent event);
    void publishStockFailed(StockFailedEvent event);
    void publishStockReleased(StockReleasedEvent event);
}
