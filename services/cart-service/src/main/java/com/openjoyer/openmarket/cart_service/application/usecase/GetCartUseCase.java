package com.openjoyer.openmarket.cart_service.application.usecase;

import com.openjoyer.openmarket.cart_service.application.dto.CartView;
import com.openjoyer.openmarket.cart_service.application.usecase.util.CartUseCaseMapper;import com.openjoyer.openmarket.cart_service.domain.model.Cart;
import com.openjoyer.openmarket.cart_service.domain.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetCartUseCase {
    private final CartRepository repository;

    public CartView handle(String userId) {
        Optional<Cart> cart = repository.findByUserId(userId);
        return cart.map(CartUseCaseMapper::mapToCartView).orElse(null);
    }
}
