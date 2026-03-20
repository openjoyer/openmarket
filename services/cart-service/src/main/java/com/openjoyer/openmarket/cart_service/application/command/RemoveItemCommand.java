package com.openjoyer.openmarket.cart_service.application.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

public record RemoveItemCommand (
    String skuId,
    String userId,
    int quantity
) {}
