package com.openjoyer.openmarket.inventory_service.application.usecase;

import com.openjoyer.openmarket.contracts.events.product.ProductCreatedEvent;
import com.openjoyer.openmarket.inventory_service.domain.model.InventoryItem;
import com.openjoyer.openmarket.inventory_service.domain.repository.InventoryItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductCreatedUseCaseTest {
    @Mock
    private InventoryItemRepository inventoryItemRepository;
    @InjectMocks
    private ProductCreatedUseCase useCase;

    @Test
    void handle_shouldCreateInventoryItemWhenSkuIsNew() {
        when(inventoryItemRepository.findById("sku-1")).thenReturn(Optional.empty());

        useCase.handle(new ProductCreatedEvent("sku-1", 25));

        ArgumentCaptor<InventoryItem> captor = ArgumentCaptor.forClass(InventoryItem.class);
        verify(inventoryItemRepository).save(captor.capture());
        assertEquals("sku-1", captor.getValue().getSkuId());
        assertEquals(25, captor.getValue().getAvailable());
        assertEquals(0, captor.getValue().getReserved());
    }

    @Test
    void handle_shouldBeIdempotentAndNotOverwriteExistingStock() {
        InventoryItem existing = InventoryItem.of("sku-1", 5);
        existing.reserve(2);
        when(inventoryItemRepository.findById("sku-1")).thenReturn(Optional.of(existing));

        useCase.handle(new ProductCreatedEvent("sku-1", 99));

        verify(inventoryItemRepository, never()).save(any());
        assertEquals(3, existing.getAvailable());
        assertEquals(2, existing.getReserved());
    }
}
