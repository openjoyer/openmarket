package com.openjoyer.openmarket.contracts.events.inventory;

import java.math.BigDecimal;

public record StockReservationRequestItem(
        String skuId,
        String titleSnapshot,
        String imageSnapshot,
        BigDecimal price,
        Integer quantity
) {}
