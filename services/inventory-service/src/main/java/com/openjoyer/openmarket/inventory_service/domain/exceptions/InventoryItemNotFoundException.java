package com.openjoyer.openmarket.inventory_service.domain.exceptions;

public class InventoryItemNotFoundException extends RuntimeException {
    public InventoryItemNotFoundException(String skuId) {
        super(skuId);
    }
}
