package com.storerecommender.webapp.schemas;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/** Fuzzy-filtered catalog grouped by grocery list line item — input to the recommender LLM. */
@Data
public class PrunedInventory {
    private List<PrunedInventoryBasedOnGroceryListLineItem> candidates = new ArrayList<>();
}
