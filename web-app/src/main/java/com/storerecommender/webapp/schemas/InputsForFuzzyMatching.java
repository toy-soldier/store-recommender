package com.storerecommender.webapp.schemas;

/** Bundles the full catalog and parsed grocery list as input to the fuzzy filter service. */
public record InputsForFuzzyMatching(
        ProductInventory inventory,
        ParsedGroceryList groceryList
) {
}
