package com.openjoyer.openmarket.cart_service.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartView {
    private String cartId;
    private String userId;
    private List<CartItemView> items;
    private Instant updatedAt;
}
