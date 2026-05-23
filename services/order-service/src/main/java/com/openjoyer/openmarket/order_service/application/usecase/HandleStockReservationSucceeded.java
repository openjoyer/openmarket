package com.openjoyer.openmarket.order_service.application.usecase;

import com.openjoyer.openmarket.contracts.events.inventory.StockReservedEvent;
import com.openjoyer.openmarket.order_service.domain.exceptions.OrderNotFoundException;
import com.openjoyer.openmarket.order_service.domain.model.Order;
import com.openjoyer.openmarket.order_service.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class HandleStockReservationSucceeded {
    private final OrderRepository orderRepository;

    @Transactional
    public void handle(StockReservedEvent event) {
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new OrderNotFoundException(event.orderId()));

        order.markReserved();
    }
}
