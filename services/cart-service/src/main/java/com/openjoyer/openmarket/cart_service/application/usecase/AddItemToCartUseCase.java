package com.openjoyer.openmarket.cart_service.application.usecase;

import com.openjoyer.openmarket.cart_service.application.command.AddItemCommand;
import com.openjoyer.openmarket.cart_service.application.dto.CartView;
import com.openjoyer.openmarket.cart_service.application.usecase.util.CartUseCaseMapper;
import com.openjoyer.openmarket.cart_service.domain.model.Cart;
import com.openjoyer.openmarket.cart_service.domain.model.CartItem;
import com.openjoyer.openmarket.cart_service.domain.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AddItemToCartUseCase {
    private final CartRepository repository;

    @Transactional
    public CartView handle(AddItemCommand command) {
        CartItem item = new CartItem(
                command.skuId(),
                command.titleSnapshot(),
                command.imageSnapshot(),
                command.priceSnapshot(),
                command.quantity()
        );
        Cart cart = repository.findByUserId(command.userId())
                .orElseGet(() -> Cart.empty(command.userId()));
        cart.addItem(item);
        Cart result = repository.save(cart);
        return CartUseCaseMapper.mapToCartView(result);
    }
}
