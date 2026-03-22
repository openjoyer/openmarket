package com.openjoyer.openmarket.cart_service.interfaces.rest.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RemoveItemRequest {
    private String skuId;
    private int deleteAmount;
}
