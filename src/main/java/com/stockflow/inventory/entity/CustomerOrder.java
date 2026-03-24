package com.stockflow.inventory.entity;

import com.stockflow.inventory.enums.OrderStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "customer_orders")
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private BigDecimal totalAmount = BigDecimal.ZERO;

    private LocalDateTime createdAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<OrderItem> items = new ArrayList<>();

    protected CustomerOrder() {
        // JPA
    }

    public CustomerOrder(Long customerId) {
        this.customerId = customerId;
        this.status = OrderStatus.CREATED;
        this.createdAt = LocalDateTime.now();
    }

    // =========================
    // BEHAVIOR (Business Logic)
    // =========================

    public void addItem(Product product, Integer quantity) {

        ensureOrderIsModifiable();

        OrderItem item = new OrderItem(this, product, quantity, product.getPrice());

        items.add(item);

        recalculateTotal();
    }

    public void removeItem(Long productId) {

        ensureOrderIsModifiable();

        items.removeIf(item -> item.getProduct().getId().equals(productId));

        recalculateTotal();
    }

    public void confirm() {
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot confirm order without items");
        }

        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("Only created orders can be confirmed");
        }

        this.status = OrderStatus.CONFIRMED;
    }

    public void cancel() {

        if (status == OrderStatus.SHIPPED) {
            throw new IllegalStateException("Cannot cancel shipped order");
        }

        this.status = OrderStatus.CANCELLED;
    }

    private void ensureOrderIsModifiable() {
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("Order cannot be modified in current state");
        }
    }

    private void recalculateTotal() {
        this.totalAmount = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // =========================
    // GETTERS
    // =========================

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public Long getCustomerId() {
        return customerId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}

