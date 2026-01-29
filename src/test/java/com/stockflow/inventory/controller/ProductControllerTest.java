package com.stockflow.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockflow.inventory.dto.ProductRequest;
import com.stockflow.inventory.dto.ProductResponse;
import com.stockflow.inventory.entity.Product;
import com.stockflow.inventory.mapper.ProductMapper;
import com.stockflow.inventory.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductMapper mapper;


    @Test
    void shouldCreateProduct() throws Exception {
        ProductRequest request = new ProductRequest(
                "Laptop",
                10,
                BigDecimal.valueOf(1200)
        );

        Product product = new Product(
                "Laptop",
                10,
                BigDecimal.valueOf(1200),
                "prueba",
                true,
                Instant.now()
        );

        ProductResponse response = new ProductResponse(
                1L,
                "Laptop",
                10,
                BigDecimal.valueOf(1200),
                Instant.now()
        );

        when(productService.create(any(ProductRequest.class)))
                .thenReturn(product);
        when(mapper.toResponse(any(Product.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    void shouldReturn400WhenInvalidRequest() throws Exception {
        ProductRequest request = new ProductRequest(
                "Laptop",
                10,
                BigDecimal.valueOf(1200)
        );
        request.setName(""); // inválido
        request.setStock(-5);
        request.setPrice(BigDecimal.ZERO);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldListProducts() throws Exception {
        Product product = new Product(
                "Laptop",
                10,
                BigDecimal.valueOf(1200),
                "prueba",
                true,
                Instant.now()
        );
        product.setId(1L);

        ProductResponse response = new ProductResponse(
                1L,
                "Laptop",
                10,
                BigDecimal.valueOf(1200),
                Instant.now()
        );
        response.setId(1L);
        response.setName("Laptop");

        Mockito.when(productService.findAll(Mockito.any()))
                .thenReturn(new PageImpl<>(java.util.List.of(product)));

        Mockito.when(mapper.toResponse(product))
                .thenReturn(response);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }


    @Test
    void shouldDeleteProduct() throws Exception {
        Mockito.doNothing().when(productService).delete(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistingProduct() throws Exception {
        ProductRequest request = new ProductRequest(
                "Laptop",
                10,
                BigDecimal.valueOf(1200)
        );
        request.setName("Test");
        request.setStock(1);
        request.setPrice(BigDecimal.ONE);

        Mockito.when(productService.update(Mockito.eq(99L), Mockito.any()))
                .thenThrow(new RuntimeException("Product not found"));

        mockMvc.perform(put("/api/products/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }


}