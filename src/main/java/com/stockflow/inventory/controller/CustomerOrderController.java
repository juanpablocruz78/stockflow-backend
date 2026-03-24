package com.stockflow.inventory.controller;

import com.stockflow.inventory.dto.*;
import com.stockflow.inventory.entity.CustomerOrder;
import com.stockflow.inventory.enums.OrderStatus;
import com.stockflow.inventory.mapper.OrderMapper;
import com.stockflow.inventory.service.CustomerOrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class CustomerOrderController {

    private final CustomerOrderService service;

    public CustomerOrderController(CustomerOrderService service) {
        this.service = service;
    }

    // =========================
    // CREATE ORDER
    // =========================

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {

        CustomerOrder order = service.createOrder(request.getCustomerId());

        return ResponseEntity.ok(OrderMapper.toResponse(order));
    }

    // =========================
    // ADD PRODUCT
    // =========================

    @PostMapping("/{orderId}/items")
    public ResponseEntity<Void> addProduct(
            @PathVariable Long orderId,
            @Valid @RequestBody AddProductRequest request) {

        service.addProductToOrder(
                orderId,
                request.getProductId(),
                request.getQuantity()
        );

        return ResponseEntity.ok().build();
    }

    // =========================
    // CONFIRM ORDER
    // =========================

    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<Void> confirm(@PathVariable Long orderId) {

        service.confirmOrder(orderId);

        return ResponseEntity.ok().build();
    }

    // =========================
    // CANCEL ORDER
    // =========================

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long orderId) {

        service.cancelOrder(orderId);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public Page<OrderResponseDTO> getOrders(
            @RequestParam int page,
            @RequestParam int size) {

        Pageable pageable = PageRequest.of(page, size);

        return service.getOrders(pageable);
    }
}