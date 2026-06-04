package com.openjoyer.openmarket.order_service.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Order {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 64)
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "reserved_at")
    private Instant reservedAt;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "refunded_at")
    private Instant refundedAt;

    public BigDecimal getTotalAmount() {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItem item : items) {
            totalAmount = totalAmount.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        return totalAmount;
    }

    public static Order create(String userId, List<OrderItem> items) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId must not be blank");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("order must contain at least one item");
        }

        Order order = new Order();
        order.userId = userId;
        order.orderStatus = OrderStatus.PENDING_RESERVATION;
        items.forEach(order::addItem);
        return order;
    }

    public void addItem(OrderItem item) {
        if (item == null) {
            throw new IllegalArgumentException("order item must not be null");
        }
        item.setOrder(this);
        items.add(item);
    }

    public boolean markReserved() {
        if (orderStatus != OrderStatus.PENDING_RESERVATION) return false;

        orderStatus = OrderStatus.PAYMENT_PENDING;
        reservedAt = Instant.now();
        return true;
    }

    public boolean markPaid() {
        if (orderStatus != OrderStatus.PAYMENT_PENDING) return false;

        orderStatus = OrderStatus.PROCESSING;
        paidAt = Instant.now();
        return true;
    }

    public boolean complete() {
        if (orderStatus != OrderStatus.PROCESSING) return false;

        orderStatus = OrderStatus.COMPLETED;
        completedAt = Instant.now();
        return true;
    }

    public boolean cancel() {
        if (orderStatus != OrderStatus.PENDING_RESERVATION && orderStatus != OrderStatus.PAYMENT_PENDING) return false;

        orderStatus = OrderStatus.CANCELED;
        cancelledAt = Instant.now();
        return true;
    }
}
