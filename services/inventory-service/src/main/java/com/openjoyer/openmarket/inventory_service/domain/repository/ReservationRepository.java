package com.openjoyer.openmarket.inventory_service.domain.repository;

import com.openjoyer.openmarket.inventory_service.domain.model.Reservation;

import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository {
    Reservation save(Reservation reservation);
    boolean existsByOrderId(UUID orderId);
    Optional<Reservation> findByOrderId(UUID orderId);
}
