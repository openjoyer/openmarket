package com.openjoyer.openmarket.order_service.application.usecase;

import com.openjoyer.openmarket.order_service.application.command.CreateOrderCommand;
import com.openjoyer.openmarket.order_service.application.dto.OrderView;
import com.openjoyer.openmarket.order_service.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateOrderUseCase {
    private final OrderRepository orderRepository;

    public OrderView handle(CreateOrderCommand createOrderCommand) {
        OrderView orderView = new OrderView();
    }
}
