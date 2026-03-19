package com.openjoyer.openmarket.cart_service.infrastructure.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisCartItemDocument {
    private String skuId;
    private String titleSnapshot;
    private String imageSnapshot;
    private double priceSnapshot;
    private int quantity;
}
