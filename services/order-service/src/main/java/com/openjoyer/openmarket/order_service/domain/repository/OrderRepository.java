package com.openjoyer.openmarket.order_service.domain.repository;

import com.openjoyer.openmarket.order_service.domain.model.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(UUID id);
    List<Order> findByUserId(String userId);
    boolean existsById(UUID id);
}
