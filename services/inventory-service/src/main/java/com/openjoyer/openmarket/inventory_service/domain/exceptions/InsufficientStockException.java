package com.openjoyer.openmarket.inventory_service.domain.exceptions;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String skuId, int requested, int available) {
        super("Insufficient stock for sku %s: requested %d, available %d".formatted(skuId, requested, available));
    }

    public InsufficientStockException(String skuId) {
        super("Insufficient stock for sku %s".formatted(skuId));
    }
}
