package com.openjoyer.openmarket.cart_service.application.usecase.util;

import com.openjoyer.openmarket.cart_service.application.dto.CartItemView;
import com.openjoyer.openmarket.cart_service.application.dto.CartView;
import com.openjoyer.openmarket.cart_service.domain.model.Cart;
import com.openjoyer.openmarket.cart_service.domain.model.CartItem;
import com.openjoyer.openmarket.cart_service.interfaces.rest.request.AddItemRequest;

public class CartUseCaseMapper {
    public static CartView mapToCartView(Cart cart) {
        CartView view = new CartView();
        view.setCartId(cart.getCartId());
        view.setUserId(cart.getUserId());
        view.setItems(cart.getItems().stream()
                .map(CartUseCaseMapper::mapToCartItemView)
                .toList()
        );
        view.setUpdatedAt(cart.getUpdatedAt());
        return view;

    }

    public static CartItemView mapToCartItemView(CartItem item) {
        CartItemView itemView = new CartItemView();
        itemView.setSkuId(item.getSkuId());
        itemView.setTitleSnapshot(item.getTitleSnapshot());
        itemView.setImageSnapshot(item.getImageSnapshot());
        itemView.setPriceSnapshot(item.getPriceSnapshot());
        itemView.setQuantity(item.getQuantity());
        return itemView;
    }

    public static CartItem mapRequestToDomain(AddItemRequest req) {
        return new CartItem(
                req.getSkuId(),
                req.getTitleSnapshot(),
                req.getImageSnapshot(),
                req.getPriceSnapshot(),
                req.getQuantity()
        );
    }
}
