package com.openjoyer.openmarket.order_service.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderItemTest {
    @Test
    void create_shouldCreateOrderItem() {
        OrderItem item = OrderItem.create("sku-1", "Keyboard", "keyboard.png", BigDecimal.valueOf(199.99), 2);

        assertEquals("sku-1", item.getSkuId());
        assertEquals("Keyboard", item.getTitleSnapshot());
        assertEquals("keyboard.png", item.getImageSnapshot());
        assertEquals(BigDecimal.valueOf(199.99), item.getPrice());
        assertEquals(2, item.getQuantity());
        assertNull(item.getOrder());
    }

    @Test
    void create_shouldRejectBlankSkuId() {
        assertThrows(IllegalArgumentException.class,
                () -> OrderItem.create(" ", "Keyboard", "keyboard.png", BigDecimal.TEN, 1));
    }

    @Test
    void create_shouldRejectNegativePrice() {
        assertThrows(IllegalArgumentException.class,
                () -> OrderItem.create("sku-1", "Keyboard", "keyboard.png", BigDecimal.valueOf(-1), 1));
    }

    @Test
    void create_shouldRejectNonPositiveQuantity() {
        assertThrows(IllegalArgumentException.class,
                () -> OrderItem.create("sku-1", "Keyboard", "keyboard.png", BigDecimal.TEN, 0));
    }
}