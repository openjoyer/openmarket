package com.openjoyer.openmarket.order_service.application.usecase;

import com.openjoyer.openmarket.contracts.events.payment.PaymentFailedEvent;
import com.openjoyer.openmarket.order_service.domain.exceptions.OrderNotFoundException;
import com.openjoyer.openmarket.order_service.domain.model.Order;
import com.openjoyer.openmarket.order_service.domain.model.OrderItem;
import com.openjoyer.openmarket.order_service.domain.model.OrderStatus;
import com.openjoyer.openmarket.order_service.domain.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HandlePaymentFailedUseCaseTest {
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private HandlePaymentFailedUseCase useCase;

    @Test
    void handle_shouldCancelPaymentPendingOrder() {
        UUID orderId = UUID.randomUUID();
        Order order = order(orderId);
        order.markReserved();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        useCase.handle(new PaymentFailedEvent(orderId));

        assertEquals(OrderStatus.CANCELED, order.getOrderStatus());
        assertNotNull(order.getCancelledAt());
    }

    @Test
    void handle_shouldCancelPendingReservationOrder() {
        UUID orderId = UUID.randomUUID();
        Order order = order(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        useCase.handle(new PaymentFailedEvent(orderId));

        assertEquals(OrderStatus.CANCELED, order.getOrderStatus());
        assertNotNull(order.getCancelledAt());
    }

    @Test
    void handle_shouldIgnoreRepeatedFailureEvent() {
        UUID orderId = UUID.randomUUID();
        Order order = order(orderId);
        order.cancel();
        var cancelledAt = order.getCancelledAt();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        useCase.handle(new PaymentFailedEvent(orderId));

        assertEquals(OrderStatus.CANCELED, order.getOrderStatus());
        assertEquals(cancelledAt, order.getCancelledAt());
    }

    @Test
    void handle_shouldNotCancelPaidOrderOnLatePaymentFailure() {
        UUID orderId = UUID.randomUUID();
        Order order = order(orderId);
        order.markReserved();
        order.markPaid();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        useCase.handle(new PaymentFailedEvent(orderId));

        assertEquals(OrderStatus.PROCESSING, order.getOrderStatus());
        assertNull(order.getCancelledAt());
    }

    @Test
    void handle_shouldThrowWhenOrderDoesNotExist() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> useCase.handle(new PaymentFailedEvent(orderId)));
        verify(orderRepository).findById(orderId);
    }

    private Order order(UUID orderId) {
        OrderItem item = OrderItem.create("sku-1", "Keyboard", "keyboard.png", BigDecimal.TEN, 1);
        Order order = Order.create("user-1", List.of(item));
        order.setId(orderId);
        return order;
    }
}
