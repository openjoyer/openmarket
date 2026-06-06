package com.openjoyer.openmarket.inventory_service.application.service;

import com.openjoyer.openmarket.inventory_service.domain.model.InventoryProcessedEvent;
import com.openjoyer.openmarket.inventory_service.domain.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InboxService {
    private final ProcessedEventRepository processEventRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public boolean firstSeen(UUID eventId, String eventType) {
        if(processEventRepository.existsById(eventId)) return false;
        processEventRepository.save(InventoryProcessedEvent.of(eventId, eventType));
        return true;
    }
}
