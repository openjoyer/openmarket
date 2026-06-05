package com.openjoyer.openmarket.inventory_service.domain.exceptions;

public class ReservationNotFoundException extends RuntimeException {
    public ReservationNotFoundException(String skuId) {
        super(skuId);
    }
}
