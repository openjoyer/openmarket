package com.openjoyer.openmarket.cart_service.infrastructure.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisCartDocument {
    private String cartId;
    private String userId;
    private List<RedisCartItemDocument> items;
    private Instant updatedAt;
}
