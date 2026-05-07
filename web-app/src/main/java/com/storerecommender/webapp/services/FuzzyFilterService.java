package com.storerecommender.webapp.services;

import com.storerecommender.webapp.schemas.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FuzzyFilterService {

    public FuzzyFilterService() {
        log.info("Fuzzy Filter Service created");
    }

    public PrunedInventory pruneInventory(InputsForFuzzyMatching inputs) {
        log.info("Pruning inventory based on grocery list");
        ProductInventory productInventory = inputs.inventory();
        var pruned = new PrunedInventory();
        for (ParsedGroceryListLineItem lineItem: inputs.groceryList().getList()) {
            var result = new PrunedInventoryBasedOnGroceryListLineItem(productInventory, lineItem);
            pruned.getCandidates().add(result);

        }
        log.info("Pruning finished");
        return pruned;
    }
}
