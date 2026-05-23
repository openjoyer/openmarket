package com.openjoyer.openmarket.order_service.application.dto.mapper;

import com.openjoyer.openmarket.order_service.application.dto.OrderView;
import com.openjoyer.openmarket.order_service.domain.model.Order;
import com.openjoyer.openmarket.order_service.domain.model.OrderItem;
import com.openjoyer.openmarket.order_service.domain.model.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OrderDtoMapperTest {
    @Test
    void toOrderView_shouldMapOrderFieldsAndItems() {
        UUID orderId = UUID.randomUUID();
        OrderItem item = OrderItem.create("sku-1", "Keyboard", "keyboard.png", BigDecimal.valueOf(199.99), 2);
        Order order = Order.create("user-1", List.of(item));
        order.setId(orderId);

        OrderView view = OrderDtoMapper.toOrderView(order);

        assertEquals(orderId.toString(), view.getOrderId());
        assertEquals("user-1", view.getUserId());
        assertEquals(OrderStatus.PENDING_RESERVATION, view.getOrderStatus());
        assertEquals(1, view.getItems().size());
        assertEquals("sku-1", view.getItems().get(0).getSkuId());
        assertEquals("Keyboard", view.getItems().get(0).getTitleSnapshot());
        assertEquals("keyboard.png", view.getItems().get(0).getImageSnapshot());
        assertEquals(BigDecimal.valueOf(199.99), view.getItems().get(0).getPrice());
        assertEquals(2, view.getItems().get(0).getQuantity());
    }

    @Test
    void toOrderView_shouldReturnNullForNullOrder() {
        assertNull(OrderDtoMapper.toOrderView(null));
    }
}