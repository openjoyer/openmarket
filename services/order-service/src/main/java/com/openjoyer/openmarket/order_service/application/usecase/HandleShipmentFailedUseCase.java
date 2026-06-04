package com.openjoyer.openmarket.order_service.application.usecase;

import com.openjoyer.openmarket.contracts.events.shipment.ShipmentFailedEvent;
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
public class HandleShipmentFailedUseCase {
    private final OrderRepository orderRepository;
    private final MeterRegistry meterRegistry;

    @Transactional
    public void handle(ShipmentFailedEvent event) {
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new OrderNotFoundException(event.orderId()));

        if(order.cancel()) {
            meterRegistry.timer("order.saga.duration", "outcome", "canceled", "reason", "shipment_failed")
                    .record(Duration.between(order.getCreatedAt(), Instant.now()));
        }
        // todo add stock release and balance return
    }
}
