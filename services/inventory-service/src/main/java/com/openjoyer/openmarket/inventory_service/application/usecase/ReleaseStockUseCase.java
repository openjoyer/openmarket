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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReleaseStockUseCase {
    private final ReservationRepository reservationRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final EventPublisherPort eventPublisherPort;

    @Transactional
    public void handle(StockReleaseRequestedEvent event){
        List<Reservation> reservations = reservationRepository.findAllByOrderId(event.orderId());
        for(Reservation reservation : reservations) {
            if (reservation.getStatus() == ReservationStatus.RELEASED) continue;

            InventoryItem item = inventoryItemRepository.findById(reservation.getSkuId())
                    .orElseThrow(() -> new InventoryItemNotFoundException(reservation.getSkuId()));
            item.release(reservation.getQuantity());
            reservation.updateStatus(ReservationStatus.RELEASED);
        }
        eventPublisherPort.publishStockReleased(new StockReleasedEvent(event.orderId()));
    }
}
