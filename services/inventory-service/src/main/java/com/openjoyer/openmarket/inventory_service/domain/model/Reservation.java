package com.openjoyer.openmarket.inventory_service.domain.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reservations")
@Getter
public class Reservation {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "sku_id")
    private String skuId;

    private int quantity;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Column(name = "created")
    private Instant createdAt;
    @Column(name = "updated")
    private Instant updatedAt;

    public static Reservation of(UUID orderId, String skuId, int quantity) {
        Reservation reservation = new Reservation();
        reservation.skuId = skuId;
        reservation.orderId = orderId;
        reservation.quantity = quantity;
        reservation.createdAt = Instant.now();
        reservation.updatedAt = Instant.now();
        reservation.status = ReservationStatus.RESERVED;
        return reservation;
    }

    public void updateStatus(ReservationStatus status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
        this.updatedAt = Instant.now();
    }
}
