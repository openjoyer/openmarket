package com.openjoyer.openmarket.order_service.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderTest {
    @Test
    void create_shouldCreateOrderWithCreatedStatusAndWireItems() {
        OrderItem item = OrderItem.create("sku-1", "Keyboard", "keyboard.png", BigDecimal.TEN, 1);

        Order order = Order.create("user-1", List.of(item));

        assertEquals("user-1", order.getUserId());
        assertEquals(OrderStatus.CREATED, order.getOrderStatus());
        assertEquals(1, order.getItems().size());
        assertSame(order, order.getItems().get(0).getOrder());
    }

    @Test
    void create_shouldRejectBlankUserId() {
        OrderItem item = OrderItem.create("sku-1", "Keyboard", "keyboard.png", BigDecimal.TEN, 1);

        assertThrows(IllegalArgumentException.class, () -> Order.create(" ", List.of(item)));
    }

    @Test
    void create_shouldRejectEmptyItems() {
        assertThrows(IllegalArgumentException.class, () -> Order.create("user-1", List.of()));
    }

    @Test
    void addItem_shouldRejectNullItem() {
        OrderItem item = OrderItem.create("sku-1", "Keyboard", "keyboard.png", BigDecimal.TEN, 1);
        Order order = Order.create("user-1", List.of(item));

        assertThrows(IllegalArgumentException.class, () -> order.addItem(null));
    }

    @Test
    void markPaid_shouldSetPaidStatusAndTimestamp() {
        OrderItem item = OrderItem.create("sku-1", "Keyboard", "keyboard.png", BigDecimal.TEN, 1);
        Order order = Order.create("user-1", List.of(item));

        order.markPaid();

        assertEquals(OrderStatus.PAID, order.getOrderStatus());
        assertNotNull(order.getPaidAt());
    }

    @Test
    void cancel_shouldSetCancelledStatusAndTimestamp() {
        OrderItem item = OrderItem.create("sku-1", "Keyboard", "keyboard.png", BigDecimal.TEN, 1);
        Order order = Order.create("user-1", List.of(item));

        order.cancel();

        assertEquals(OrderStatus.CANCELED, order.getOrderStatus());
        assertNotNull(order.getCancelledAt());
    }
}