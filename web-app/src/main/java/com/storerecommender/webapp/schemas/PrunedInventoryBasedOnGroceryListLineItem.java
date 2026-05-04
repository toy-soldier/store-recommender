package com.storerecommender.webapp.schemas;

/** Pairs a fuzzy-filtered inventory subset with its originating grocery list line item. */
public record PrunedInventoryBasedOnGroceryListLineItem(
        ProductInventory pruned,
        ParsedGroceryListLineItem lineItem
)
{}
