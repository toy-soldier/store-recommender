package com.storerecommender.webapp.schemas;

/** A single product entry from the api-server catalog (sku + description). */
public record ProductInventoryLineItem(
        Integer sku,
        String description
) {}
