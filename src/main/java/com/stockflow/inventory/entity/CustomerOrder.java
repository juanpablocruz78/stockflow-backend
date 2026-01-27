package com.stockflow.inventory.entity;

import com.stockflow.common.entity.BaseEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "orders")
public class CustomerOrder extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalAmount;
}
