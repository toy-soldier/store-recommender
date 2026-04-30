package com.storerecommender.apiserver.dtos;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class ProductDto {
    private Integer sku;
    private String name;
    private String brand;
    private String category;
    private BigDecimal price;
    private Integer stock;
}
