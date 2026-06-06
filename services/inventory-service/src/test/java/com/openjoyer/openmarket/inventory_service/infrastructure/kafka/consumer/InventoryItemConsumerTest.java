package com.openjoyer.openmarket.inventory_service.infrastructure.kafka.consumer;

import com.openjoyer.openmarket.contracts.events.product.ProductCreatedEvent;
import com.openjoyer.openmarket.contracts.events.product.ProductReplenishedEvent;
import com.openjoyer.openmarket.inventory_service.application.usecase.ProductCreatedUseCase;
import com.openjoyer.openmarket.inventory_service.application.usecase.ProductReplenishedUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InventoryItemConsumerTest {
    @Mock
    private ProductCreatedUseCase productCreatedUseCase;
    @Mock
    private ProductReplenishedUseCase productReplenishedUseCase;
    @InjectMocks
    private InventoryItemConsumer consumer;

    @Test
    void onProductCreated_shouldDelegateToProductCreatedUseCase() {
        ProductCreatedEvent event = new ProductCreatedEvent("sku-1", 10);

        consumer.onProductCreated(event);

        verify(productCreatedUseCase).handle(event);
    }

    @Test
    void onProductReplenished_shouldDelegateToProductReplenishedUseCase() {
        ProductReplenishedEvent event = new ProductReplenishedEvent(UUID.randomUUID(), "sku-1", 10);

        consumer.onProductReplenished(event);

        verify(productReplenishedUseCase).handle(event);
    }
}
