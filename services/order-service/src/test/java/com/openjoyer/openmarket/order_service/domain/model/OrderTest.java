package com.openjoyer.openmarket.order_service.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderTest {
    @Test
    void create_shouldCreateOrderWithCreatedStatusAndWireItems() {
        OrderItem item = OrderItem.create("sku-1", "Keyboard", "keyboard.png", BigDecimal.TEN, 1);

        Order order = Order.create("user-1", List.of(item));

        assertEquals("user-1", order.getUserId());
        assertEquals(OrderStatus.PENDING_RESERVATION, order.getOrderStatus());
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

        order.markReserved();
        order.markPaid();

        assertEquals(OrderStatus.PROCESSING, order.getOrderStatus());
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

    @Test
    void markReserved_shouldIgnoreRepeatedReservationEvent() {
        OrderItem item = OrderItem.create("sku-1", "Keyboard", "keyboard.png", BigDecimal.TEN, 1);
        Order order = Order.create("user-1", List.of(item));

        order.markReserved();
        var reservedAt = order.getReservedAt();
        order.markReserved();

        assertEquals(OrderStatus.PAYMENT_PENDING, order.getOrderStatus());
        assertEquals(reservedAt, order.getReservedAt());
    }

    @Test
    void markReserved_shouldIgnoreReservationAfterCancellation() {
        OrderItem item = OrderItem.create("sku-1", "Keyboard", "keyboard.png", BigDecimal.TEN, 1);
        Order order = Order.create("user-1", List.of(item));

        order.cancel();
        var cancelledAt = order.getCancelledAt();
        order.markReserved();

        assertEquals(OrderStatus.CANCELED, order.getOrderStatus());
        assertEquals(cancelledAt, order.getCancelledAt());
        assertNull(order.getReservedAt());
    }

    @Test
    void markReserved_shouldIgnoreReservationAfterPayment() {
        OrderItem item = OrderItem.create("sku-1", "Keyboard", "keyboard.png", BigDecimal.TEN, 1);
        Order order = Order.create("user-1", List.of(item));

        order.markReserved();
        order.markPaid();
        var paidAt = order.getPaidAt();
        order.markReserved();

        assertEquals(OrderStatus.PROCESSING, order.getOrderStatus());
        assertEquals(paidAt, order.getPaidAt());
    }

    @Test
    void markPaid_shouldIgnorePaymentBeforeReservation() {
        OrderItem item = OrderItem.create("sku-1", "Keyboard", "keyboard.png", BigDecimal.TEN, 1);
        Order order = Order.create("user-1", List.of(item));

        order.markPaid();

        assertEquals(OrderStatus.PENDING_RESERVATION, order.getOrderStatus());
        assertNull(order.getPaidAt());
    }

    @Test
    void markPaid_shouldIgnoreRepeatedPaymentEvent() {
        OrderItem item = OrderItem.create("sku-1", "Keyboard", "keyboard.png", BigDecimal.TEN, 1);
        Order order = Order.create("user-1", List.of(item));

        order.markReserved();
        order.markPaid();
        var paidAt = order.getPaidAt();
        order.markPaid();

        assertEquals(OrderStatus.PROCESSING, order.getOrderStatus());
        assertEquals(paidAt, order.getPaidAt());
    }

    @Test
    void markPaid_shouldIgnorePaymentAfterCancellation() {
        OrderItem item = OrderItem.create("sku-1", "Keyboard", "keyboard.png", BigDecimal.TEN, 1);
        Order order = Order.create("user-1", List.of(item));

        order.cancel();
        var cancelledAt = order.getCancelledAt();
        order.markPaid();

        assertEquals(OrderStatus.CANCELED, order.getOrderStatus());
        assertEquals(cancelledAt, order.getCancelledAt());
        assertNull(order.getPaidAt());
    }

    @Test
    void cancel_shouldIgnoreLateCancelAfterPayment() {
        OrderItem item = OrderItem.create("sku-1", "Keyboard", "keyboard.png", BigDecimal.TEN, 1);
        Order order = Order.create("user-1", List.of(item));

        order.markReserved();
        order.markPaid();
        order.cancel();

        assertEquals(OrderStatus.PROCESSING, order.getOrderStatus());
        assertNull(order.getCancelledAt());
    }

    @Test
    void cancel_shouldIgnoreRepeatedCancelEvent() {
        OrderItem item = OrderItem.create("sku-1", "Keyboard", "keyboard.png", BigDecimal.TEN, 1);
        Order order = Order.create("user-1", List.of(item));

        order.cancel();
        var cancelledAt = order.getCancelledAt();
        order.cancel();

        assertEquals(OrderStatus.CANCELED, order.getOrderStatus());
        assertEquals(cancelledAt, order.getCancelledAt());
    }

    @Test
    void cancel_shouldCancelPaymentPendingOrder() {
        OrderItem item = OrderItem.create("sku-1", "Keyboard", "keyboard.png", BigDecimal.TEN, 1);
        Order order = Order.create("user-1", List.of(item));

        order.markReserved();
        order.cancel();

        assertEquals(OrderStatus.CANCELED, order.getOrderStatus());
        assertNotNull(order.getCancelledAt());
    }

    @Test
    void getTotalAmount_shouldMultiplyPriceByQuantityForSingleItem() {
        OrderItem item = OrderItem.create("sku-1", "Keyboard", "keyboard.png", new BigDecimal("12.50"), 3);
        Order order = Order.create("user-1", List.of(item));

        assertEquals(0, order.getTotalAmount().compareTo(new BigDecimal("37.50")));
    }

    @Test
    void getTotalAmount_shouldSumAcrossMultipleItems() {
        OrderItem keyboard = OrderItem.create("sku-1", "Keyboard", "keyboard.png", BigDecimal.TEN, 2);
        OrderItem mouse = OrderItem.create("sku-2", "Mouse", "mouse.png", new BigDecimal("5"), 3);
        Order order = Order.create("user-1", List.of(keyboard, mouse));

        assertEquals(0, order.getTotalAmount().compareTo(new BigDecimal("35")));
    }
}
