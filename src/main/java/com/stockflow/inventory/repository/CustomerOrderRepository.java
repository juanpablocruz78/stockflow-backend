package com.stockflow.inventory.repository;

import com.stockflow.inventory.entity.CustomerOrder;
import com.stockflow.inventory.enums.OrderStatus;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {
    Page<CustomerOrder> findAllByStatus(OrderStatus status, Pageable pageable);
}
