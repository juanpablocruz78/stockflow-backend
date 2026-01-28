package com.stockflow.inventory.mapper;

import com.stockflow.inventory.dto.ProductRequest;
import com.stockflow.inventory.dto.ProductResponse;
import com.stockflow.inventory.entity.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    // Request → Entity
    Product toEntity(ProductRequest request);

    // Entity → Response
    ProductResponse toResponse(Product product);
    // Update Entity from Request (MUY IMPORTANTE)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(ProductRequest request, @MappingTarget Product product);
}
