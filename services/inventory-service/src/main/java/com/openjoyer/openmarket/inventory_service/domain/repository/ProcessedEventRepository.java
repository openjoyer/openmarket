package com.openjoyer.openmarket.inventory_service.domain.repository;

import com.openjoyer.openmarket.inventory_service.domain.model.InventoryProcessedEvent;

import java.time.Instant;
import java.util.UUID;

public interface ProcessedEventRepository {
    boolean existsById(UUID eventId);

    InventoryProcessedEvent save(InventoryProcessedEvent event);

    int deleteOlderThan(Instant timestamp);
}
