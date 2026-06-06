package com.openjoyer.openmarket.inventory_service.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReservationTest {

    @Test
    void of_shouldCreateReservedReservationWithTimestamps() {
        UUID orderId = UUID.randomUUID();

        Reservation reservation = Reservation.of(orderId, "sku-1", 3);

        assertEquals(orderId, reservation.getOrderId());
        assertEquals("sku-1", reservation.getSkuId());
        assertEquals(3, reservation.getQuantity());
        assertEquals(ReservationStatus.RESERVED, reservation.getStatus());
        assertNotNull(reservation.getCreatedAt());
        assertNotNull(reservation.getUpdatedAt());
    }

    @Test
    void updateStatus_shouldChangeStatusAndBumpUpdatedAt() {
        Reservation reservation = Reservation.of(UUID.randomUUID(), "sku-1", 3);
        var createdAt = reservation.getCreatedAt();

        reservation.updateStatus(ReservationStatus.RELEASED);

        assertEquals(ReservationStatus.RELEASED, reservation.getStatus());
        assertEquals(createdAt, reservation.getCreatedAt());
        assertTrue(!reservation.getUpdatedAt().isBefore(createdAt));
    }

    @Test
    void updateQuantity_shouldChangeQuantity() {
        Reservation reservation = Reservation.of(UUID.randomUUID(), "sku-1", 3);

        reservation.updateQuantity(8);

        assertEquals(8, reservation.getQuantity());
    }
}
