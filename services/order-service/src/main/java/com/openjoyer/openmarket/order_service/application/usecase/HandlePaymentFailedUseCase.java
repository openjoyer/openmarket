package com.openjoyer.openmarket.order_service.application.usecase;

import com.openjoyer.openmarket.contracts.events.payment.PaymentFailedEvent;
import com.openjoyer.openmarket.order_service.domain.exceptions.OrderNotFoundException;
import com.openjoyer.openmarket.order_service.domain.model.Order;
import com.openjoyer.openmarket.order_service.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HandlePaymentFailedUseCase {
    private final OrderRepository orderRepository;

    public void handle(PaymentFailedEvent event) {
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new OrderNotFoundException(event.orderId()));

        order.cancel();
        // TODO add stock release
    }
}
