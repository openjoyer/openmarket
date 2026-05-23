package com.openjoyer.openmarket.order_service.infrastructure.kafka.publisher;

import com.openjoyer.openmarket.contracts.events.inventory.StockReservationRequestEvent;
import com.openjoyer.openmarket.contracts.kafka.KafkaTopics;
import com.openjoyer.openmarket.order_service.application.port.EventPublisherPort;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventPublisher implements EventPublisherPort {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publishStockReservationRequestEvent(StockReservationRequestEvent event) {
        kafkaTemplate.send(KafkaTopics.STOCK_RESERVATION_REQUESTED, event.orderId().toString(), event);
    }
}
