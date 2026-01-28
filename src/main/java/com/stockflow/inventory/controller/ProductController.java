package com.stockflow.inventory.controller;

import com.stockflow.inventory.dto.ProductRequest;
import com.stockflow.inventory.dto.ProductResponse;
import com.stockflow.inventory.entity.Product;
import com.stockflow.inventory.mapper.ProductMapper;
import com.stockflow.inventory.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    private final ProductMapper mapper;

    public ProductController(ProductService productService, ProductMapper mapper) {
        this.productService = productService;
        this.mapper = mapper;
    }

    @PostMapping
    public ProductResponse create(
            @Valid @RequestBody ProductRequest request
    ) {
        Product product = productService.create(request);
        return mapper.toResponse(product);
    }

    @GetMapping
    public Page<ProductResponse> list(
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return productService.findAll(pageable)
                .map(mapper::toResponse);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }

    @PutMapping("/{id}")
    public ProductResponse update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        Product product = productService.update(id, request);
        return mapper.toResponse(product);
    }
}
