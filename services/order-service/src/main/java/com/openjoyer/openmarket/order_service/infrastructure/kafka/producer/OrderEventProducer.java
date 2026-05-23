package com.openjoyer.openmarket.order_service.infrastructure.kafka.producer;

import com.openjoyer.openmarket.contracts.events.order.OrderCreatedEvent;
import com.openjoyer.openmarket.contracts.kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void produceOrderCreated(OrderCreatedEvent event) {
        kafkaTemplate.send(KafkaTopics.ORDER_CREATED, event.orderId().toString(), event);
    }
}
