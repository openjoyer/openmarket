package com.openjoyer.openmarket.order_service.application.usecase;

import com.openjoyer.openmarket.order_service.application.dto.OrderView;
import com.openjoyer.openmarket.order_service.application.dto.mapper.OrderDtoMapper;
import com.openjoyer.openmarket.order_service.domain.model.Order;
import com.openjoyer.openmarket.order_service.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetOrderByUserIdUseCase {
    private final OrderRepository orderRepository;

    public List<OrderView> handle(String userId) {
        List<Order> orders = orderRepository.findByUserId(userId);

        if(orders == null || orders.isEmpty()) {
            return Collections.emptyList();
        }
        return orders.stream()
                .map(OrderDtoMapper::toOrderView)
                .toList();
    }
}
