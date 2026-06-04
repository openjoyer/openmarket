package com.openjoyer.openmarket.order_service.application.usecase;

import com.openjoyer.openmarket.contracts.events.inventory.StockReleaseRequestedEvent;
import com.openjoyer.openmarket.contracts.events.payment.PaymentRefundRequestedEvent;
import com.openjoyer.openmarket.contracts.events.shipment.ShipmentFailedEvent;
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
import java.time.Instant;
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
class HandleShipmentFailedUseCaseTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private EventPublisherPort eventPublisherPort;

    private MeterRegistry meterRegistry;
    private HandleShipmentFailedUseCase useCase;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        useCase = new HandleShipmentFailedUseCase(orderRepository, meterRegistry, eventPublisherPort);
    }

    @Test
    void handle_shouldCompensateProcessingOrderWithReleaseRefundAndMetric() {
        UUID orderId = UUID.randomUUID();
        Order order = processingOrder(orderId); // PROCESSING (reserved + paid)
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        useCase.handle(new ShipmentFailedEvent(orderId, Instant.now()));

        assertEquals(OrderStatus.CANCELED, order.getOrderStatus());
        assertNotNull(order.getCancelledAt());
        assertEquals(1L, meterRegistry.get("order.saga.duration")
                .tag("outcome", "canceled").tag("reason", "shipment_failed").timer().count());

        // both compensations: release stock + refund (money was already taken)
        ArgumentCaptor<StockReleaseRequestedEvent> release = ArgumentCaptor.forClass(StockReleaseRequestedEvent.class);
        verify(eventPublisherPort).publishStockReleaseRequestEvent(release.capture());
        assertEquals(orderId, release.getValue().orderId());

        ArgumentCaptor<PaymentRefundRequestedEvent> refund = ArgumentCaptor.forClass(PaymentRefundRequestedEvent.class);
        verify(eventPublisherPort).publishPaymentRefundRequestEvent(refund.capture());
        assertEquals(orderId, refund.getValue().orderId());
        assertEquals(0, refund.getValue().amount().compareTo(BigDecimal.TEN));
    }

    @Test
    void handle_shouldIgnoreReplayAndNotCompensateTwice() {
        UUID orderId = UUID.randomUUID();
        Order order = processingOrder(orderId);
        order.compensate(); // already CANCELED
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        useCase.handle(new ShipmentFailedEvent(orderId, Instant.now()));

        assertEquals(OrderStatus.CANCELED, order.getOrderStatus());
        // idempotency: replayed shipment.failed must NOT trigger a second refund / release
        verify(eventPublisherPort, never()).publishStockReleaseRequestEvent(any());
        verify(eventPublisherPort, never()).publishPaymentRefundRequestEvent(any());
        assertNull(meterRegistry.find("order.saga.duration").timer());
    }

    @Test
    void handle_shouldNotCompensateOrderThatIsNotYetProcessing() {
        UUID orderId = UUID.randomUUID();
        Order order = order(orderId);
        order.markReserved(); // PAYMENT_PENDING, not PROCESSING
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        useCase.handle(new ShipmentFailedEvent(orderId, Instant.now()));

        assertEquals(OrderStatus.PAYMENT_PENDING, order.getOrderStatus());
        verify(eventPublisherPort, never()).publishStockReleaseRequestEvent(any());
        verify(eventPublisherPort, never()).publishPaymentRefundRequestEvent(any());
        assertNull(meterRegistry.find("order.saga.duration").timer());
    }

    @Test
    void handle_shouldThrowWhenOrderDoesNotExist() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class,
                () -> useCase.handle(new ShipmentFailedEvent(orderId, Instant.now())));
        verify(orderRepository).findById(orderId);
    }

    private Order order(UUID orderId) {
        OrderItem item = OrderItem.create("sku-1", "Keyboard", "keyboard.png", BigDecimal.TEN, 1);
        Order order = Order.create("user-1", List.of(item));
        order.setId(orderId);
        order.setCreatedAt(Instant.now());
        return order;
    }

    private Order processingOrder(UUID orderId) {
        Order order = order(orderId);
        order.markReserved();
        order.markPaid();
        return order;
    }
}