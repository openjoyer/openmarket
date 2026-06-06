package com.openjoyer.openmarket.inventory_service.application.usecase;

import com.openjoyer.openmarket.contracts.events.inventory.StockReservationRequestEvent;
import com.openjoyer.openmarket.contracts.events.inventory.StockReservationRequestItem;
import com.openjoyer.openmarket.inventory_service.domain.exceptions.InsufficientStockException;
import com.openjoyer.openmarket.inventory_service.domain.model.InventoryItem;
import com.openjoyer.openmarket.inventory_service.domain.model.Reservation;
import com.openjoyer.openmarket.inventory_service.domain.repository.InventoryItemRepository;
import com.openjoyer.openmarket.inventory_service.domain.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReserveStockTransactionTest {
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private InventoryItemRepository itemsRepository;
    @InjectMocks
    private ReserveStockTransaction transaction;

    @Test
    void reserve_shouldReserveEveryItemAndSaveReservations() {
        UUID orderId = UUID.randomUUID();
        InventoryItem keyboard = InventoryItem.of("sku-1", 10);
        InventoryItem mouse = InventoryItem.of("sku-2", 10);
        when(reservationRepository.existsByOrderId(orderId)).thenReturn(false);
        when(itemsRepository.findById("sku-1")).thenReturn(Optional.of(keyboard));
        when(itemsRepository.findById("sku-2")).thenReturn(Optional.of(mouse));

        transaction.reserve(event(orderId, item("sku-1", 2), item("sku-2", 3)));

        assertEquals(8, keyboard.getAvailable());
        assertEquals(2, keyboard.getReserved());
        assertEquals(7, mouse.getAvailable());
        assertEquals(3, mouse.getReserved());

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepository, times(2)).save(captor.capture());
        assertEquals(orderId, captor.getAllValues().get(0).getOrderId());
    }

    @Test
    void reserve_shouldThrowAndStopWhenStockIsInsufficient() {
        UUID orderId = UUID.randomUUID();
        InventoryItem item = InventoryItem.of("sku-1", 1);
        when(reservationRepository.existsByOrderId(orderId)).thenReturn(false);
        when(itemsRepository.findById("sku-1")).thenReturn(Optional.of(item));

        assertThrows(InsufficientStockException.class,
                () -> transaction.reserve(event(orderId, item("sku-1", 5))));

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void reserve_shouldThrowInsufficientStockWhenItemUnknown() {
        UUID orderId = UUID.randomUUID();
        when(reservationRepository.existsByOrderId(orderId)).thenReturn(false);
        when(itemsRepository.findById("sku-x")).thenReturn(Optional.empty());

        assertThrows(InsufficientStockException.class,
                () -> transaction.reserve(event(orderId, item("sku-x", 1))));

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void reserve_shouldBeIdempotentWhenOrderAlreadyReserved() {
        UUID orderId = UUID.randomUUID();
        when(reservationRepository.existsByOrderId(orderId)).thenReturn(true);

        transaction.reserve(event(orderId, item("sku-1", 1)));

        verifyNoInteractions(itemsRepository);
        verify(reservationRepository, never()).save(any());
    }

    private StockReservationRequestEvent event(UUID orderId, StockReservationRequestItem... items) {
        return new StockReservationRequestEvent(orderId, List.of(items));
    }

    private StockReservationRequestItem item(String skuId, int quantity) {
        return new StockReservationRequestItem(skuId, "title", "image.png", BigDecimal.TEN, quantity);
    }
}
