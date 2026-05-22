package com.openjoyer.openmarket.order_service.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "order_items")
@Data
public class OrderItem {
    @Id
    @UuidGenerator
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id",  nullable = false)
    private Order order;

    @Column(nullable = false, name = "sku_id")
    private String skuId;

    @Column(name = "title_snapshot")
    private String titleSnapshot;

    @Column(name = "image_snapshot")
    private String imageSnapshot;

    @Column
    private Double price;

    @Column
    private Integer quantity;
}