package com.openjoyer.openmarket.cart_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    private String cartId;
    private String userId;
    private List<CartItem> items;
    private Instant updatedAt;

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void addItem(CartItem item) {
        Optional<CartItem> foundItem = items.stream()
                .filter(i -> i.getSkuId().equals(item.getSkuId()))
                .findFirst();
        if (foundItem.isPresent()) {
            foundItem.get().increaseQuantity(item.getQuantity());
        } else {
            items.add(item);
        }
        this.updatedAt = Instant.now();
    }

    public void removeItem(String skuId, int quantity) {
        Optional<CartItem> foundItem = items.stream()
                .filter(i -> i.getSkuId().equals(skuId))
                .findFirst();
        foundItem.ifPresent(item -> item.decreaseQuantity(quantity));
    }

    public double totalPrice() {
        return items.stream()
                .mapToDouble(CartItem::totalPrice)
                .sum();
    }
}
