package com.openjoyer.openmarket.cart_service.interfaces.rest;

import com.openjoyer.openmarket.cart_service.application.command.AddItemCommand;
import com.openjoyer.openmarket.cart_service.application.command.RemoveItemCommand;
import com.openjoyer.openmarket.cart_service.application.dto.CartView;
import com.openjoyer.openmarket.cart_service.application.usecase.AddItemToCartUseCase;
import com.openjoyer.openmarket.cart_service.application.usecase.GetCartUseCase;
import com.openjoyer.openmarket.cart_service.application.usecase.RemoveItemFromCartUseCase;import com.openjoyer.openmarket.cart_service.interfaces.rest.request.AddItemRequest;
import com.openjoyer.openmarket.cart_service.interfaces.rest.request.DeleteItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {
    private final GetCartUseCase getCartUseCase;
    private final AddItemToCartUseCase addItemToCartUseCase;
    private final RemoveItemFromCartUseCase removeItemFromCartUseCase;

    @GetMapping("/{userId}")
    public ResponseEntity<CartView> getCart(@PathVariable String userId) {
        return ResponseEntity.ok(getCartUseCase.handle(userId));
    }

    @PostMapping("/{userId}")
    public ResponseEntity<CartView> addItem(@PathVariable String userId, @RequestBody AddItemRequest request) {
        AddItemCommand command = new AddItemCommand(
                userId,
                request.getSkuId(),
                request.getTitleSnapshot(),
                request.getImageSnapshot(),
                request.getPriceSnapshot(),
                request.getQuantity()
        );
        return ResponseEntity.ok(addItemToCartUseCase.handle(command));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<CartView> removeItem(@PathVariable String userId, @RequestBody DeleteItemRequest request) {
        RemoveItemCommand command = new RemoveItemCommand(
                request.getSkuId(),
                userId,
                request.getDeleteAmount()
        );
        return ResponseEntity.ok(removeItemFromCartUseCase.handle(command));
    }
}
