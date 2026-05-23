package com.openjoyer.openmarket.order_service.application.usecase;

import com.openjoyer.openmarket.contracts.events.payment.PaymentSucceedEvent;
import com.openjoyer.openmarket.order_service.application.port.CartCommandPort;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HandlePaymentSucceededUseCaseTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartCommandPort cartCommandPort;

    @InjectMocks
    private HandlePaymentSucceededUseCase useCase;

    @Test
    void handle_shouldMarkReservedOrderAsPaidAndClearCart() {
        UUID orderId = UUID.randomUUID();
        Order order = order(orderId);
        order.markReserved();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        useCase.handle(new PaymentSucceedEvent(orderId));

        assertEquals(OrderStatus.PROCESSING, order.getOrderStatus());
        assertNotNull(order.getPaidAt());
        verify(cartCommandPort).clearCart("user-1");
    }

    @Test
    void handle_shouldIgnorePaymentBeforeReservationAndKeepCart() {
        UUID orderId = UUID.randomUUID();
        Order order = order(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        useCase.handle(new PaymentSucceedEvent(orderId));

        assertEquals(OrderStatus.PENDING_RESERVATION, order.getOrderStatus());
        assertNull(order.getPaidAt());
        verify(cartCommandPort, never()).clearCart("user-1");
    }

    @Test
    void handle_shouldIgnoreRepeatedPaymentAndNotClearCartAgain() {
        UUID orderId = UUID.randomUUID();
        Order order = order(orderId);
        order.markReserved();
        order.markPaid();
        var paidAt = order.getPaidAt();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        useCase.handle(new PaymentSucceedEvent(orderId));

        assertEquals(OrderStatus.PROCESSING, order.getOrderStatus());
        assertEquals(paidAt, order.getPaidAt());
        verify(cartCommandPort, never()).clearCart("user-1");
    }

    @Test
    void handle_shouldNotReviveCancelledOrder() {
        UUID orderId = UUID.randomUUID();
        Order order = order(orderId);
        order.cancel();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        useCase.handle(new PaymentSucceedEvent(orderId));

        assertEquals(OrderStatus.CANCELED, order.getOrderStatus());
        assertNull(order.getPaidAt());
        verify(cartCommandPort, never()).clearCart("user-1");
    }

    @Test
    void handle_shouldThrowWhenOrderDoesNotExist() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> useCase.handle(new PaymentSucceedEvent(orderId)));
        verify(orderRepository).findById(orderId);
        verify(cartCommandPort, never()).clearCart("user-1");
    }

    private Order order(UUID orderId) {
        OrderItem item = OrderItem.create("sku-1", "Keyboard", "keyboard.png", BigDecimal.TEN, 1);
        Order order = Order.create("user-1", List.of(item));
        order.setId(orderId);
        return order;
    }
}
