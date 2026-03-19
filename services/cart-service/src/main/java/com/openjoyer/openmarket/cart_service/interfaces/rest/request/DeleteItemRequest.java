package com.openjoyer.openmarket.cart_service.interfaces.rest.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class DeleteItemRequest {
    private String skuId;
    private int deleteAmount;
}
