package com.openjoyer.openmarket.inventory_service.infrastructure.persistence;

import com.openjoyer.openmarket.inventory_service.domain.model.InventoryItem;
import com.openjoyer.openmarket.inventory_service.domain.repository.InventoryItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InventoryItemRepositoryImpl implements InventoryItemRepository {
    private final InventoryItemJpaRepository repo;

    @Override
    public InventoryItem save(InventoryItem inventoryItem) {
        return repo.save(inventoryItem);
    }

    @Override
    public Optional<InventoryItem> findById(String id) {
        return repo.findById(id);
    }
}
