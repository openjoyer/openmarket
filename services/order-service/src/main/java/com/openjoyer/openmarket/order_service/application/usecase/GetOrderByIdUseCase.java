package com.openjoyer.openmarket.order_service.application.usecase;

import com.openjoyer.openmarket.order_service.application.dto.OrderView;
import com.openjoyer.openmarket.order_service.domain.model.Order;
import com.openjoyer.openmarket.order_service.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetOrderByIdUseCase {
    private final OrderRepository orderRepository;

    public OrderView handle(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
    }
}
