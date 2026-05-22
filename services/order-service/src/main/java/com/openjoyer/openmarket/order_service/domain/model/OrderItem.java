package com.openjoyer.openmarket.order_service.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {
    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @EqualsAndHashCode.Exclude
    @Setter(AccessLevel.PACKAGE)
    @ToString.Exclude
    private Order order;

    @Column(nullable = false, name = "sku_id")
    private String skuId;

    @Column(name = "title_snapshot")
    private String titleSnapshot;

    @Column(name = "image_snapshot")
    private String imageSnapshot;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer quantity;

    public static OrderItem create(String skuId, String titleSnapshot, String imageSnapshot, BigDecimal price, Integer quantity) {
        if (skuId == null || skuId.isBlank()) {
            throw new IllegalArgumentException("skuId must not be blank");
        }
        if (price == null || price.signum() < 0) {
            throw new IllegalArgumentException("price must not be negative");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }

        OrderItem item = new OrderItem();
        item.skuId = skuId;
        item.titleSnapshot = titleSnapshot;
        item.imageSnapshot = imageSnapshot;
        item.price = price;
        item.quantity = quantity;
        return item;
    }
}
