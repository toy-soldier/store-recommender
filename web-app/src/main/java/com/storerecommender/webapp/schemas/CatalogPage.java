package com.storerecommender.webapp.schemas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CatalogPage(
        List<ProductInventoryLineItem> content,
        int totalPages
) {}
