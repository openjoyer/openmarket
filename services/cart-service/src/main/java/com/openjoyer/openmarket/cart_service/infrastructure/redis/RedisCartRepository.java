package com.openjoyer.openmarket.cart_service.infrastructure.redis;

import com.openjoyer.openmarket.cart_service.domain.model.Cart;
import com.openjoyer.openmarket.cart_service.domain.repository.CartRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
public class RedisCartRepository implements CartRepository {
    private final RedisTemplate<String, RedisCartDocument> redisTemplate;
    private final Duration ttl;

    public RedisCartRepository(
            RedisTemplate<String, RedisCartDocument> template,
            @Value("${cart.ttl}") Duration ttl
    ) {
        this.redisTemplate = template;
        this.ttl = ttl;
    }

    @Override
    public Optional<Cart> findByUserId(String userId) {
        String key = CartKeyGenerator.getKey(userId);
        RedisCartDocument doc = redisTemplate.opsForValue().get(key);
        if (doc == null) {
            return Optional.empty();
        }
        return Optional.of(RedisCartMapper.mapToDomain(doc));
    }

    @Override
    public Cart save(Cart cart) {
        String key = CartKeyGenerator.getKey(cart.getUserId());
        RedisCartDocument doc = RedisCartMapper.mapToDocument(cart);
        redisTemplate.opsForValue().set(key, doc, ttl);
        return cart;
    }

    @Override
    public void deleteByUserId(String userId) {
        String key = CartKeyGenerator.getKey(userId);
        redisTemplate.delete(key);
    }

    static class CartKeyGenerator {
        private CartKeyGenerator() {}

        public static String getKey(String userId) {
            return "cart:" + userId;
        }
    }
}
