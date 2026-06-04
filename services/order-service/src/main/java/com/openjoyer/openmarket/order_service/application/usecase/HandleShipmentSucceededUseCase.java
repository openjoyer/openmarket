package com.openjoyer.openmarket.order_service.application.usecase;

import com.openjoyer.openmarket.contracts.events.shipment.ShipmentSucceededEvent;
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
public class HandleShipmentSucceededUseCase {
    private final OrderRepository orderRepository;
    private final MeterRegistry meterRegistry;

    @Transactional
    public void handle(ShipmentSucceededEvent event) {
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new OrderNotFoundException(event.orderId()));

        if(order.complete()) {
            orderRepository.save(order);
            meterRegistry.timer("order.saga.duration", "outcome", "completed", "reason", "none")
                    .record(Duration.between(order.getCreatedAt(), Instant.now()));
        } else {
            meterRegistry.counter("order.saga.duplicate_event", "event", "shipment.succeeded").increment();
        }
    }
}