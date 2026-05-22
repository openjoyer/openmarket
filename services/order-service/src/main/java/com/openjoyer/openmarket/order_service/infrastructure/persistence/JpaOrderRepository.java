package com.openjoyer.openmarket.order_service.infrastructure.persistence;

import com.openjoyer.openmarket.order_service.domain.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaOrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserId(String userId);
}
