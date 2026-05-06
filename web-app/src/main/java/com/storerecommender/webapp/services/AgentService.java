package com.storerecommender.webapp.services;

import com.storerecommender.webapp.schemas.FinalRecommendations;
import com.storerecommender.webapp.schemas.LlmFinalSuggestions;
import com.storerecommender.webapp.schemas.LlmSuggestion;
import com.storerecommender.webapp.schemas.LlmSuggestionsPerGroceryListLineItem;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class AgentService {
    private final InventoryService inventoryService;

    public FinalRecommendations getFinalRecommendations(String filename, String content) {
        LlmSuggestion s1 = new LlmSuggestion(1001, "yyy",94);
        LlmSuggestion s2 = new LlmSuggestion(1002, "zzz", 98);
        LlmSuggestionsPerGroceryListLineItem perGroceryListLineItem = new LlmSuggestionsPerGroceryListLineItem(
                "milk", List.of(s1, s2));
        LlmFinalSuggestions finalSuggestions = new LlmFinalSuggestions();
        finalSuggestions.setList(List.of(perGroceryListLineItem));
        var recommendations = inventoryService.generateFinalRecommendations(finalSuggestions);
        return recommendations;
    }
}
