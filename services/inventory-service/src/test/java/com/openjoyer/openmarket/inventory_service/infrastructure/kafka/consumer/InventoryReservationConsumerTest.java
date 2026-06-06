package com.openjoyer.openmarket.inventory_service.infrastructure.kafka.consumer;

import com.openjoyer.openmarket.contracts.events.inventory.StockReleaseRequestedEvent;
import com.openjoyer.openmarket.contracts.events.inventory.StockReservationRequestEvent;
import com.openjoyer.openmarket.inventory_service.application.usecase.ReleaseStockUseCase;
import com.openjoyer.openmarket.inventory_service.application.usecase.ReserveStockUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InventoryReservationConsumerTest {
    @Mock
    private ReserveStockUseCase reserveStockUseCase;
    @Mock
    private ReleaseStockUseCase releaseStockUseCase;
    @InjectMocks
    private InventoryReservationConsumer consumer;

    @Test
    void onStockReservationRequested_shouldDelegateToReserveUseCase() {
        StockReservationRequestEvent event = new StockReservationRequestEvent(UUID.randomUUID(), List.of());

        consumer.onStockReservationRequested(event);

        verify(reserveStockUseCase).handle(event);
    }

    @Test
    void onStockReleaseRequested_shouldDelegateToReleaseUseCase() {
        StockReleaseRequestedEvent event = new StockReleaseRequestedEvent(UUID.randomUUID());

        consumer.onStockReleaseRequested(event);

        verify(releaseStockUseCase).handle(event);
    }
}
