package com.openjoyer.openmarket.inventory_service.application.dto;

import com.openjoyer.openmarket.inventory_service.domain.model.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReservationView {
    private UUID orderId;
    private String skuId;
    private int quantity;
    private ReservationStatus status;
}
