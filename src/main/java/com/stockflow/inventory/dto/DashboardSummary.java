package com.stockflow.inventory.dto;

import com.stockflow.inventory.entity.CustomerOrder;

import java.util.List;

public record DashboardSummary (
        long totalOrders,
        double totalRevenue,
        long totalProducts,
        long lowStockProducts,
        List<CustomerOrder> recentOrders
){
}
