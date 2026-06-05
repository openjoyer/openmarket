package com.openjoyer.openmarket.order_service.infrastructure.kafka.consumer;

import com.openjoyer.openmarket.contracts.events.inventory.StockFailedEvent;
import com.openjoyer.openmarket.contracts.events.inventory.StockReservedEvent;
import com.openjoyer.openmarket.contracts.kafka.KafkaTopics;
import com.openjoyer.openmarket.order_service.application.usecase.HandleStockReservationFailedUseCase;
import com.openjoyer.openmarket.order_service.application.usecase.HandleStockReservationSucceededUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryEventsConsumer {
    private final HandleStockReservationSucceededUseCase handleStockReservationSucceeded;
    private final HandleStockReservationFailedUseCase handleStockReservationFailed;

    @KafkaListener(topics = KafkaTopics.STOCK_RESERVATION_SUCCEEDED, groupId = "order-service")
    public void onStockReservationSucceeded(StockReservedEvent event) {
        handleStockReservationSucceeded.handle(event);
    }

    @KafkaListener(topics = KafkaTopics.STOCK_RESERVATION_FAILED, groupId = "order-service")
    public void onStockReservationFailed(StockFailedEvent event) {
        handleStockReservationFailed.handle(event);
    }
}
