package com.openjoyer.openmarket.inventory_service.application.usecase;

import com.openjoyer.openmarket.contracts.events.inventory.StockFailedEvent;
import com.openjoyer.openmarket.contracts.events.inventory.StockReservationRequestEvent;
import com.openjoyer.openmarket.contracts.events.inventory.StockReservedEvent;
import com.openjoyer.openmarket.inventory_service.application.port.EventPublisherPort;
import com.openjoyer.openmarket.inventory_service.domain.exceptions.InsufficientStockException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReserveStockUseCaseTest {
    @Mock
    private ReserveStockTransaction transaction;
    @Mock
    private EventPublisherPort eventPublisher;
    @InjectMocks
    private ReserveStockUseCase useCase;

    @Test
    void handle_shouldPublishStockReservedWhenReservationSucceeds() {
        UUID orderId = UUID.randomUUID();
        StockReservationRequestEvent event = event(orderId);
        doNothing().when(transaction).reserve(event);

        useCase.handle(event);

        verify(eventPublisher).publishStockReserved(new StockReservedEvent(orderId));
        verify(eventPublisher, never()).publishStockFailed(any());
    }

    @Test
    void handle_shouldPublishStockFailedWhenStockInsufficient() {
        UUID orderId = UUID.randomUUID();
        StockReservationRequestEvent event = event(orderId);
        doThrow(new InsufficientStockException("sku-1")).when(transaction).reserve(event);

        useCase.handle(event);

        verify(eventPublisher).publishStockFailed(new StockFailedEvent(orderId));
        verify(eventPublisher, never()).publishStockReserved(any());
    }

    @Test
    void handle_shouldPropagateOptimisticLockSoRetryCanKickIn() {
        UUID orderId = UUID.randomUUID();
        StockReservationRequestEvent event = event(orderId);
        doThrow(new ObjectOptimisticLockingFailureException("inventory_items", "sku-1"))
                .when(transaction).reserve(event);

        assertThrows(ObjectOptimisticLockingFailureException.class, () -> useCase.handle(event));
        verify(eventPublisher, never()).publishStockReserved(any());
        verify(eventPublisher, never()).publishStockFailed(any());
    }

    @Test
    void recover_shouldPublishStockFailedAfterRetriesExhausted() {
        UUID orderId = UUID.randomUUID();

        useCase.recover(new ObjectOptimisticLockingFailureException("inventory_items", "sku-1"), event(orderId));

        verify(eventPublisher).publishStockFailed(new StockFailedEvent(orderId));
    }

    private StockReservationRequestEvent event(UUID orderId) {
        return new StockReservationRequestEvent(orderId, List.of());
    }
}
