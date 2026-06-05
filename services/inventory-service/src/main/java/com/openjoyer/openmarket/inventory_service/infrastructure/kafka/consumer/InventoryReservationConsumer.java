package com.openjoyer.openmarket.inventory_service.infrastructure.kafka.consumer;

import com.openjoyer.openmarket.contracts.events.inventory.StockReleaseRequestedEvent;
import com.openjoyer.openmarket.contracts.events.inventory.StockReservationRequestEvent;
import com.openjoyer.openmarket.contracts.kafka.KafkaTopics;
import com.openjoyer.openmarket.inventory_service.application.usecase.ReleaseStockUseCase;
import com.openjoyer.openmarket.inventory_service.application.usecase.ReserveStockUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryReservationConsumer {
    private final ReserveStockUseCase reserveStockUseCase;
    private final ReleaseStockUseCase releaseStockUseCase;

    @KafkaListener(topics = KafkaTopics.STOCK_RESERVATION_REQUESTED, groupId = "inventory-service")
    public void onStockReservationRequested(StockReservationRequestEvent event) {
        reserveStockUseCase.handle(event);
    }

    @KafkaListener(topics = KafkaTopics.STOCK_RELEASE_REQUESTED, groupId = "inventory-service")
    public void onStockReleaseRequested(StockReleaseRequestedEvent event) {
        releaseStockUseCase.handle(event);
    }
}
