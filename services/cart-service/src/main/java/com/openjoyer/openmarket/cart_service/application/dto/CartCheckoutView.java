package com.openjoyer.openmarket.cart_service.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CartCheckoutView {
    private String userId;
    private List<CartCheckoutItemView> items;
    private boolean isEmpty;
}
