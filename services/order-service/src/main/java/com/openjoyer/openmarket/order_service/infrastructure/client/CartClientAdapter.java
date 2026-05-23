package com.openjoyer.openmarket.order_service.infrastructure.client;

import com.openjoyer.openmarket.contracts.dto.cart.CartCheckoutView;
import com.openjoyer.openmarket.order_service.application.port.CartCommandPort;
import com.openjoyer.openmarket.order_service.application.port.CartQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartClientAdapter implements CartQueryPort, CartCommandPort {
    private final CartFeignClient cartFeignClient;

    @Override
    public CartCheckoutView getCartCheckout(String userId) {
        return cartFeignClient.getCheckoutCart(userId);
    }

    @Override
    public void clearCart(String userId) {
        cartFeignClient.clearCart(userId);
    }
}
