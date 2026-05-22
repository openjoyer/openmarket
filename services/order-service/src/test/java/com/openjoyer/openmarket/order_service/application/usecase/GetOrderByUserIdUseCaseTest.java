package com.openjoyer.openmarket.order_service.application.usecase;

import com.openjoyer.openmarket.order_service.application.dto.OrderView;
import com.openjoyer.openmarket.order_service.domain.model.Order;
import com.openjoyer.openmarket.order_service.domain.model.OrderItem;
import com.openjoyer.openmarket.order_service.domain.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetOrderByUserIdUseCaseTest {
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private GetOrderByUserIdUseCase useCase;

    @Test
    void handle_shouldReturnMappedOrders() {
        OrderItem item = OrderItem.create("sku-1", "Keyboard", "keyboard.png", BigDecimal.TEN, 1);
        Order order = Order.create("user-1", List.of(item));
        order.setId(UUID.randomUUID());
        when(orderRepository.findByUserId("user-1")).thenReturn(List.of(order));

        List<OrderView> result = useCase.handle("user-1");

        assertEquals(1, result.size());
        assertEquals("user-1", result.get(0).getUserId());
        verify(orderRepository).findByUserId("user-1");
    }

    @Test
    void handle_shouldReturnEmptyListWhenRepositoryReturnsEmptyList() {
        when(orderRepository.findByUserId("user-1")).thenReturn(List.of());

        List<OrderView> result = useCase.handle("user-1");

        assertTrue(result.isEmpty());
        verify(orderRepository).findByUserId("user-1");
    }

    @Test
    void handle_shouldReturnEmptyListWhenRepositoryReturnsNull() {
        when(orderRepository.findByUserId("user-1")).thenReturn(null);

        List<OrderView> result = useCase.handle("user-1");

        assertTrue(result.isEmpty());
        verify(orderRepository).findByUserId("user-1");
    }
}
