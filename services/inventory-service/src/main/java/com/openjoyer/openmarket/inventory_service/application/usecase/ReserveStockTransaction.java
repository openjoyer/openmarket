package com.openjoyer.openmarket.inventory_service.application.usecase;

import com.openjoyer.openmarket.contracts.events.inventory.StockFailedEvent;
import com.openjoyer.openmarket.contracts.events.inventory.StockReservationRequestEvent;
import com.openjoyer.openmarket.contracts.events.inventory.StockReservationRequestItem;
import com.openjoyer.openmarket.contracts.events.inventory.StockReservedEvent;
import com.openjoyer.openmarket.inventory_service.domain.exceptions.InsufficientStockException;
import com.openjoyer.openmarket.inventory_service.domain.model.InventoryItem;
import com.openjoyer.openmarket.inventory_service.domain.model.Reservation;
import com.openjoyer.openmarket.inventory_service.domain.repository.InventoryItemRepository;
import com.openjoyer.openmarket.inventory_service.domain.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ReserveStockTransaction {
    private final ReservationRepository reservationRepository;
    private final InventoryItemRepository itemsRepository;

    @Transactional
    public void reserve(StockReservationRequestEvent event) {
        if (reservationRepository.existsByOrderId(event.orderId())) return;

        for (StockReservationRequestItem i : event.items()) {
            InventoryItem item = itemsRepository.findById(i.skuId()).orElseThrow(() -> new InsufficientStockException(i.skuId()));
            item.reserve(i.quantity());
            reservationRepository.save(Reservation.of(event.orderId(), i.skuId(), i.quantity()));
        }
    }
}
