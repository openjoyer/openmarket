package com.openjoyer.openmarket.order_service.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemView {
    private String skuId;
    private String titleSnapshot;
    private String imageSnapshot;
    private Double price;
    private Integer quantity;
}
