package com.openjoyer.openmarket.order_service.application.usecase;

import com.openjoyer.openmarket.contracts.events.inventory.StockReservedEvent;
import com.openjoyer.openmarket.contracts.events.payment.PaymentRequestedEvent;
import com.openjoyer.openmarket.order_service.application.port.EventPublisherPort;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HandleStockReservationSucceededTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private EventPublisherPort eventPublisherPort;

    private MeterRegistry meterRegistry;
    private HandleStockReservationSucceededUseCase useCase;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        useCase = new HandleStockReservationSucceededUseCase(orderRepository, eventPublisherPort, meterRegistry);
    }

    @Test
    void handle_shouldMovePendingReservationOrderToPaymentPending() {
        UUID orderId = UUID.randomUUID();
        Order order = order(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        useCase.handle(new StockReservedEvent(orderId));

        assertEquals(OrderStatus.PAYMENT_PENDING, order.getOrderStatus());
        assertNotNull(order.getReservedAt());
        verify(orderRepository).findById(orderId);
    }

    @Test
    void handle_shouldPublishPaymentRequestWithOrderTotalOnReservation() {
        UUID orderId = UUID.randomUUID();
        OrderItem keyboard = OrderItem.create("sku-1", "Keyboard", "keyboard.png", BigDecimal.TEN, 2);
        OrderItem mouse = OrderItem.create("sku-2", "Mouse", "mouse.png", new BigDecimal("5"), 3);
        Order order = Order.create("user-1", List.of(keyboard, mouse));
        order.setId(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        useCase.handle(new StockReservedEvent(orderId));

        ArgumentCaptor<PaymentRequestedEvent> captor = ArgumentCaptor.forClass(PaymentRequestedEvent.class);
        verify(eventPublisherPort).publishPaymentRequestEvent(captor.capture());
        PaymentRequestedEvent published = captor.getValue();
        assertEquals(orderId, published.orderId());
        assertEquals("user-1", published.userId());
        assertEquals(0, published.amount().compareTo(new BigDecimal("35")));
        assertNull(meterRegistry.find("order.saga.duplicate_event").counter());
    }

    @Test
    void handle_shouldCountDuplicateAndNotRequestPaymentTwiceOnReplay() {
        UUID orderId = UUID.randomUUID();
        Order order = order(orderId);
        order.markReserved();
        var reservedAt = order.getReservedAt();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        useCase.handle(new StockReservedEvent(orderId));

        assertEquals(OrderStatus.PAYMENT_PENDING, order.getOrderStatus());
        assertEquals(reservedAt, order.getReservedAt());
        verify(eventPublisherPort, never()).publishPaymentRequestEvent(any());
        assertEquals(1.0, meterRegistry.get("order.saga.duplicate_event")
                .tag("event", "stock.reservation.succeeded").counter().count());
    }

    @Test
    void handle_shouldNotReviveCancelledOrderNorRequestPayment() {
        UUID orderId = UUID.randomUUID();
        Order order = order(orderId);
        order.cancel();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        useCase.handle(new StockReservedEvent(orderId));

        assertEquals(OrderStatus.CANCELED, order.getOrderStatus());
        verify(eventPublisherPort, never()).publishPaymentRequestEvent(any());
        assertEquals(1.0, meterRegistry.get("order.saga.duplicate_event")
                .tag("event", "stock.reservation.succeeded").counter().count());
    }

    @Test
    void handle_shouldThrowWhenOrderDoesNotExist() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> useCase.handle(new StockReservedEvent(orderId)));
        verify(orderRepository).findById(orderId);
        verify(eventPublisherPort, never()).publishPaymentRequestEvent(any());
    }

    private Order order(UUID orderId) {
        OrderItem item = OrderItem.create("sku-1", "Keyboard", "keyboard.png", BigDecimal.TEN, 1);
        Order order = Order.create("user-1", List.of(item));
        order.setId(orderId);
        return order;
    }
}
