package com.openjoyer.openmarket.order_service.application.usecase;

import com.openjoyer.openmarket.contracts.events.payment.PaymentSucceedEvent;
import com.openjoyer.openmarket.order_service.application.port.CartCommandPort;
import com.openjoyer.openmarket.order_service.domain.exceptions.OrderNotFoundException;
import com.openjoyer.openmarket.order_service.domain.model.Order;
import com.openjoyer.openmarket.order_service.domain.model.OrderItem;
import com.openjoyer.openmarket.order_service.domain.model.OrderStatus;
import com.openjoyer.openmarket.order_service.domain.repository.OrderRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HandlePaymentSucceededUseCaseTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartCommandPort cartCommandPort;

    private MeterRegistry meterRegistry;
    private HandlePaymentSucceededUseCase useCase;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        useCase = new HandlePaymentSucceededUseCase(orderRepository, cartCommandPort, meterRegistry);
    }

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
    void handle_shouldRecordSagaDurationOnSuccess() {
        UUID orderId = UUID.randomUUID();
        Order order = order(orderId);
        order.markReserved();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        useCase.handle(new PaymentSucceedEvent(orderId));

        assertEquals(1L, meterRegistry.get("order.saga.duration")
                .tag("outcome", "completed").timer().count());
        assertNull(meterRegistry.find("order.saga.duplicate_event").counter());
    }

    @Test
    void handle_shouldCountDuplicateAndNotRecordDurationOnRepeatedPayment() {
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
        assertEquals(1.0, meterRegistry.get("order.saga.duplicate_event")
                .tag("event", "payment.succeeded").counter().count());
        assertNull(meterRegistry.find("order.saga.duration").timer());
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
        assertNull(meterRegistry.find("order.saga.duration").timer());
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
        order.setCreatedAt(Instant.now());
        return order;
    }
}
