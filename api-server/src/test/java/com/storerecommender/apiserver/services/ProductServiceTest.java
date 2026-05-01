package com.storerecommender.apiserver.services;

import com.storerecommender.apiserver.dtos.ProductDto;
import com.storerecommender.apiserver.dtos.ProductShortDto;
import com.storerecommender.apiserver.entities.Product;
import com.storerecommender.apiserver.exceptions.ProductNotFoundException;
import com.storerecommender.apiserver.mappers.ProductMapper;
import com.storerecommender.apiserver.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Spy
    private ProductMapper                                                                                                                                                                                productMapper;
    @InjectMocks
    private ProductService productService;

    @Test
    void getProductGivenExistingSkuReturnsProductDetails() {
        var product = Product.builder()
                .sku(123)
                .name("name")
                .brand("brand")
                .category("category")
                .price(BigDecimal.valueOf(4.56))
                .stock(78)
                .build();
        when(productRepository.findById(123))
                .thenReturn(Optional.of(product));

        Map<String, ProductDto> productDetails = productService.getProduct(123);
        assertEquals(123, productDetails.get("content").getSku());
    }

    @Test
    void getProductGivenNonExistingSkuThrowsProductNotFoundException() {
        when(productRepository.findById(999))
                .thenReturn(Optional.empty());

        ProductNotFoundException pnfe = assertThrows(ProductNotFoundException.class,
                () -> productService.getProduct(999));
        assertEquals("Product 999 not found", pnfe.getMessage());
    }

    @Test
    void getCatalogReturnsPageWithThreeProducts() {
        List<Product> products = List.of(
                Product.builder().sku(1).brand("brand").name("a").build(),
                Product.builder().sku(2).brand("brand").name("b").build(),
                Product.builder().sku(3).brand("brand").name("c").build()
        );
        when(productRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(products));

        Page<ProductShortDto> page = productService.getCatalog(Pageable.unpaged());

        List<ProductShortDto> dtos = page.getContent();
        assertEquals(3, dtos.size());
        for (int i = 0; i < dtos.size(); i++) {
            assertEquals(products.get(i).getSku(), dtos.get(i).sku());
        }
    }
}