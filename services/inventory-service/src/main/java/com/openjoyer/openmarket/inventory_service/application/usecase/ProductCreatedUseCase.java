package com.openjoyer.openmarket.inventory_service.application.usecase;

import com.openjoyer.openmarket.contracts.events.product.ProductCreatedEvent;
import com.openjoyer.openmarket.inventory_service.domain.model.InventoryItem;
import com.openjoyer.openmarket.inventory_service.domain.repository.InventoryItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ProductCreatedUseCase {
    private final InventoryItemRepository inventoryItemRepository;

    @Transactional
    public void handle(ProductCreatedEvent event) {
        if (inventoryItemRepository.findById(event.skuId()).isPresent()) {
            return;
        }
        InventoryItem inventoryItem = InventoryItem.of(event.skuId(), event.quantity());
        inventoryItemRepository.save(inventoryItem);
    }
}
