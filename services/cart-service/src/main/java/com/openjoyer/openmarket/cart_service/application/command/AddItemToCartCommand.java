package com.openjoyer.openmarket.cart_service.application.command;

import java.util.Objects;

public record AddItemToCartCommand(
        String userId,
        String skuId,
        int quantity,
        String titleSnapshot,
        String imageSnapshot,
        long priceSnapshotMinor,
        String currency
) {
    public AddItemToCartCommand {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(skuId);
        Objects.requireNonNull(titleSnapshot);
        Objects.requireNonNull(currency);

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        if (priceSnapshotMinor < 0) {
            throw new IllegalArgumentException("Price must be non-negative");
        }
    }
}
