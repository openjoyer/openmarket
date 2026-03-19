package com.openjoyer.openmarket.cart_service.infrastructure.redis;

import com.openjoyer.openmarket.cart_service.domain.model.Cart;
import com.openjoyer.openmarket.cart_service.domain.repository.CartRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RedisCartRepository implements CartRepository {

    @Override
    public Optional<Cart> findByUserId(String userId) {
        return Optional.empty();
    }

    @Override
    public Cart save(Cart cart) {
        return null;
    }
}
