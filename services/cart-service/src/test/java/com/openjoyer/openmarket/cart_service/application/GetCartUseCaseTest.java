package com.openjoyer.openmarket.cart_service.application;

import com.openjoyer.openmarket.cart_service.application.dto.CartView;
import com.openjoyer.openmarket.cart_service.application.usecase.GetCartUseCase;
import com.openjoyer.openmarket.cart_service.domain.model.Cart;
import com.openjoyer.openmarket.cart_service.domain.model.CartItem;
import com.openjoyer.openmarket.cart_service.domain.repository.CartRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetCartUseCaseTest {

    @Mock
    private CartRepository repository;

    @InjectMocks
    private GetCartUseCase useCase;

    @Test
    void handle_shouldReturnExistingCart() {
        Cart existingCart = new Cart();
        existingCart.setCartId("cart123");
        existingCart.setUserId("user123");
        List<CartItem> items = new ArrayList<>();
        items.add(new CartItem("sku1", "Product 1", "image1.jpg", 100.0, 2));
        existingCart.setItems(items);
        existingCart.setUpdatedAt(Instant.now());

        when(repository.findByUserId("user123")).thenReturn(Optional.of(existingCart));

        CartView result = useCase.handle("user123");

        assertNotNull(result);
        assertEquals("cart123", result.getCartId());
        assertEquals("user123", result.getUserId());
        assertEquals(1, result.getItems().size());
        assertEquals("sku1", result.getItems().get(0).getSkuId());

        verify(repository).findByUserId("user123");
    }

    @Test
    void handle_shouldReturnEmptyCartWhenNotExists() {
        when(repository.findByUserId("user123")).thenReturn(Optional.empty());

        CartView result = useCase.handle("user123");

        assertNotNull(result);
        assertEquals("user123", result.getUserId());
        assertNull(result.getCartId());

        verify(repository).findByUserId("user123");
    }
}