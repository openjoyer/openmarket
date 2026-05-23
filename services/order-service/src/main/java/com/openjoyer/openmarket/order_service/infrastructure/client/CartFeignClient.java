package com.openjoyer.openmarket.order_service.infrastructure.client;

import com.openjoyer.openmarket.contracts.dto.cart.CartCheckoutView;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(name = "cart-service", path = "/api/v1/internal/cart")
public interface CartFeignClient {

    @GetMapping("/{userId}/checkout")
    CartCheckoutView getCheckoutCart(@PathVariable String userId);

    @DeleteMapping("/{userId}/items")
    void clearCart(@PathVariable String userId);
}
