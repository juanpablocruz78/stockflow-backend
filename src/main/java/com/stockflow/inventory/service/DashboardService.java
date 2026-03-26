package com.stockflow.inventory.service;

import com.stockflow.inventory.dto.DashboardSummaryDTO;
import com.stockflow.inventory.repository.CustomerOrderRepository;
import com.stockflow.inventory.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DashboardService {
    private final CustomerOrderRepository orderRepository;
    private final ProductRepository productRepository;

    public DashboardService(CustomerOrderRepository orderRepository,
                            ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public DashboardSummaryDTO getSummary() {

        long totalOrders = orderRepository.count();
        long totalProducts = productRepository.count();
        double revenue = Optional.ofNullable(orderRepository.sumTotalAmount()).orElse(0.0);
        long lowStock = productRepository.countByStockLessThan(10);

        return new DashboardSummaryDTO(
                totalOrders,
                totalProducts,
                revenue,
                lowStock
        );
    }
}
