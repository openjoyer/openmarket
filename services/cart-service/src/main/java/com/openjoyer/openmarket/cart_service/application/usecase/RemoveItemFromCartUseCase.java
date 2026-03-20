package com.openjoyer.openmarket.cart_service.application.usecase;

import com.openjoyer.openmarket.cart_service.application.command.RemoveItemCommand;
import com.openjoyer.openmarket.cart_service.application.dto.CartView;
import com.openjoyer.openmarket.cart_service.application.usecase.util.CartUseCaseMapper;import com.openjoyer.openmarket.cart_service.domain.model.Cart;
import com.openjoyer.openmarket.cart_service.domain.repository.CartRepository;
import com.openjoyer.openmarket.cart_service.interfaces.rest.request.DeleteItemRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoveItemFromCartUseCase {
    private final CartRepository repository;

    @Transactional
    public CartView handle(RemoveItemCommand command) {
        Cart cart = repository.findByUserId(command.userId()).orElseGet(() -> Cart.empty(command.userId()));
        cart.removeItem(command.skuId(), command.quantity());
        Cart result = repository.save(cart);
        return CartUseCaseMapper.mapToCartView(result);
    }
}
