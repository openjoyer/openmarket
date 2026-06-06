package com.openjoyer.openmarket.order_service.application.usecase;

import com.openjoyer.openmarket.contracts.events.inventory.StockReservedEvent;
import com.openjoyer.openmarket.contracts.events.payment.PaymentRequestedEvent;
import com.openjoyer.openmarket.order_service.application.port.EventPublisherPort;
import com.openjoyer.openmarket.order_service.domain.exceptions.OrderNotFoundException;
import com.openjoyer.openmarket.order_service.domain.model.Order;
import com.openjoyer.openmarket.order_service.domain.repository.OrderRepository;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class HandleStockReservationSucceededUseCase {
    private final OrderRepository orderRepository;
    private final EventPublisherPort eventPublisherPort;
    private final MeterRegistry meterRegistry;

    @Transactional
    public void handle(StockReservedEvent event) {
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new OrderNotFoundException(event.orderId()));

        if(order.markReserved()) {
            eventPublisherPort.publishPaymentRequestEvent(
                    new PaymentRequestedEvent(
                            order.getId(),
                            order.getUserId(),
                            order.getTotalAmount()
                    )
            );
        } else {
            meterRegistry.counter("order.saga.duplicate_event", "event", "stock.reservation.succeeded").increment();
        }
    }
}
