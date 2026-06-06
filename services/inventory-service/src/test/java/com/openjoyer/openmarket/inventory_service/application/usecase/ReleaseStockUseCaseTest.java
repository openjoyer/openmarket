package com.openjoyer.openmarket.inventory_service.application.usecase;

import com.openjoyer.openmarket.contracts.events.inventory.StockReleaseRequestedEvent;
import com.openjoyer.openmarket.contracts.events.inventory.StockReleasedEvent;
import com.openjoyer.openmarket.inventory_service.application.port.EventPublisherPort;
import com.openjoyer.openmarket.inventory_service.domain.exceptions.InventoryItemNotFoundException;
import com.openjoyer.openmarket.inventory_service.domain.model.InventoryItem;
import com.openjoyer.openmarket.inventory_service.domain.model.Reservation;
import com.openjoyer.openmarket.inventory_service.domain.model.ReservationStatus;
import com.openjoyer.openmarket.inventory_service.domain.repository.InventoryItemRepository;
import com.openjoyer.openmarket.inventory_service.domain.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReleaseStockUseCaseTest {
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private InventoryItemRepository inventoryItemRepository;
    @Mock
    private EventPublisherPort eventPublisherPort;
    @InjectMocks
    private ReleaseStockUseCase useCase;

    @Test
    void handle_shouldReturnReservedStockToAvailableAndCloseReservations() {
        UUID orderId = UUID.randomUUID();
        InventoryItem item = InventoryItem.of("sku-1", 10);
        item.reserve(4);
        Reservation reservation = Reservation.of(orderId, "sku-1", 4);
        when(reservationRepository.findAllByOrderId(orderId)).thenReturn(List.of(reservation));
        when(inventoryItemRepository.findById("sku-1")).thenReturn(Optional.of(item));

        useCase.handle(new StockReleaseRequestedEvent(orderId));

        assertEquals(10, item.getAvailable());
        assertEquals(0, item.getReserved());
        assertEquals(ReservationStatus.RELEASED, reservation.getStatus());
        verify(eventPublisherPort).publishStockReleased(new StockReleasedEvent(orderId));
    }

    @Test
    void handle_shouldSkipAlreadyReleasedReservations() {
        UUID orderId = UUID.randomUUID();
        Reservation reservation = Reservation.of(orderId, "sku-1", 4);
        reservation.updateStatus(ReservationStatus.RELEASED);
        when(reservationRepository.findAllByOrderId(orderId)).thenReturn(List.of(reservation));

        useCase.handle(new StockReleaseRequestedEvent(orderId));

        verify(inventoryItemRepository, never()).findById(anyString());
        verify(eventPublisherPort).publishStockReleased(new StockReleasedEvent(orderId));
    }

    @Test
    void handle_shouldPublishReleasedEvenWhenNoReservationsExist() {
        UUID orderId = UUID.randomUUID();
        when(reservationRepository.findAllByOrderId(orderId)).thenReturn(List.of());

        useCase.handle(new StockReleaseRequestedEvent(orderId));

        verify(inventoryItemRepository, never()).findById(anyString());
        verify(eventPublisherPort).publishStockReleased(new StockReleasedEvent(orderId));
    }

    @Test
    void handle_shouldThrowWhenReservedItemMissing() {
        UUID orderId = UUID.randomUUID();
        Reservation reservation = Reservation.of(orderId, "sku-1", 4);
        when(reservationRepository.findAllByOrderId(orderId)).thenReturn(List.of(reservation));
        when(inventoryItemRepository.findById("sku-1")).thenReturn(Optional.empty());

        assertThrows(InventoryItemNotFoundException.class,
                () -> useCase.handle(new StockReleaseRequestedEvent(orderId)));
        verify(eventPublisherPort, never()).publishStockReleased(org.mockito.ArgumentMatchers.any());
    }
}
