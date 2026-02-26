package com.stockflow.inventory.service;

import com.stockflow.inventory.entity.CustomerOrder;
import com.stockflow.inventory.entity.Product;
import com.stockflow.inventory.enums.OrderStatus;
import com.stockflow.inventory.repository.CustomerOrderRepository;
import com.stockflow.inventory.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerOrderServiceTest {
    @Mock
    private CustomerOrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CustomerOrderService service;

    private CustomerOrder order;
    private Product product;

    @BeforeEach
    void setup() {
        order = new CustomerOrder(1L);

        product = new Product();
        product.setId(10L);
        product.setPrice(new BigDecimal("100"));
        product.setStock(10);
        product.setActive(true);
    }

    @Test
    void shouldCreateOrder() {

        when(orderRepository.save(any(CustomerOrder.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CustomerOrder result = service.createOrder(1L);

        assertEquals(OrderStatus.CREATED, result.getStatus());
        assertEquals(1L, result.getCustomerId());
        verify(orderRepository).save(any(CustomerOrder.class));
    }

    @Test
    void shouldAddProductToOrder() {

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        service.addProductToOrder(1L, 10L, 2);

        assertEquals(1, order.getItems().size());
        assertEquals(new BigDecimal("200"), order.getTotalAmount());
        verify(orderRepository).save(order);
    }

    @Test
    void shouldThrowWhenStockIsInsufficient() {

        product.setStock(1);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.addProductToOrder(1L, 10L, 5)
        );

        assertEquals("Insufficient stock", ex.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenProductIsInactive() {

        product.setActive(false);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        assertThrows(
                IllegalStateException.class,
                () -> service.addProductToOrder(1L, 10L, 1)
        );

        verify(orderRepository, never()).save(any());
    }

    @Test
    void shouldConfirmOrder() {

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        service.addProductToOrder(1L, 10L, 1);

        service.confirmOrder(1L);

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        verify(orderRepository, atLeastOnce()).save(order);
    }

    @Test
    void shouldNotConfirmEmptyOrder() {

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(
                IllegalStateException.class,
                () -> service.confirmOrder(1L)
        );
    }

    @Test
    void shouldCancelOrder() {

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        service.cancelOrder(1L);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void shouldReduceStockWhenConfirming() {

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        service.addProductToOrder(1L, 10L, 2);
        service.confirmOrder(1L);

        assertEquals(8, product.getStock());
    }

    @Test
    void shouldRestoreStockWhenCancelingConfirmedOrder() {

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        service.addProductToOrder(1L, 10L, 2);
        service.confirmOrder(1L);
        service.cancelOrder(1L);

        assertEquals(10, product.getStock());
    }
}