package com.openjoyer.openmarket.contracts.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartCheckoutItemView {
    private String skuId;
    private String titleSnapshot;
    private double priceSnapshot;
    private int quantity;
}
