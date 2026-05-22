package com.openjoyer.openmarket.cart_service.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openjoyer.openmarket.cart_service.application.dto.CartItemView;
import com.openjoyer.openmarket.cart_service.application.dto.CartView;
import com.openjoyer.openmarket.cart_service.application.usecase.AddItemToCartUseCase;
import com.openjoyer.openmarket.cart_service.application.usecase.GetCartUseCase;
import com.openjoyer.openmarket.cart_service.application.usecase.RemoveItemFromCartUseCase;
import com.openjoyer.openmarket.cart_service.interfaces.rest.CartController;
import com.openjoyer.openmarket.cart_service.interfaces.rest.request.AddItemRequest;
import com.openjoyer.openmarket.cart_service.interfaces.rest.request.RemoveItemRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private GetCartUseCase getCartUseCase;

    @Mock
    private AddItemToCartUseCase addItemToCartUseCase;

    @Mock
    private RemoveItemFromCartUseCase removeItemFromCartUseCase;

    @InjectMocks
    private CartController cartController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void ping_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1/cart/ping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void getCart_shouldReturnCart() throws Exception {
        CartView cartView = new CartView();
        cartView.setCartId("cart123");
        cartView.setUserId("user123");
        cartView.setItems(new ArrayList<>());
        cartView.setUpdatedAt(Instant.now());

        when(getCartUseCase.handle("user123")).thenReturn(cartView);

        mockMvc.perform(get("/api/v1/cart/user123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value("cart123"))
                .andExpect(jsonPath("$.userId").value("user123"));
    }

    @Test
    void addItem_shouldAddItemToCart() throws Exception {
        AddItemRequest request = new AddItemRequest();
        request.setSkuId("sku1");
        request.setTitleSnapshot("Product 1");
        request.setImageSnapshot("image1.jpg");
        request.setPriceSnapshot(100.0);
        request.setQuantity(2);

        CartItemView itemView = new CartItemView();
        itemView.setSkuId("sku1");
        itemView.setTitleSnapshot("Product 1");
        itemView.setImageSnapshot("image1.jpg");
        itemView.setPriceSnapshot(100.0);
        itemView.setQuantity(2);

        CartView cartView = new CartView();
        cartView.setCartId("cart123");
        cartView.setUserId("user123");
        cartView.setItems(List.of(itemView));
        cartView.setUpdatedAt(Instant.now());

        when(addItemToCartUseCase.handle(any())).thenReturn(cartView);

        mockMvc.perform(post("/api/v1/cart/user123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value("cart123"))
                .andExpect(jsonPath("$.items[0].skuId").value("sku1"))
                .andExpect(jsonPath("$.items[0].quantity").value(2));
    }

    @Test
    void removeItem_shouldRemoveItemFromCart() throws Exception {
        RemoveItemRequest request = new RemoveItemRequest("sku1", 1);

        CartView cartView = new CartView();
        cartView.setCartId("cart123");
        cartView.setUserId("user123");
        cartView.setItems(new ArrayList<>());
        cartView.setUpdatedAt(Instant.now());

        when(removeItemFromCartUseCase.handle(any())).thenReturn(cartView);

        mockMvc.perform(delete("/api/v1/cart/user123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value("cart123"))
                .andExpect(jsonPath("$.userId").value("user123"));
    }
}
