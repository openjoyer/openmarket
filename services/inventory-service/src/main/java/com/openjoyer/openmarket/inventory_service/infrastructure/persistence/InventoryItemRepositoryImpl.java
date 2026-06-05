package com.openjoyer.openmarket.inventory_service.infrastructure.persistence;

import com.openjoyer.openmarket.inventory_service.domain.repository.InventoryItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class InventoryItemRepositoryImpl implements InventoryItemRepository {
    private final InventoryItemJpaRepository repo;
}
