package com.openjoyer.openmarket.order_service.application.usecase;

import com.openjoyer.openmarket.contracts.dto.cart.CartCheckoutItemView;
import com.openjoyer.openmarket.contracts.dto.cart.CartCheckoutView;
import com.openjoyer.openmarket.contracts.events.inventory.StockReservationRequestEvent;
import com.openjoyer.openmarket.order_service.application.command.CreateOrderCommand;
import com.openjoyer.openmarket.order_service.application.dto.OrderView;
import com.openjoyer.openmarket.order_service.application.port.CartQueryPort;
import com.openjoyer.openmarket.order_service.application.port.EventPublisherPort;
import com.openjoyer.openmarket.order_service.domain.exceptions.CartException;
import com.openjoyer.openmarket.order_service.domain.model.Order;
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
class CreateOrderUseCaseTest {
    @Mock
    private CartQueryPort cartQueryPort;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private EventPublisherPort eventPublisherPort;

    private MeterRegistry meterRegistry;
    private CreateOrderUseCase useCase;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        useCase = new CreateOrderUseCase(cartQueryPort, orderRepository, eventPublisherPort, meterRegistry);
    }

    @Test
    void handle_shouldCreateOrderPublishReservationAndCountCreated() {
        CartCheckoutItemView item = new CartCheckoutItemView("sku-1", "Keyboard", "keyboard.png", BigDecimal.TEN, 2);
        CartCheckoutView cart = new CartCheckoutView("user-1", List.of(item), false);
        when(cartQueryPort.getCartCheckout("user-1")).thenReturn(cart);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order saved = inv.getArgument(0);
            saved.setId(UUID.randomUUID());
            saved.setCreatedAt(Instant.now());
            return saved;
        });

        OrderView result = useCase.handle(new CreateOrderCommand("user-1"));

        assertNotNull(result);
        verify(orderRepository).save(any(Order.class));
        verify(eventPublisherPort).publishStockReservationRequestEvent(any(StockReservationRequestEvent.class));
        assertEquals(1.0, meterRegistry.get("order.created").counter().count());
        assertNull(meterRegistry.find("order.rejected").counter());
    }

    @Test
    void handle_shouldRejectEmptyCartAndCountRejection() {
        CartCheckoutView cart = new CartCheckoutView("user-1", List.of(), true);
        when(cartQueryPort.getCartCheckout("user-1")).thenReturn(cart);

        assertThrows(CartException.class, () -> useCase.handle(new CreateOrderCommand("user-1")));

        verify(orderRepository, never()).save(any());
        verify(eventPublisherPort, never()).publishStockReservationRequestEvent(any());
        assertEquals(1.0, meterRegistry.get("order.rejected")
                .tag("reason", "empty_cart").counter().count());
        assertNull(meterRegistry.find("order.created").counter());
    }

    @Test
    void handle_shouldRejectNullCartAndCountRejection() {
        when(cartQueryPort.getCartCheckout("user-1")).thenReturn(null);

        assertThrows(CartException.class, () -> useCase.handle(new CreateOrderCommand("user-1")));

        verify(orderRepository, never()).save(any());
        verify(eventPublisherPort, never()).publishStockReservationRequestEvent(any());
        assertEquals(1.0, meterRegistry.get("order.rejected")
                .tag("reason", "empty_cart").counter().count());
    }
}