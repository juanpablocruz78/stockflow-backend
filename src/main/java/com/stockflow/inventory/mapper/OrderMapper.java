package com.stockflow.inventory.mapper;

import com.stockflow.inventory.dto.*;
import com.stockflow.inventory.entity.CustomerOrder;
import com.stockflow.inventory.entity.OrderItem;

import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderResponse toResponse(CustomerOrder order) {

        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getItems().stream()
                        .map(OrderMapper::toItemResponse)
                        .collect(Collectors.toList())
        );
    }

    private static OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getProduct().getId(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal()
        );
    }
}