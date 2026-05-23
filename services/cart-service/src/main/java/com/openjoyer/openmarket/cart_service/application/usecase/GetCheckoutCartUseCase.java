package com.openjoyer.openmarket.cart_service.application.usecase;

import com.openjoyer.openmarket.cart_service.application.usecase.util.CartUseCaseMapper;
import com.openjoyer.openmarket.cart_service.domain.model.Cart;
import com.openjoyer.openmarket.cart_service.domain.repository.CartRepository;
import com.openjoyer.openmarket.contracts.dto.cart.CartCheckoutView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetCheckoutCartUseCase {
    private final CartRepository repository;

    public CartCheckoutView handle(String userId) {
        Cart cart = repository.findByUserId(userId).orElse(Cart.empty(userId));
        return CartUseCaseMapper.mapToCartCheckoutView(cart);
    }
}
