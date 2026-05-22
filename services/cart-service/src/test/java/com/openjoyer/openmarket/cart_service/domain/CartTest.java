package com.openjoyer.openmarket.cart_service.domain;

import com.openjoyer.openmarket.cart_service.domain.model.Cart;
import com.openjoyer.openmarket.cart_service.domain.model.CartItem;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CartTest {

    @Test
    void empty_shouldCreateEmptyCart() {
        Cart cart = Cart.empty("user123");

        assertEquals("user123", cart.getUserId());
        assertNull(cart.getCartId());
        assertNotNull(cart.getItems());
        assertEquals(0, cart.getItems().size());
        assertNull(cart.getUpdatedAt());
    }

    @Test
    void addItem_shouldAddNewItem() {
        Cart cart = new Cart();
        cart.setUserId("user123");
        cart.setItems(new ArrayList<>());

        CartItem item = new CartItem("sku1", "Product 1", "image1.jpg", 100.0, 2);
        cart.addItem(item);

        assertEquals(1, cart.getItems().size());
        assertEquals("sku1", cart.getItems().get(0).getSkuId());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        assertNotNull(cart.getUpdatedAt());
    }

    @Test
    void addItem_shouldIncreaseQuantityForExistingItem() {
        Cart cart = new Cart();
        cart.setUserId("user123");
        cart.setItems(new ArrayList<>());

        CartItem item1 = new CartItem("sku1", "Product 1", "image1.jpg", 100.0, 2);
        cart.addItem(item1);

        CartItem item2 = new CartItem("sku1", "Product 1", "image1.jpg", 100.0, 3);
        cart.addItem(item2);

        assertEquals(1, cart.getItems().size());
        assertEquals(5, cart.getItems().get(0).getQuantity());
    }

    @Test
    void removeItem_shouldDecreaseQuantity() {
        Cart cart = new Cart();
        cart.setUserId("user123");
        cart.setItems(new ArrayList<>());

        CartItem item = new CartItem("sku1", "Product 1", "image1.jpg", 100.0, 5);
        cart.addItem(item);

        cart.removeItem("sku1", 2);

        assertEquals(1, cart.getItems().size());
        assertEquals(3, cart.getItems().get(0).getQuantity());
    }

    @Test
    void removeItem_shouldRemoveItemWhenQuantityBecomesZero() {
        Cart cart = new Cart();
        cart.setUserId("user123");
        cart.setItems(new ArrayList<>());

        CartItem item = new CartItem("sku1", "Product 1", "image1.jpg", 100.0, 3);
        cart.addItem(item);

        cart.removeItem("sku1", 3);

        assertEquals(0, cart.getItems().size());
    }

    @Test
    void removeItem_shouldRemoveItemWhenQuantityExceedsAvailable() {
        Cart cart = new Cart();
        cart.setUserId("user123");
        cart.setItems(new ArrayList<>());

        CartItem item = new CartItem("sku1", "Product 1", "image1.jpg", 100.0, 3);
        cart.addItem(item);

        cart.removeItem("sku1", 10);

        assertEquals(0, cart.getItems().size());
    }

    @Test
    void removeItem_shouldDoNothingForNonExistentItem() {
        Cart cart = new Cart();
        cart.setUserId("user123");
        cart.setItems(new ArrayList<>());

        CartItem item = new CartItem("sku1", "Product 1", "image1.jpg", 100.0, 3);
        cart.addItem(item);

        cart.removeItem("sku999", 1);

        assertEquals(1, cart.getItems().size());
    }

    @Test
    void totalPrice_shouldCalculateCorrectly() {
        Cart cart = new Cart();
        cart.setUserId("user123");
        cart.setItems(new ArrayList<>());

        cart.addItem(new CartItem("sku1", "Product 1", "image1.jpg", 100.0, 2));
        cart.addItem(new CartItem("sku2", "Product 2", "image2.jpg", 50.0, 3));

        assertEquals(350.0, cart.totalPrice(), 0.01);
    }

    @Test
    void totalPrice_shouldReturnZeroForEmptyCart() {
        Cart cart = new Cart();
        cart.setUserId("user123");
        cart.setItems(new ArrayList<>());

        assertEquals(0.0, cart.totalPrice(), 0.01);
    }

    @Test
    void getItems_shouldReturnUnmodifiableList() {
        Cart cart = new Cart();
        cart.setUserId("user123");
        cart.setItems(new ArrayList<>());

        cart.addItem(new CartItem("sku1", "Product 1", "image1.jpg", 100.0, 2));

        assertThrows(UnsupportedOperationException.class, () -> {
            cart.getItems().clear();
        });
    }
}
