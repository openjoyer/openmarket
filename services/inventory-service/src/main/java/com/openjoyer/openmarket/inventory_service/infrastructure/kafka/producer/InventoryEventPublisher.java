package com.openjoyer.openmarket.inventory_service.infrastructure.kafka.producer;

import com.openjoyer.openmarket.contracts.events.inventory.StockFailedEvent;
import com.openjoyer.openmarket.contracts.events.inventory.StockReleasedEvent;
import com.openjoyer.openmarket.contracts.events.inventory.StockReservedEvent;
import com.openjoyer.openmarket.contracts.kafka.KafkaTopics;
import com.openjoyer.openmarket.inventory_service.application.port.EventPublisherPort;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryEventPublisher implements EventPublisherPort {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publishStockReserved(StockReservedEvent event) {
        kafkaTemplate.send(KafkaTopics.STOCK_RESERVATION_SUCCEEDED, event.orderId().toString(), event);
    }

    @Override
    public void publishStockFailed(StockFailedEvent event) {
        kafkaTemplate.send(KafkaTopics.STOCK_RESERVATION_FAILED, event.orderId().toString(), event);
    }

    @Override
    public void publishStockReleased(StockReleasedEvent event) {
        kafkaTemplate.send(KafkaTopics.STOCK_RELEASE_SUCCEEDED, event.orderId().toString(), event);
    }
}
