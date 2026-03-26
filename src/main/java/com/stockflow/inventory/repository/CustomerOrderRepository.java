package com.stockflow.inventory.repository;

import com.stockflow.inventory.entity.CustomerOrder;
import com.stockflow.inventory.enums.OrderStatus;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {
    Page<CustomerOrder> findAllByStatus(OrderStatus status, Pageable pageable);


    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM CustomerOrder o")
    Double sumTotalAmount();

    List<CustomerOrder> findTop5ByOrderByCreatedAtDesc();
}
