package com.openjoyer.openmarket.inventory_service.application.usecase;

import com.openjoyer.openmarket.contracts.events.product.ProductReplenishedEvent;
import com.openjoyer.openmarket.contracts.kafka.KafkaTopics;
import com.openjoyer.openmarket.inventory_service.application.service.InboxService;
import com.openjoyer.openmarket.inventory_service.domain.exceptions.InventoryItemNotFoundException;
import com.openjoyer.openmarket.inventory_service.domain.model.InventoryItem;
import com.openjoyer.openmarket.inventory_service.domain.repository.InventoryItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ProductReplenishedUseCase {
    private final InventoryItemRepository inventoryItemRepository;
    private final InboxService inboxService;

    @Transactional
    public void handle(ProductReplenishedEvent event) {
        if (!inboxService.firstSeen(event.eventId(), KafkaTopics.PRODUCT_REPLENISHED)) {
            return;
        }
        InventoryItem item = inventoryItemRepository.findById(event.skuId())
                .orElseThrow(() -> new InventoryItemNotFoundException(event.skuId()));

        item.replenish(event.quantity());
    }
}
