package com.openjoyer.openmarket.cart_service.infrastructure.redis;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class RedisCartDocument {
    private String cartId;
    private String userId;
    private List<RedisCartItemDocument> items;
    private Instant updatedAt;
}
