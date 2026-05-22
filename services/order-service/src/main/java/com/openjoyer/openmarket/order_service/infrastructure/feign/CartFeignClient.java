package com.openjoyer.openmarket.order_service.infrastructure.feign;

import com.openjoyer.openmarket.contracts.cart.CartCheckoutView;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cart-service", path = "/api/v1/internal/cart")
public interface CartFeignClient {
    @GetMapping("/{userId}/checkout")
    ResponseEntity<CartCheckoutView> getCheckoutCart(@PathVariable String userId);
}
