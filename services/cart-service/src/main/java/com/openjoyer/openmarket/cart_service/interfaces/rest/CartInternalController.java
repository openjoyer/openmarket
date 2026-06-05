package com.openjoyer.openmarket.cart_service.interfaces.rest;

import com.openjoyer.openmarket.cart_service.application.usecase.ClearCartUseCase;
import com.openjoyer.openmarket.cart_service.application.usecase.GetCheckoutCartUseCase;
import com.openjoyer.openmarket.contracts.dto.cart.CartCheckoutView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internal/cart")
public class CartInternalController {
    private final GetCheckoutCartUseCase getCheckoutCartUseCase;
    private final ClearCartUseCase clearCartUseCase;

    @GetMapping("/{userId}/checkout")
    public CartCheckoutView getCheckoutCart(@PathVariable String userId) {
        return getCheckoutCartUseCase.handle(userId);
    }

    @DeleteMapping("/{userId}/items")
    void clearCart(@PathVariable String userId) {
        clearCartUseCase.handle(userId);
    }
}
