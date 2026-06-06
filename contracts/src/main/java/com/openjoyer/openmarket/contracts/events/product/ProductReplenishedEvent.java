package com.openjoyer.openmarket.contracts.events.product;

import java.util.UUID;

public record ProductReplenishedEvent(UUID eventId, String skuId, int quantity) {
}
