package com.openjoyer.openmarket.order_service.application.dto;

import com.openjoyer.openmarket.order_service.domain.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderView {
    private String orderId;
    private String userId;
    private OrderStatus orderStatus;
    private List<OrderItemView> items;
}
