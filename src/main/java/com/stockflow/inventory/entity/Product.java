package com.stockflow.inventory.entity;

import com.stockflow.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "products")
public class Product extends BaseEntity {
    public Product() {
    }

    public Product(String name, Integer stock, BigDecimal price, String description, Boolean active, Instant now) {
        this.name = name;
        this.stock = stock;
        this.price = price;
        this.description = description;
        this.active = active;
    }

    @NotBlank
    @Column(nullable = false, length = 150)
    private String name;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Integer stock;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column
    private String description;

    @Column(nullable = false)
    private Boolean active = true;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        return getId() != null && getId().equals(((Product) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @PrePersist
    public void prePersist() {
        if (active == null) active = true;
    }
}
