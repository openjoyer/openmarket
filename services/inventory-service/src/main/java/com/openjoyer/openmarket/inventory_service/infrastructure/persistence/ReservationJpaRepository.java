package com.openjoyer.openmarket.inventory_service.infrastructure.persistence;

import com.openjoyer.openmarket.inventory_service.domain.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationJpaRepository extends JpaRepository<Reservation, UUID> {
    boolean existsByOrderId(UUID orderId);

    Optional<Reservation> findByOrderId(UUID orderId);
}
