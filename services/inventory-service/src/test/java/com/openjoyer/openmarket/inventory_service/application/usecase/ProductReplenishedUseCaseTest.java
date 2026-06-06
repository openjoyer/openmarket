package com.openjoyer.openmarket.inventory_service.application.usecase;

import com.openjoyer.openmarket.contracts.events.product.ProductReplenishedEvent;
import com.openjoyer.openmarket.contracts.kafka.KafkaTopics;
import com.openjoyer.openmarket.inventory_service.application.service.InboxService;
import com.openjoyer.openmarket.inventory_service.domain.exceptions.InventoryItemNotFoundException;
import com.openjoyer.openmarket.inventory_service.domain.model.InventoryItem;
import com.openjoyer.openmarket.inventory_service.domain.repository.InventoryItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductReplenishedUseCaseTest {
    @Mock
    private InventoryItemRepository inventoryItemRepository;
    @Mock
    private InboxService inboxService;
    @InjectMocks
    private ProductReplenishedUseCase useCase;

    @Test
    void handle_shouldTopUpStockWhenEventSeenFirstTime() {
        UUID eventId = UUID.randomUUID();
        InventoryItem item = InventoryItem.of("sku-1", 5);
        when(inboxService.firstSeen(eventId, KafkaTopics.PRODUCT_REPLENISHED)).thenReturn(true);
        when(inventoryItemRepository.findById("sku-1")).thenReturn(Optional.of(item));

        useCase.handle(new ProductReplenishedEvent(eventId, "sku-1", 7));

        assertEquals(12, item.getAvailable());
    }

    @Test
    void handle_shouldSkipDuplicateEventAndNotTouchStock() {
        UUID eventId = UUID.randomUUID();
        when(inboxService.firstSeen(eventId, KafkaTopics.PRODUCT_REPLENISHED)).thenReturn(false);

        useCase.handle(new ProductReplenishedEvent(eventId, "sku-1", 7));

        verify(inventoryItemRepository, never()).findById(anyString());
    }

    @Test
    void handle_shouldThrowWhenItemDoesNotExist() {
        UUID eventId = UUID.randomUUID();
        when(inboxService.firstSeen(eventId, KafkaTopics.PRODUCT_REPLENISHED)).thenReturn(true);
        when(inventoryItemRepository.findById("sku-x")).thenReturn(Optional.empty());

        assertThrows(InventoryItemNotFoundException.class,
                () -> useCase.handle(new ProductReplenishedEvent(eventId, "sku-x", 7)));
    }
}
