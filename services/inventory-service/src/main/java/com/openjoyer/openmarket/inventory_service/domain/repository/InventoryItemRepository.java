package com.openjoyer.openmarket.inventory_service.domain.repository;

import com.openjoyer.openmarket.inventory_service.domain.model.InventoryItem;

import java.util.Optional;

public interface InventoryItemRepository {
    InventoryItem create(InventoryItem inventoryItem);
    Optional<InventoryItem> findById(String id);
}
