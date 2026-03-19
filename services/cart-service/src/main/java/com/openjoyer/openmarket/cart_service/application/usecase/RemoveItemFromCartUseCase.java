package com.openjoyer.openmarket.cart_service.application.usecase;

import com.openjoyer.openmarket.cart_service.application.dto.CartView;
import com.openjoyer.openmarket.cart_service.application.usecase.util.CartUseCaseMapper;import com.openjoyer.openmarket.cart_service.domain.model.Cart;
import com.openjoyer.openmarket.cart_service.domain.repository.CartRepository;
import com.openjoyer.openmarket.cart_service.interfaces.rest.request.DeleteItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoveItemFromCartUseCase {
    private final CartRepository repository;

    public CartView handle(String userId, DeleteItemRequest request) {
        Cart cart = repository.findByUserId(userId).orElse(null);
        if(cart == null) {
            throw new IllegalArgumentException("Cart not exists: " + userId);
        }
        cart.removeItem(request.getSkuId(), request.getDeleteAmount());
        Cart result = repository.save(cart);
        return CartUseCaseMapper.mapToCartView(result);
    }
}
