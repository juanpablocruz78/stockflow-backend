package com.stockflow.inventory.dto;

public class DashboardSummaryDTO {
    private long totalOrders;
    private long totalProducts;
    private double revenue;
    private long lowStockProducts;

    public DashboardSummaryDTO(long totalOrders, long totalProducts,
                               double revenue, long lowStockProducts) {
        this.totalOrders = totalOrders;
        this.totalProducts = totalProducts;
        this.revenue = revenue;
        this.lowStockProducts = lowStockProducts;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public long getLowStockProducts() {
        return lowStockProducts;
    }

    public void setLowStockProducts(long lowStockProducts) {
        this.lowStockProducts = lowStockProducts;
    }
}
