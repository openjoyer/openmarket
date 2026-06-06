package com.openjoyer.openmarket.inventory_service.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "inventory_processed_events")
@Getter
@NoArgsConstructor
public class InventoryProcessedEvent {
    @Id
    private UUID eventId;
    private String eventType;
    private Instant processedAt;

    public static InventoryProcessedEvent of(UUID eventId, String eventType) {
        InventoryProcessedEvent e = new InventoryProcessedEvent();
        e.eventId = eventId;
        e.eventType = eventType;
        e.processedAt = Instant.now();
        return e;
    }
}
