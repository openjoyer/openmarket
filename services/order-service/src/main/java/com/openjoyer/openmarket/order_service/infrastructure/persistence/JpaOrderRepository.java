package com.openjoyer.openmarket.order_service.infrastructure.persistence;

import com.openjoyer.openmarket.order_service.domain.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaOrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserId(String userId);

    @Query("select o from Order o left join fetch o.items where o.userId = :userId")
    List<Order> findByUserIdWithItems(String userId);

    @Query("select o from Order o left join fetch o.items where o.id = :id")
    Optional<Order> findByIdWithItems(UUID userId);
}
