package com.storerecommender.webapp.schemas;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/** A recommendation suggestion enriched with live stock and price data from the api-server. */
@Data
@Builder
public class EnrichedSuggestion {
    private Integer sku;
    private String name;
    private String brand;
    private String category;
    private BigDecimal price;
    private Integer stock;
    private Integer confidence;
}
