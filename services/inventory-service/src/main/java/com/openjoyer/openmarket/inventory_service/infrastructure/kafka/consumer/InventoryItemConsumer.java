package com.openjoyer.openmarket.inventory_service.infrastructure.kafka.consumer;

import com.openjoyer.openmarket.contracts.events.product.ProductCreatedEvent;
import com.openjoyer.openmarket.contracts.events.product.ProductReplenishedEvent;
import com.openjoyer.openmarket.contracts.kafka.KafkaTopics;
import com.openjoyer.openmarket.inventory_service.application.usecase.ProductCreatedUseCase;
import com.openjoyer.openmarket.inventory_service.application.usecase.ProductReplenishedUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryItemConsumer {
    private final ProductCreatedUseCase productCreatedUseCase;
    private final ProductReplenishedUseCase productReplenishedUseCase;

    @KafkaListener(topics = KafkaTopics.PRODUCT_CREATED, groupId = "inventory-service")
    public void onProductCreated(ProductCreatedEvent event) {
        productCreatedUseCase.handle(event);
    }

    @KafkaListener(topics = KafkaTopics.PRODUCT_REPLENISHED, groupId = "inventory-service")
    public void onProductReplenished(ProductReplenishedEvent event) {
        productReplenishedUseCase.handle(event);
    }
}
