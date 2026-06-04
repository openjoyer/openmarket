package com.openjoyer.openmarket.order_service.application.usecase;

import com.openjoyer.openmarket.contracts.dto.cart.CartCheckoutView;
import com.openjoyer.openmarket.contracts.events.inventory.StockReservationRequestEvent;
import com.openjoyer.openmarket.contracts.events.inventory.StockReservationRequestItem;
import com.openjoyer.openmarket.order_service.application.command.CreateOrderCommand;
import com.openjoyer.openmarket.order_service.application.dto.OrderView;
import com.openjoyer.openmarket.order_service.application.dto.mapper.OrderDtoMapper;
import com.openjoyer.openmarket.order_service.application.port.CartQueryPort;
import com.openjoyer.openmarket.order_service.application.port.EventPublisherPort;
import com.openjoyer.openmarket.order_service.domain.exceptions.CartException;
import com.openjoyer.openmarket.order_service.domain.model.Order;
import com.openjoyer.openmarket.order_service.domain.model.OrderItem;
import com.openjoyer.openmarket.order_service.domain.repository.OrderRepository;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CreateOrderUseCase {
    private final CartQueryPort cartQueryPort;
    private final OrderRepository orderRepository;
    private final EventPublisherPort eventPublisherPort;
    private final MeterRegistry meterRegistry;

    @Transactional
    public OrderView handle(CreateOrderCommand command) {
        CartCheckoutView view = cartQueryPort.getCartCheckout(command.userId());
        if(view == null || view.isEmpty()) {
            meterRegistry.counter("order.rejected", "reason", "empty_cart").increment();
            throw new CartException(command.userId());
        }

        Order order = Order.create(command.userId(), view.getItems().stream()
                .map(item -> OrderItem.create(
                        item.getSkuId(),
                        item.getTitleSnapshot(),
                        item.getImageSnapshot(),
                        item.getPriceSnapshot(),
                        item.getQuantity()
                )).toList()
        );

        Order saved = orderRepository.save(order);

        StockReservationRequestEvent event = new StockReservationRequestEvent(
                order.getId(),
                order.getItems().stream()
                        .map(item -> new StockReservationRequestItem(
                                item.getSkuId(),
                                item.getTitleSnapshot(),
                                item.getImageSnapshot(),
                                item.getPrice(),
                                item.getQuantity()
                        )).toList()
        );
        eventPublisherPort.publishStockReservationRequestEvent(event);

        meterRegistry.counter("order.created").increment();
        return OrderDtoMapper.toOrderView(saved);
    }
}
