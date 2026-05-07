package com.storerecommender.webapp.services;

import com.storerecommender.webapp.schemas.LlmFinalSuggestions;
import com.storerecommender.webapp.schemas.LlmSuggestion;
import com.storerecommender.webapp.schemas.LlmSuggestionsPerGroceryListLineItem;
import com.storerecommender.webapp.schemas.PrunedInventory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class RecommenderService extends LlmService {

    public RecommenderService() {
        super();
        log.info("Recommender service created");
    }

    public LlmFinalSuggestions recommendProducts(String filename, PrunedInventory pruned) {
        LlmSuggestion s1 = new LlmSuggestion(1001, "yyy",94);
        LlmSuggestion s2 = new LlmSuggestion(1002, "zzz", 98);
        LlmSuggestion s3 = new LlmSuggestion(1250, "aaa",85);
        LlmSuggestionsPerGroceryListLineItem perGroceryListLineItem1 = new LlmSuggestionsPerGroceryListLineItem(
                "milk", List.of(s1, s2));
        LlmSuggestionsPerGroceryListLineItem perGroceryListLineItem2 = new LlmSuggestionsPerGroceryListLineItem(
                "milk", List.of(s3));
        LlmFinalSuggestions finalSuggestions = new LlmFinalSuggestions();
        finalSuggestions.setList(List.of(perGroceryListLineItem1, perGroceryListLineItem2));
        return finalSuggestions;
    }
}
