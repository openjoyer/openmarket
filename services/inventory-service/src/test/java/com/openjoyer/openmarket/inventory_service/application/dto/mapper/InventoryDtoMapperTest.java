package com.openjoyer.openmarket.inventory_service.application.dto.mapper;

import com.openjoyer.openmarket.inventory_service.application.dto.ReservationView;
import com.openjoyer.openmarket.inventory_service.domain.model.Reservation;
import com.openjoyer.openmarket.inventory_service.domain.model.ReservationStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InventoryDtoMapperTest {

    @Test
    void mapToReservationView_shouldCopyAllFields() {
        UUID orderId = UUID.randomUUID();
        Reservation reservation = Reservation.of(orderId, "sku-1", 4);

        ReservationView view = InventoryDtoMapper.mapToReservationView(reservation);

        assertEquals(orderId, view.getOrderId());
        assertEquals("sku-1", view.getSkuId());
        assertEquals(4, view.getQuantity());
        assertEquals(ReservationStatus.RESERVED, view.getStatus());
    }
}
