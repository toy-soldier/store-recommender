package com.storerecommender.apiserver.mappers;

import com.storerecommender.apiserver.dtos.ProductDto;
import com.storerecommender.apiserver.dtos.ProductShortDto;
import com.storerecommender.apiserver.entities.Product;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ProductMapper {
    public Map<String, ProductDto> toProductDetails(Product product) {
        var productDto = ProductDto.builder()
                .sku(product.getSku())
                .name(product.getName())
                .brand(product.getBrand())
                .category(product.getCategory())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
        return Map.of("content", productDto);
    }

    public ProductShortDto toProductShortDto(Product product) {
        return new ProductShortDto(product.getSku(),
                product.getBrand() + " " + product.getName());
    }
}
