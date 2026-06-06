package com.openjoyer.openmarket.inventory_service.infrastructure.persistence;

import com.openjoyer.openmarket.inventory_service.domain.model.Reservation;
import com.openjoyer.openmarket.inventory_service.domain.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {
    private final ReservationJpaRepository repo;

    @Override
    public Reservation save(Reservation reservation) {
        return repo.save(reservation);
    }

    @Override
    public boolean existsByOrderId(UUID orderId) {
        return repo.existsByOrderId(orderId);
    }

    @Override
    public Optional<Reservation> findByOrderId(UUID orderId) {
        return repo.findByOrderId(orderId);
    }

    @Override
    public List<Reservation> findAllByOrderId(UUID orderId) {
        return repo.findAllByOrderId(orderId);
    }
}
