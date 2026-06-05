package com.openjoyer.openmarket.inventory_service.application.dto.mapper;

import com.openjoyer.openmarket.inventory_service.application.dto.ReservationView;
import com.openjoyer.openmarket.inventory_service.domain.model.Reservation;

public class InventoryDtoMapper {
    public static ReservationView mapToReservationView(Reservation reservation){
        ReservationView reservationView = new ReservationView();
        reservationView.setSkuId(reservation.getSkuId());
        reservationView.setQuantity(reservation.getQuantity());
        reservationView.setStatus(reservation.getStatus());
        return reservationView;
    }
}
