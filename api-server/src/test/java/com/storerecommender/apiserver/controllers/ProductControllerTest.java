package com.storerecommender.apiserver.controllers;

import com.storerecommender.apiserver.dtos.ProductDto;
import com.storerecommender.apiserver.dtos.ProductShortDto;
import com.storerecommender.apiserver.exceptions.ProductNotFoundException;
import com.storerecommender.apiserver.services.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductController.class)
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void getProductGivenExistingSkuReturns200AndProductDetails() throws Exception {
        var dto = ProductDto.builder()
                .sku(123)
                .name("name")
                .brand("brand")
                .category("category")
                .stock(45)
                .price(BigDecimal.valueOf(6.78))
                .build();
        when(productService.getProduct(123))
                        .thenReturn(Map.of("content", dto));

        mockMvc.perform(get("/api/v1/products/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.sku").value(123));
    }

    @Test
    void getProductGivenNonExistingSkuReturns404() throws Exception {
        when(productService.getProduct(999))
                .thenThrow(new ProductNotFoundException(999));

        mockMvc.perform(get("/api/v1/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProductGivenInvalidSkuReturns400() throws Exception {
        mockMvc.perform(get("/api/v1/products/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCatalogReturns200AndPageOfResults() throws Exception {
        Page<ProductShortDto> page = new PageImpl<>(List.of(
                new ProductShortDto(1, "a"),
                new ProductShortDto(2, "b"),
                new ProductShortDto(3, "c")
        ));
        when(productService.getCatalog(Mockito.any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfElements").value(3));
    }
}
