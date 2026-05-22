package com.openjoyer.openmarket.order_service.application.dto.mapper;

import com.openjoyer.openmarket.order_service.application.dto.OrderItemView;
import com.openjoyer.openmarket.order_service.application.dto.OrderView;
import com.openjoyer.openmarket.order_service.domain.model.Order;
import com.openjoyer.openmarket.order_service.domain.model.OrderItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderDtoMapper {
    public static OrderView toOrderView(Order order) {
        if (order == null) return null;

        List<OrderItemView> items = (order.getItems() == null) ?
                Collections.emptyList() : order.getItems().stream()
                .map(OrderDtoMapper::toItemView)
                .toList();
        OrderView view = new OrderView();
        view.setUserId((order.getUserId() != null && !order.getUserId().isEmpty()) ? order.getUserId() : null);
        view.setOrderId(order.getId().toString());
        view.setOrderStatus(order.getOrderStatus());
        view.setItems(items);
        return view;
    }

    public static OrderItemView toItemView(OrderItem item) {
        OrderItemView view = new OrderItemView();
        view.setSkuId(item.getSkuId());
        view.setTitleSnapshot(item.getTitleSnapshot());
        view.setImageSnapshot(item.getImageSnapshot());
        view.setPrice(item.getPrice());
        view.setQuantity(item.getQuantity());
        return view;
    }
}
