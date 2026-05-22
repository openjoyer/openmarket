package com.openjoyer.openmarket.cart_service.application;

import com.openjoyer.openmarket.cart_service.application.command.RemoveItemCommand;
import com.openjoyer.openmarket.cart_service.application.dto.CartView;
import com.openjoyer.openmarket.cart_service.application.usecase.RemoveItemFromCartUseCase;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoveItemFromCartUseCaseTest {

    @Mock
    private CartRepository repository;

    @InjectMocks
    private RemoveItemFromCartUseCase useCase;

    private RemoveItemCommand command;

    @BeforeEach
    void setUp() {
        command = new RemoveItemCommand("sku1", "user123", 2);
    }

    @Test
    void handle_shouldRemoveItemFromCart() {
        Cart existingCart = new Cart();
        existingCart.setCartId("cart123");
        existingCart.setUserId("user123");
        List<CartItem> items = new ArrayList<>();
        items.add(new CartItem("sku1", "Product 1", "image1.jpg", 100.0, 5));
        existingCart.setItems(items);
        existingCart.setUpdatedAt(Instant.now());

        when(repository.findByUserId("user123")).thenReturn(Optional.of(existingCart));
        when(repository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartView result = useCase.handle(command);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(3, result.getItems().get(0).getQuantity());

        verify(repository).findByUserId("user123");
        verify(repository).save(any(Cart.class));
    }

    @Test
    void handle_shouldRemoveItemCompletelyWhenQuantityBecomesZero() {
        Cart existingCart = new Cart();
        existingCart.setCartId("cart123");
        existingCart.setUserId("user123");
        List<CartItem> items = new ArrayList<>();
        items.add(new CartItem("sku1", "Product 1", "image1.jpg", 100.0, 2));
        existingCart.setItems(items);
        existingCart.setUpdatedAt(Instant.now());

        when(repository.findByUserId("user123")).thenReturn(Optional.of(existingCart));
        when(repository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartView result = useCase.handle(command);

        assertNotNull(result);
        assertEquals(0, result.getItems().size());

        verify(repository).findByUserId("user123");
        verify(repository).save(any(Cart.class));
    }

    @Test
    void handle_shouldCreateEmptyCartWhenCartNotExists() {
        when(repository.findByUserId("user123")).thenReturn(Optional.empty());
        when(repository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartView result = useCase.handle(command);

        assertNotNull(result);
        assertEquals("user123", result.getUserId());

        verify(repository).findByUserId("user123");
        verify(repository).save(any(Cart.class));
    }
}
