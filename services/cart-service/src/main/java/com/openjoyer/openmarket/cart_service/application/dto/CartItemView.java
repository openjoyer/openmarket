package com.openjoyer.openmarket.cart_service.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartItemView {
    private String skuId;
    private String titleSnapshot;
    private String imageSnapshot;
    private double priceSnapshot;
    private int quantity;
}
