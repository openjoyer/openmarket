package com.openjoyer.openmarket.order_service.interfaces.rest;

import com.openjoyer.openmarket.order_service.application.dto.OrderItemView;
import com.openjoyer.openmarket.order_service.application.dto.OrderView;
import com.openjoyer.openmarket.order_service.application.usecase.GetOrderByIdUseCase;
import com.openjoyer.openmarket.order_service.application.usecase.GetOrderByUserIdUseCase;
import com.openjoyer.openmarket.order_service.domain.exceptions.OrderNotFoundException;
import com.openjoyer.openmarket.order_service.domain.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {
    private MockMvc mockMvc;

    @Mock
    private GetOrderByIdUseCase getOrderByIdUseCase;

    @Mock
    private GetOrderByUserIdUseCase getOrderByUserIdUseCase;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController)
                .setControllerAdvice(new OrderExceptionHandler())
                .build();
    }

    @Test
    void getOrderById_shouldReturnOrder() throws Exception {
        UUID orderId = UUID.randomUUID();
        OrderView view = orderView(orderId);
        when(getOrderByIdUseCase.handle(orderId)).thenReturn(view);

        mockMvc.perform(get("/api/v1/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId.toString()))
                .andExpect(jsonPath("$.userId").value("user-1"))
                .andExpect(jsonPath("$.orderStatus").value("CREATED"))
                .andExpect(jsonPath("$.items[0].skuId").value("sku-1"));
    }

    @Test
    void getOrderById_shouldReturnNotFoundWhenOrderDoesNotExist() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(getOrderByIdUseCase.handle(orderId)).thenThrow(new OrderNotFoundException(orderId));

        mockMvc.perform(get("/api/v1/orders/{orderId}", orderId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getOrderById_shouldReturnBadRequestForInvalidUuid() throws Exception {
        mockMvc.perform(get("/api/v1/orders/not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad request"));
    }

    @Test
    void getOrdersByUserId_shouldReturnOrders() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(getOrderByUserIdUseCase.handle("user-1")).thenReturn(List.of(orderView(orderId)));

        mockMvc.perform(get("/api/v1/orders/user").param("id", "user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(orderId.toString()))
                .andExpect(jsonPath("$[0].userId").value("user-1"));
    }

    private OrderView orderView(UUID orderId) {
        OrderItemView item = new OrderItemView("sku-1", "Keyboard", "keyboard.png", BigDecimal.TEN, 1);
        return new OrderView(orderId.toString(), "user-1", OrderStatus.PENDING_RESERVATION, List.of(item));
    }
}