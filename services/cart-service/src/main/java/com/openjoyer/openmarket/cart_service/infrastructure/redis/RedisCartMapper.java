package com.openjoyer.openmarket.cart_service.infrastructure.redis;

import com.openjoyer.openmarket.cart_service.domain.model.Cart;
import com.openjoyer.openmarket.cart_service.domain.model.CartItem;

public class RedisCartMapper {
    public static Cart mapToDomain(RedisCartDocument doc) {
        Cart cart = new Cart();
        cart.setCartId(doc.getCartId());
        cart.setUserId(doc.getUserId());
        cart.setItems(doc.getItems().stream()
                .map(RedisCartMapper::mapToCartItem)
                .toList()
        );
        cart.setUpdatedAt(doc.getUpdatedAt());
        return cart;
    }

    public static RedisCartDocument mapToDocument(Cart cart) {
        RedisCartDocument doc = new RedisCartDocument();
        doc.setCartId(cart.getCartId());
        doc.setUserId(cart.getUserId());
        doc.setItems(cart.getItems().stream()
                .map(RedisCartMapper::mapToItemDocument)
                .toList()
        );
        doc.setUpdatedAt(doc.getUpdatedAt());
        return doc;
    }

    private static CartItem mapToCartItem(RedisCartItemDocument doc) {
        return new CartItem(
                doc.getSkuId(),
                doc.getTitleSnapshot(),
                doc.getImageSnapshot(),
                doc.getPriceSnapshot(),
                doc.getQuantity()
        );
    }

    private static RedisCartItemDocument mapToItemDocument(CartItem item) {
        return new RedisCartItemDocument(
                item.getSkuId(),
                item.getTitleSnapshot(),
                item.getImageSnapshot(),
                item.getPriceSnapshot(),
                item.getQuantity()
        );
    }
}
