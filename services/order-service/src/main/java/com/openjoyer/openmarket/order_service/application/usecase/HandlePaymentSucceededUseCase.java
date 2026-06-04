package com.openjoyer.openmarket.order_service.application.usecase;

import com.openjoyer.openmarket.contracts.events.payment.PaymentSucceedEvent;
import com.openjoyer.openmarket.contracts.events.shipment.ShipmentRequestedEvent;
import com.openjoyer.openmarket.order_service.application.port.CartCommandPort;
import com.openjoyer.openmarket.order_service.application.port.EventPublisherPort;
import com.openjoyer.openmarket.order_service.domain.exceptions.OrderNotFoundException;
import com.openjoyer.openmarket.order_service.domain.model.Order;
import com.openjoyer.openmarket.order_service.domain.repository.OrderRepository;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class HandlePaymentSucceededUseCase {
    private final OrderRepository orderRepository;
    private final CartCommandPort cartCommandPort;
    private final EventPublisherPort eventPublisherPort;
    private final MeterRegistry meterRegistry;

    @Transactional
    public void handle(PaymentSucceedEvent event) {
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new OrderNotFoundException(event.orderId()));

        if (order.markPaid()) {
            cartCommandPort.clearCart(order.getUserId());

            eventPublisherPort.publishShipmentRequestEvent(
                    new ShipmentRequestedEvent(
                            order.getId(),
                            Instant.now()
                    )
            );
        } else {
            meterRegistry.counter("order.saga.duplicate_event",
                    "event", "payment.succeeded").increment();
        }
    }
}