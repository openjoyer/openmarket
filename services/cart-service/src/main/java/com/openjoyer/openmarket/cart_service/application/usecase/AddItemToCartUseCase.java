package com.openjoyer.openmarket.cart_service.application.usecase;

import com.openjoyer.openmarket.cart_service.application.dto.CartView;
import com.openjoyer.openmarket.cart_service.application.usecase.util.CartUseCaseMapper;import com.openjoyer.openmarket.cart_service.domain.model.Cart;
import com.openjoyer.openmarket.cart_service.domain.model.CartItem;
import com.openjoyer.openmarket.cart_service.domain.repository.CartRepository;
import com.openjoyer.openmarket.cart_service.interfaces.rest.request.AddItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddItemToCartUseCase {
    private final CartRepository repository;

    public CartView handle(String userId, AddItemRequest request) {
        CartItem item = CartUseCaseMapper.mapRequestToDomain(request);
        Cart cart = repository.findByUserId(userId).orElse(null);
        if(cart == null) return null;
        cart.addItem(item);
        Cart result = repository.save(cart);
        return CartUseCaseMapper.mapToCartView(result);
    }
}
