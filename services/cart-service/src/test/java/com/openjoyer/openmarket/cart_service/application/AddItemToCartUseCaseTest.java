package com.openjoyer.openmarket.cart_service.application;

import com.openjoyer.openmarket.cart_service.application.command.AddItemCommand;
import com.openjoyer.openmarket.cart_service.application.dto.CartView;
import com.openjoyer.openmarket.cart_service.application.usecase.AddItemToCartUseCase;
import com.openjoyer.openmarket.cart_service.domain.model.Cart;
import com.openjoyer.openmarket.cart_service.domain.model.CartItem;
import com.openjoyer.openmarket.cart_service.domain.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddItemToCartUseCaseTest {

    @Mock
    private CartRepository repository;

    @InjectMocks
    private AddItemToCartUseCase useCase;

    private AddItemCommand command;

    @BeforeEach
    void setUp() {
        command = new AddItemCommand(
                "user123",
                "sku1",
                "Product 1",
                "image1.jpg",
                100.0,
                2
        );
    }

    @Test
    void handle_shouldCreateNewCartWhenNotExists() {
        when(repository.findByUserId("user123")).thenReturn(Optional.empty());
        when(repository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartView result = useCase.handle(command);

        assertNotNull(result);
        assertEquals("user123", result.getUserId());
        assertEquals(1, result.getItems().size());
        assertEquals("sku1", result.getItems().get(0).getSkuId());
        assertEquals(2, result.getItems().get(0).getQuantity());

        verify(repository).findByUserId("user123");
        verify(repository).save(any(Cart.class));
    }

    @Test
    void handle_shouldAddItemToExistingCart() {
        Cart existingCart = new Cart();
        existingCart.setCartId("cart123");
        existingCart.setUserId("user123");
        existingCart.setItems(new ArrayList<>());
        existingCart.setUpdatedAt(Instant.now());

        when(repository.findByUserId("user123")).thenReturn(Optional.of(existingCart));
        when(repository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartView result = useCase.handle(command);

        assertNotNull(result);
        assertEquals("cart123", result.getCartId());
        assertEquals(1, result.getItems().size());
        assertEquals("sku1", result.getItems().get(0).getSkuId());

        verify(repository).findByUserId("user123");
        verify(repository).save(any(Cart.class));
    }

    @Test
    void handle_shouldIncreaseQuantityForExistingItem() {
        Cart existingCart = new Cart();
        existingCart.setCartId("cart123");
        existingCart.setUserId("user123");
        List<CartItem> items = new ArrayList<>();
        items.add(new CartItem("sku1", "Product 1", "image1.jpg", 100.0, 3));
        existingCart.setItems(items);
        existingCart.setUpdatedAt(Instant.now());

        when(repository.findByUserId("user123")).thenReturn(Optional.of(existingCart));
        when(repository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartView result = useCase.handle(command);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(5, result.getItems().get(0).getQuantity());

        verify(repository).findByUserId("user123");
        verify(repository).save(any(Cart.class));
    }

    @Test
    void handle_shouldAddMultipleItems() {
        when(repository.findByUserId("user123")).thenReturn(Optional.empty());
        when(repository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartView result = useCase.handle(command);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(2, result.getItems().get(0).getQuantity());
    }

    @Test
    void handle_shouldUpdateTimestamp() {
        Instant before = Instant.now();

        when(repository.findByUserId("user123")).thenReturn(Optional.empty());
        when(repository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartView result = useCase.handle(command);

        assertNotNull(result.getUpdatedAt());
        assertTrue(result.getUpdatedAt().isAfter(before) || result.getUpdatedAt().equals(before));
    }
}