package com.stockflow.inventory.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponseDTO(
        Long id,
        String status,
        BigDecimal totalAmount,
        LocalDateTime createdAt
) {
}
