package com.openjoyer.openmarket.order_service.application.usecase;

import com.openjoyer.openmarket.contracts.events.inventory.StockReleaseRequestedEvent;
import com.openjoyer.openmarket.contracts.events.payment.PaymentFailedEvent;
import com.openjoyer.openmarket.order_service.application.port.EventPublisherPort;
import com.openjoyer.openmarket.order_service.domain.exceptions.OrderNotFoundException;
import com.openjoyer.openmarket.order_service.domain.model.Order;
import com.openjoyer.openmarket.order_service.domain.repository.OrderRepository;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class HandlePaymentFailedUseCase {
    private final OrderRepository orderRepository;
    private final MeterRegistry meterRegistry;
    private final EventPublisherPort eventPublisherPort;

    @Transactional
    public void handle(PaymentFailedEvent event) {
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new OrderNotFoundException(event.orderId()));

        if(order.cancel()) {
            meterRegistry.timer("order.saga.duration",
                            "outcome", "canceled", "reason", "payment_failed")
                    .record(Duration.between(order.getCreatedAt(), Instant.now()));

            eventPublisherPort.publishStockReleaseRequestEvent(
                    new StockReleaseRequestedEvent(
                            order.getId()
                    )
            );
        }
    }
}
