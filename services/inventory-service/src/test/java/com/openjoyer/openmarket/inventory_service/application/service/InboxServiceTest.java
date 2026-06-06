package com.openjoyer.openmarket.inventory_service.application.service;

import com.openjoyer.openmarket.inventory_service.domain.model.InventoryProcessedEvent;
import com.openjoyer.openmarket.inventory_service.domain.repository.ProcessedEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InboxServiceTest {
    @Mock
    private ProcessedEventRepository processedEventRepository;
    @InjectMocks
    private InboxService inboxService;

    @Test
    void firstSeen_shouldRecordEventAndReturnTrueForNewEvent() {
        UUID eventId = UUID.randomUUID();
        when(processedEventRepository.existsById(eventId)).thenReturn(false);

        boolean result = inboxService.firstSeen(eventId, "product.replenished");

        assertTrue(result);
        ArgumentCaptor<InventoryProcessedEvent> captor = ArgumentCaptor.forClass(InventoryProcessedEvent.class);
        verify(processedEventRepository).save(captor.capture());
        assertEquals(eventId, captor.getValue().getEventId());
        assertEquals("product.replenished", captor.getValue().getEventType());
    }

    @Test
    void firstSeen_shouldReturnFalseAndNotRecordForDuplicateEvent() {
        UUID eventId = UUID.randomUUID();
        when(processedEventRepository.existsById(eventId)).thenReturn(true);

        boolean result = inboxService.firstSeen(eventId, "product.replenished");

        assertFalse(result);
        verify(processedEventRepository, never()).save(any());
    }
}
