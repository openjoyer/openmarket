package com.openjoyer.openmarket.order_service.domain.exceptions;

public class CartException extends RuntimeException {
    public CartException(String userId) {
        super("Cart exception: " + userId);
    }
}
