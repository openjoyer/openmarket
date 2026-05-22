package com.openjoyer.openmarket.cart_service.domain;

import com.openjoyer.openmarket.cart_service.domain.model.CartItem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CartItemTest {

    @Test
    void increaseQuantity_shouldIncreaseCorrectly() {
        CartItem item = new CartItem("sku1", "Product 1", "image1.jpg", 100.0, 5);

        item.increaseQuantity(3);

        assertEquals(8, item.getQuantity());
    }

    @Test
    void increaseQuantity_shouldThrowExceptionForNegativeDelta() {
        CartItem item = new CartItem("sku1", "Product 1", "image1.jpg", 100.0, 5);

        assertThrows(IllegalArgumentException.class, () -> item.increaseQuantity(-1));
    }

    @Test
    void decreaseQuantity_shouldDecreaseCorrectly() {
        CartItem item = new CartItem("sku1", "Product 1", "image1.jpg", 100.0, 5);

        item.decreaseQuantity(2);

        assertEquals(3, item.getQuantity());
    }

    @Test
    void decreaseQuantity_shouldSetToZeroWhenDeltaExceedsQuantity() {
        CartItem item = new CartItem("sku1", "Product 1", "image1.jpg", 100.0, 5);

        item.decreaseQuantity(10);

        assertEquals(0, item.getQuantity());
    }

    @Test
    void decreaseQuantity_shouldThrowExceptionForNegativeDelta() {
        CartItem item = new CartItem("sku1", "Product 1", "image1.jpg", 100.0, 5);

        assertThrows(IllegalArgumentException.class, () -> item.decreaseQuantity(-1));
    }

    @Test
    void totalPrice_shouldCalculateCorrectly() {
        CartItem item = new CartItem("sku1", "Product 1", "image1.jpg", 99.99, 3);

        assertEquals(299.97, item.totalPrice(), 0.01);
    }

    @Test
    void totalPrice_shouldReturnZeroForZeroQuantity() {
        CartItem item = new CartItem("sku1", "Product 1", "image1.jpg", 100.0, 0);

        assertEquals(0.0, item.totalPrice(), 0.01);
    }
}
