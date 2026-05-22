package com.openjoyer.openmarket.order_service.infrastructure.persistence;

import com.openjoyer.openmarket.order_service.domain.model.Order;
import com.openjoyer.openmarket.order_service.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final JpaOrderRepository jpaOrderRepository;

    @Override
    public Order save(Order order) {
        return jpaOrderRepository.save(order);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return jpaOrderRepository.findByIdWithItems(id);
    }

    @Override
    public List<Order> findByUserId(String userId) {
        return jpaOrderRepository.findByUserIdWithItems(userId);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaOrderRepository.existsById(id);
    }
}
