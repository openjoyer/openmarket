package com.openjoyer.openmarket.contracts.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartCheckoutItemView {
    private String skuId;
    private String titleSnapshot;
    private String imageSnapshot;
    private BigDecimal priceSnapshot;
    private int quantity;
}
