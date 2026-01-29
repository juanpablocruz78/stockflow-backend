package com.stockflow.inventory.dto;


import java.math.BigDecimal;
import java.time.Instant;

public class ProductResponse {
    public ProductResponse(Long id, String name, Integer stock, BigDecimal price, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.stock = stock;
        this.price = price;
        this.createdAt = createdAt;
    }

    private Long id;
    private String name;
    private Integer stock;
    private BigDecimal price;
    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
