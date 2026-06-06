package com.openjoyer.openmarket.inventory_service.domain.model;

import com.openjoyer.openmarket.inventory_service.domain.exceptions.InsufficientStockException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "inventory_items")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class InventoryItem {
    @Id
    @Column(name = "sku_id")
    private String skuId;
    private int available;
    private int reserved;

    @Version
    private long version;

    public static InventoryItem of(String skuId, int quantity) {
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.skuId = skuId;
        inventoryItem.available = quantity;
        inventoryItem.reserved = 0;

        return inventoryItem;
    }

    public void replenish(int quantity) {
        if(quantity <= 0) return;
        this.available += quantity;
    }

    public void reserve(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Reserve quantity must be positive, got " + quantity);
        }
        if(available < quantity) {
            throw new InsufficientStockException(skuId, quantity, available);
        }
        available -= quantity;
        reserved += quantity;
    }

    public void release(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Release quantity must be positive, got " + quantity);
        }
        if (reserved < quantity) {
            throw new IllegalArgumentException("Cannot release " + quantity + " for sku " + skuId + ", only " + reserved + " reserved");
        }
        reserved  -= quantity;
        available += quantity;
    }

    public void confirm(int quantity) {
        if (reserved < quantity) {
            throw new IllegalArgumentException("Cannot confirm " + quantity + " for sku " + skuId + ", only " + reserved + " reserved");
        }
        reserved -= quantity;
    }
}
