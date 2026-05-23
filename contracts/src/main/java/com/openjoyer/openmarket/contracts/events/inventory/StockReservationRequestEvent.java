package com.openjoyer.openmarket.contracts.events.inventory;

import java.util.List;
import java.util.UUID;

public record StockReservationRequestEvent(
    UUID orderId,
    List<StockReservationRequestItem> items
) {}
