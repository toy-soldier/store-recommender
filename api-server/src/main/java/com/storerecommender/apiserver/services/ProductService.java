package com.storerecommender.apiserver.services;

import com.storerecommender.apiserver.dtos.ProductDto;
import com.storerecommender.apiserver.dtos.ProductShortDto;
import com.storerecommender.apiserver.entities.Product;
import com.storerecommender.apiserver.exceptions.ProductNotFoundException;
import com.storerecommender.apiserver.mappers.ProductMapper;
import com.storerecommender.apiserver.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public Map<String, ProductDto> getProduct(Integer sku) {
        Product product = productRepository.findById(sku).orElseThrow(
                () -> new ProductNotFoundException(sku)
        );
        return productMapper.toProductDetails(product);
    }

    public Page<ProductShortDto> getCatalog(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(productMapper::toProductShortDto);
    }
}
