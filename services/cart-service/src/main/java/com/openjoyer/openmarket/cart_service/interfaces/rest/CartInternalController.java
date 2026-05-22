package com.openjoyer.openmarket.cart_service.interfaces.rest;

import com.openjoyer.openmarket.cart_service.application.usecase.GetCheckoutCartUseCase;
import com.openjoyer.openmarket.contracts.cart.CartCheckoutView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internal/cart")
public class CartInternalController {
    private final GetCheckoutCartUseCase getCheckoutCartUseCase;

    @GetMapping("/{userId}/checkout")
    public ResponseEntity<CartCheckoutView> getCheckoutCart(@PathVariable String userId) {
        return ResponseEntity.ok(getCheckoutCartUseCase.handle(userId));
    }
}
