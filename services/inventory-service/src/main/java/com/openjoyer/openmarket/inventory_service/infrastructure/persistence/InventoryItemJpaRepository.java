package com.openjoyer.openmarket.inventory_service.infrastructure.persistence;

import com.openjoyer.openmarket.inventory_service.domain.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryItemJpaRepository extends JpaRepository<InventoryItem, String> {
}
