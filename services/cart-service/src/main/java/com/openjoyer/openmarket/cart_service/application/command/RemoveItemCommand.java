package com.openjoyer.openmarket.cart_service.application.command;

public record RemoveItemCommand (
    String skuId,
    String userId,
    int quantity
) {}
