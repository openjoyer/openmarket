package com.openjoyer.openmarket.inventory_service.domain.model;

import com.openjoyer.openmarket.inventory_service.domain.exceptions.InsufficientStockException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InventoryItemTest {

    @Test
    void of_shouldStartWithAllStockAvailableAndNothingReserved() {
        InventoryItem item = InventoryItem.of("sku-1", 10);

        assertEquals("sku-1", item.getSkuId());
        assertEquals(10, item.getAvailable());
        assertEquals(0, item.getReserved());
    }

    @Test
    void reserve_shouldMoveQuantityFromAvailableToReserved() {
        InventoryItem item = InventoryItem.of("sku-1", 10);

        item.reserve(3);

        assertEquals(7, item.getAvailable());
        assertEquals(3, item.getReserved());
    }

    @Test
    void reserve_shouldAllowReservingExactlyAvailable() {
        InventoryItem item = InventoryItem.of("sku-1", 5);

        item.reserve(5);

        assertEquals(0, item.getAvailable());
        assertEquals(5, item.getReserved());
    }

    @Test
    void reserve_shouldThrowWhenNotEnoughAvailable() {
        InventoryItem item = InventoryItem.of("sku-1", 2);

        assertThrows(InsufficientStockException.class, () -> item.reserve(3));
        assertEquals(2, item.getAvailable());
        assertEquals(0, item.getReserved());
    }

    @Test
    void reserve_shouldRejectNonPositiveQuantity() {
        InventoryItem item = InventoryItem.of("sku-1", 5);

        assertThrows(IllegalArgumentException.class, () -> item.reserve(0));
        assertThrows(IllegalArgumentException.class, () -> item.reserve(-1));
    }

    @Test
    void release_shouldMoveQuantityFromReservedBackToAvailable() {
        InventoryItem item = InventoryItem.of("sku-1", 10);
        item.reserve(4);

        item.release(4);

        assertEquals(10, item.getAvailable());
        assertEquals(0, item.getReserved());
    }

    @Test
    void release_shouldThrowWhenReleasingMoreThanReserved() {
        InventoryItem item = InventoryItem.of("sku-1", 10);
        item.reserve(2);

        assertThrows(IllegalArgumentException.class, () -> item.release(3));
        assertEquals(8, item.getAvailable());
        assertEquals(2, item.getReserved());
    }

    @Test
    void release_shouldRejectNonPositiveQuantity() {
        InventoryItem item = InventoryItem.of("sku-1", 10);
        item.reserve(2);

        assertThrows(IllegalArgumentException.class, () -> item.release(0));
        assertThrows(IllegalArgumentException.class, () -> item.release(-5));
    }

    @Test
    void confirm_shouldReduceReservedWithoutTouchingAvailable() {
        InventoryItem item = InventoryItem.of("sku-1", 10);
        item.reserve(4);

        item.confirm(4);

        assertEquals(6, item.getAvailable());
        assertEquals(0, item.getReserved());
    }

    @Test
    void confirm_shouldThrowWhenConfirmingMoreThanReserved() {
        InventoryItem item = InventoryItem.of("sku-1", 10);
        item.reserve(2);

        assertThrows(IllegalArgumentException.class, () -> item.confirm(3));
        assertEquals(2, item.getReserved());
    }

    @Test
    void replenish_shouldIncreaseAvailable() {
        InventoryItem item = InventoryItem.of("sku-1", 5);

        item.replenish(7);

        assertEquals(12, item.getAvailable());
        assertEquals(0, item.getReserved());
    }

    @Test
    void replenish_shouldIgnoreNonPositiveQuantity() {
        InventoryItem item = InventoryItem.of("sku-1", 5);

        item.replenish(0);
        item.replenish(-3);

        assertEquals(5, item.getAvailable());
    }

    @Test
    void reserveThenReleaseAndReplenish_shouldKeepBalancesConsistent() {
        InventoryItem item = InventoryItem.of("sku-1", 10);

        item.reserve(6);
        item.release(2);
        item.confirm(4);
        item.replenish(4);

        assertEquals(10, item.getAvailable());
        assertEquals(0, item.getReserved());
    }
}
