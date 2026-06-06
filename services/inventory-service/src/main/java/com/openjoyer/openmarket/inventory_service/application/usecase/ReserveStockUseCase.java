package com.openjoyer.openmarket.inventory_service.application.usecase;

import com.openjoyer.openmarket.contracts.events.inventory.StockFailedEvent;
import com.openjoyer.openmarket.contracts.events.inventory.StockReservationRequestEvent;
import com.openjoyer.openmarket.contracts.events.inventory.StockReservationRequestItem;
import com.openjoyer.openmarket.contracts.events.inventory.StockReservedEvent;
import com.openjoyer.openmarket.inventory_service.application.port.EventPublisherPort;
import com.openjoyer.openmarket.inventory_service.domain.exceptions.InsufficientStockException;
import com.openjoyer.openmarket.inventory_service.domain.model.InventoryItem;
import com.openjoyer.openmarket.inventory_service.domain.model.Reservation;
import com.openjoyer.openmarket.inventory_service.domain.repository.InventoryItemRepository;
import com.openjoyer.openmarket.inventory_service.domain.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ReserveStockUseCase {
    private final ReserveStockTransaction transaction;
    private final EventPublisherPort eventPublisher;

    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            maxAttempts = 4,
            backoff = @Backoff(delay = 50, multiplier = 2.0)
    )
    @Transactional
    public void handle(StockReservationRequestEvent event) {

        try {
            transaction.reserve(event);
            eventPublisher.publishStockReserved(new StockReservedEvent(event.orderId()));
        } catch (InsufficientStockException e) {
            eventPublisher.publishStockFailed(new StockFailedEvent(event.orderId()));
        }
    }

    @Recover
    void recover(ObjectOptimisticLockingFailureException ex, StockReservationRequestEvent event) {
        eventPublisher.publishStockFailed(new StockFailedEvent(event.orderId()));
    }
}
