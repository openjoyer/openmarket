package com.openjoyer.openmarket.inventory_service.infrastructure.persistence;

import com.openjoyer.openmarket.inventory_service.domain.model.InventoryProcessedEvent;
import com.openjoyer.openmarket.inventory_service.domain.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InventoryProcessedEventRepositoryImpl implements ProcessedEventRepository {
    private final InventoryProcessedEventJpaRepository repo;

    @Override
    public boolean existsById(UUID eventId) {
        return repo.existsById(eventId);
    }

    @Override
    public InventoryProcessedEvent save(InventoryProcessedEvent event) {
        return repo.save(event);
    }

    @Override
    public int deleteOlderThan(Instant timestamp) {
        return repo.deleteOlderThan(timestamp);
    }
}
