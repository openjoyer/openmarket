package com.openjoyer.openmarket.order_service.application.usecase;

import com.openjoyer.openmarket.contracts.events.payment.PaymentSucceedEvent;
import com.openjoyer.openmarket.order_service.application.port.CartCommandPort;
import com.openjoyer.openmarket.order_service.domain.exceptions.OrderNotFoundException;
import com.openjoyer.openmarket.order_service.domain.model.Order;
import com.openjoyer.openmarket.order_service.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HandlePaymentSucceededUseCase {
    private final OrderRepository orderRepository;
    private final CartCommandPort cartCommandPort;

    public void handle(PaymentSucceedEvent event) {
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new OrderNotFoundException(event.orderId()));

        order.markPaid();
        cartCommandPort.clearCart(order.getUserId());
    }
}