package com.openjoyer.openmarket.cart_service.application.command;

public record AddItemCommand (
        String userId,
        String skuId,
        String titleSnapshot,
        String imageSnapshot,
        double priceSnapshot,
        int quantity
) {}
