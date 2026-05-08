package com.storerecommender.webapp.services;

import com.storerecommender.webapp.schemas.*;
import lombok.extern.slf4j.Slf4j;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FuzzyFilterService {
    private final int topN;
    private final int minScore;

    public FuzzyFilterService(@Value("${app.fuzzy.top-n}") int topN,
                              @Value("${app.fuzzy.min-score}") int minScore) {
        this.topN = topN;
        this.minScore = minScore;
        log.info("Fuzzy Filter Service created");
    }

    public PrunedInventory pruneInventory(InputsForFuzzyMatching inputs) {
        log.info("Pruning inventory based on grocery list");
        List<ProductInventoryLineItem> productList = inputs.inventory().getInventory();
        List<String> productDescriptions = productList.stream()
                .map(ProductInventoryLineItem::description)
                .toList();

        var pruned = new PrunedInventory();
        for (ParsedGroceryListLineItem lineItem : inputs.groceryList().getList()) {
            ProductInventory smallerInventory = pruneList(lineItem.product(), productList, productDescriptions);
            pruned.getCandidates().add(new PrunedInventoryBasedOnGroceryListLineItem(smallerInventory, lineItem));
        }
        log.info("Pruning finished");
        return pruned;
    }

    private ProductInventory pruneList(String needed, List<ProductInventoryLineItem> productList, List<String> productDescriptions) {
        List<Integer> indexes = FuzzySearch.extractSorted(needed, productDescriptions).stream()
                .filter(r -> r.getScore() >= minScore)
                .limit(topN)
                .map(ExtractedResult::getIndex)
                .toList();

        var focused = new ProductInventory();
        for (Integer index : indexes)
            focused.getInventory().add(productList.get(index));
        return focused;
    }
}
