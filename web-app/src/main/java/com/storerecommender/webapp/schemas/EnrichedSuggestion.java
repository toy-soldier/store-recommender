package com.storerecommender.webapp.schemas;

import java.math.BigDecimal;

/** A recommendation suggestion enriched with live stock and price data from the api-server. */
public record EnrichedSuggestion(
        Integer sku,
        String name,
        String brand,
        String category,
        BigDecimal price,
        Integer stock,
        Integer confidence
) {}
