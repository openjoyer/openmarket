package com.openjoyer.openmarket.cart_service.interfaces.rest;

import com.openjoyer.openmarket.cart_service.application.dto.CartView;
import com.openjoyer.openmarket.cart_service.application.usecase.AddItemToCartUseCase;
import com.openjoyer.openmarket.cart_service.application.usecase.GetCartUseCase;
import com.openjoyer.openmarket.cart_service.application.usecase.RemoveItemFromCartUseCase;import com.openjoyer.openmarket.cart_service.interfaces.rest.request.AddItemRequest;
import com.openjoyer.openmarket.cart_service.interfaces.rest.request.DeleteItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {
    private final GetCartUseCase getCartUseCase;
    private final AddItemToCartUseCase addItemToCartUseCase;
    private final RemoveItemFromCartUseCase removeItemFromCartUseCase;

    @GetMapping("/{userId}")
    public CartView getCart(@PathVariable String userId) {
        return getCartUseCase.handle(userId);
    }

    @PostMapping("/{userId}")
    public CartView addItem(@PathVariable String userId, @RequestBody AddItemRequest request) {
        return addItemToCartUseCase.handle(userId, request);
    }

    @DeleteMapping("/{userId}")
    public CartView removeItem(@PathVariable String userId, @RequestBody DeleteItemRequest request) {
        return removeItemFromCartUseCase.handle(userId, request);
    }
}
