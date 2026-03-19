package com.openjoyer.openmarket.cart_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CartItem {
    private final String skuId;
    private final String titleSnapshot;
    private final String imageSnapshot;
    private final double priceSnapshot;
    private int quantity;

    public void increaseQuantity(int delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("CartItem: delta < 0");
        }
        this.quantity += delta;
    }
    public void decreaseQuantity(int delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("CartItem: delta < 0");
        }
        if (delta >= quantity) {
            quantity = 0;
        } else {
            this.quantity -= delta;
        }
    }

    public double totalPrice() {
        return priceSnapshot * quantity;
    }
}
