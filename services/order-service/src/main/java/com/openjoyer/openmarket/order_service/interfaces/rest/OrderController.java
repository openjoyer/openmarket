package com.openjoyer.openmarket.order_service.interfaces.rest;

import com.openjoyer.openmarket.order_service.application.command.CreateOrderCommand;
import com.openjoyer.openmarket.order_service.application.dto.OrderView;
import com.openjoyer.openmarket.order_service.application.usecase.CreateOrderUseCase;
import com.openjoyer.openmarket.order_service.application.usecase.GetOrderByIdUseCase;
import com.openjoyer.openmarket.order_service.application.usecase.GetOrderByUserIdUseCase;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final GetOrderByIdUseCase getOrderByIdUseCase;
    private final GetOrderByUserIdUseCase getOrderByUserIdUseCase;
    private final CreateOrderUseCase createOrderUseCase;

    @GetMapping("/user")
    @Loggable
    public ResponseEntity<List<OrderView>> getOrdersByUserId(@RequestParam("id") String userId) {
        List<OrderView> orders = getOrderByUserIdUseCase.handle(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    @Loggable
    public ResponseEntity<OrderView> getOrderById(@PathVariable UUID orderId) {
        OrderView view = getOrderByIdUseCase.handle(orderId);
        return ResponseEntity.ok(view);
    }

    @PostMapping("/create")
    @Loggable
    public ResponseEntity<?> createOrder(@RequestParam @NotBlank String userId) {
        return ResponseEntity.ok(createOrderUseCase.handle(new CreateOrderCommand(userId)));
    }
}
