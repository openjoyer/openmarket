package com.openjoyer.openmarket.order_service.application.usecase;

import com.openjoyer.openmarket.contracts.events.shipment.ShipmentSucceededEvent;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HandleShipmentSucceededUseCaseTest {
    @Mock
    private OrderRepository orderRepository;

    private MeterRegistry meterRegistry;
    private HandleShipmentSucceededUseCase useCase;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        useCase = new HandleShipmentSucceededUseCase(orderRepository, meterRegistry);
    }

    @Test
    void handle_shouldCompleteProcessingOrderAndRecordCompletedDuration() {
        UUID orderId = UUID.randomUUID();
        Order order = processingOrder(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        useCase.handle(new ShipmentSucceededEvent(orderId, Instant.now()));

        assertEquals(OrderStatus.COMPLETED, order.getOrderStatus());
        verify(orderRepository).save(order);
        assertEquals(1L, meterRegistry.get("order.saga.duration")
                .tag("outcome", "completed").tag("reason", "none").timer().count());
        assertNull(meterRegistry.find("order.saga.duplicate_event").counter());
    }

    @Test
    void handle_shouldCountDuplicateAndNotCompleteAgainOnReplay() {
        UUID orderId = UUID.randomUUID();
        Order order = processingOrder(orderId);
        order.complete();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        useCase.handle(new ShipmentSucceededEvent(orderId, Instant.now()));

        assertEquals(OrderStatus.COMPLETED, order.getOrderStatus());
        verify(orderRepository, never()).save(any());
        assertEquals(1.0, meterRegistry.get("order.saga.duplicate_event")
                .tag("event", "shipment.succeeded").counter().count());
        assertNull(meterRegistry.find("order.saga.duration").timer());
    }

    @Test
    void handle_shouldThrowWhenOrderDoesNotExist() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class,
                () -> useCase.handle(new ShipmentSucceededEvent(orderId, Instant.now())));
        verify(orderRepository).findById(orderId);
    }

    private Order processingOrder(UUID orderId) {
        OrderItem item = OrderItem.create("sku-1", "Keyboard", "keyboard.png", BigDecimal.TEN, 1);
        Order order = Order.create("user-1", List.of(item));
        order.setId(orderId);
        order.setCreatedAt(Instant.now());
        order.markReserved();
        order.markPaid();
        return order;
    }
}