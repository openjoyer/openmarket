package com.openjoyer.openmarket.inventory_service.infrastructure.kafka.producer;

import com.openjoyer.openmarket.contracts.events.inventory.StockReleasedEvent;
import com.openjoyer.openmarket.contracts.events.inventory.StockReservedEvent;
import com.openjoyer.openmarket.inventory_service.application.port.EventPublisherPort;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryEventPublisher implements EventPublisherPort {
//    private final KafkaTemplate<String, InventoryItem> kafkaTemplate; // todo ...
    @Override
    public void publishStockReserved(StockReservedEvent event) {

    }

    @Override
    public void publishStockReleased(StockReleasedEvent event) {

    }
    // todo ...
}
