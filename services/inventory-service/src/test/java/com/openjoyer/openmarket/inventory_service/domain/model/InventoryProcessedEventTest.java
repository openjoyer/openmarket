package com.openjoyer.openmarket.inventory_service.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InventoryProcessedEventTest {

    @Test
    void of_shouldStoreEventIdTypeAndProcessedAt() {
        UUID eventId = UUID.randomUUID();

        InventoryProcessedEvent event = InventoryProcessedEvent.of(eventId, "product.replenished");

        assertEquals(eventId, event.getEventId());
        assertEquals("product.replenished", event.getEventType());
        assertNotNull(event.getProcessedAt());
    }
}
