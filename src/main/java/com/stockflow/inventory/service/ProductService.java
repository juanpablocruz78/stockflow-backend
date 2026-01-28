package com.stockflow.inventory.service;

import com.stockflow.common.exception.ResourceNotFoundException;
import com.stockflow.inventory.dto.ProductRequest;
import com.stockflow.inventory.dto.ProductResponse;
import com.stockflow.inventory.entity.Product;
import com.stockflow.inventory.mapper.ProductMapper;
import com.stockflow.inventory.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper mapper;

    public ProductService(ProductRepository productRepository, ProductMapper mapper) {
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    public Product create(ProductRequest request) {
        if (productRepository.existsByNameAndActiveTrue(request.getName())) {
            throw new IllegalArgumentException("Product already exists");
        }
        Product product = mapper.toEntity(request);
        return productRepository.save(product);

    }

    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable);
    }

    public void delete(Long id) {
        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.deactivate();
        productRepository.save(product);
    }

    public Product update(Long id, ProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        mapper.updateEntityFromRequest(request, product);
        return productRepository.save(product);
    }
}
