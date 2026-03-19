package com.openjoyer.openmarket.cart_service.domain.repository;

import com.openjoyer.openmarket.cart_service.domain.model.Cart;

import java.util.Optional;

public interface CartRepository {
    Optional<Cart> findByUserId(String userId);
    Cart save(Cart cart);
}
