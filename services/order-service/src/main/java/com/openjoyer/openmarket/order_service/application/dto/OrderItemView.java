package com.openjoyer.openmarket.order_service.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemView {
    private String skuId;
    private String titleSnapshot;
    private String imageSnapshot;
    private BigDecimal price;
    private Integer quantity;
}
