package com.openjoyer.openmarket.order_service.interfaces.rest;

import com.openjoyer.openmarket.order_service.application.dto.OrderView;
import com.openjoyer.openmarket.order_service.application.usecase.GetOrderByIdUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final GetOrderByIdUseCase getOrderUseCase;

    @GetMapping("")
    public ResponseEntity<List<OrderView>> getOrdersByUserId() {

    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderView> getOrderById(@PathVariable String orderId) {

    }
}