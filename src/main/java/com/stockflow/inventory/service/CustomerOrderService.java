package com.stockflow.inventory.service;

import com.stockflow.inventory.entity.CustomerOrder;
import com.stockflow.inventory.entity.OrderItem;
import com.stockflow.inventory.entity.Product;
import com.stockflow.inventory.enums.OrderStatus;
import com.stockflow.inventory.repository.CustomerOrderRepository;
import com.stockflow.inventory.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomerOrderService {

    private final CustomerOrderRepository orderRepository;
    private final ProductRepository productRepository;

    public CustomerOrderService(CustomerOrderRepository orderRepository,
                                ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    // ==============================
    // CREATE ORDER
    // ==============================

    public CustomerOrder createOrder(Long customerId) {
        CustomerOrder order = new CustomerOrder(customerId);
        return orderRepository.save(order);
    }

    // ==============================
    // ADD PRODUCT TO ORDER
    // ==============================

    public void addProductToOrder(Long orderId, Long productId, Integer quantity) {

        CustomerOrder order = getOrderOrThrow(orderId);
        Product product = getProductOrThrow(productId);

        validateProductIsActive(product);
        validateStock(product, quantity);

        order.addItem(product, quantity);

        orderRepository.save(order);
    }

    // ==============================
    // REMOVE PRODUCT
    // ==============================

    public void removeProductFromOrder(Long orderId, Long productId) {

        CustomerOrder order = getOrderOrThrow(orderId);

        order.removeItem(productId);

        orderRepository.save(order);
    }

    // ==============================
    // CONFIRM ORDER
    // ==============================

    @Transactional
    public void confirmOrder(Long orderId) {

        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (order.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot confirm empty order");
        }

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new IllegalStateException("Order cannot be confirmed");
        }

        // 🔥 Descontar stock
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();

            if (product.getStock() < item.getQuantity()) {
                throw new IllegalStateException("Insufficient stock at confirmation");
            }

            product.setStock(product.getStock() - item.getQuantity());
        }

        order.setStatus(OrderStatus.CONFIRMED);

        orderRepository.save(order);
    }

    // ==============================
    // CANCEL ORDER
    // ==============================

    @Transactional
    public void cancelOrder(Long orderId) {

        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (order.getStatus() == OrderStatus.CONFIRMED) {

            // 🔥 Restaurar stock
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                product.setStock(product.getStock() + item.getQuantity());
            }
        }

        order.setStatus(OrderStatus.CANCELLED);

        orderRepository.save(order);
    }

    // ==============================
    // PRIVATE HELPERS
    // ==============================

    private CustomerOrder getOrderOrThrow(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }

    private Product getProductOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    private void validateStock(Product product, Integer quantity) {
        if (product.getStock() < quantity) {
            throw new IllegalStateException("Insufficient stock");
        }
    }

    private void validateProductIsActive(Product product) {
        if (!product.getActive()) {
            throw new IllegalStateException("Product is not active");
        }
    }
}