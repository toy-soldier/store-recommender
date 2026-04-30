package com.storerecommender.apiserver.controllers;

import com.storerecommender.apiserver.dtos.ProductDto;
import com.storerecommender.apiserver.dtos.ProductShortDto;
import com.storerecommender.apiserver.services.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/{sku}")
    public ResponseEntity<Map<String, ProductDto>> getProduct(@PathVariable("sku") Integer sku) {
        return ResponseEntity.ok(productService.getProduct(sku));
    }

    @GetMapping
    public ResponseEntity<Page<ProductShortDto>> getCatalog(Pageable pageable) {
        return ResponseEntity.ok(productService.getCatalog(pageable));
    }
}
