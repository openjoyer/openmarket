package com.openjoyer.openmarket.order_service.application.dto.mapper;

import com.openjoyer.openmarket.order_service.application.dto.OrderView;
import com.openjoyer.openmarket.order_service.domain.model.Order;

public class OrderDtoMapper {
    public static OrderView toOrderView(Order order) {
        OrderView view = new OrderView();
        view.setUserId(order.getUserId());
        view.setOrderId(order.getId().toString());
        view.setItems();
    }
}
