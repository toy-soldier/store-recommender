package com.storerecommender.webapp.services;

import com.storerecommender.webapp.schemas.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class AgentService {
    private final InventoryService inventoryService;
    private final FuzzyFilterService fuzzyFilterService;
    private final ParserService parserService;
    private final RecommenderService recommenderService;

    public FinalRecommendations getFinalRecommendations(String filename, String content) {
        ParsedGroceryList parsedList = parserService.parseList(filename, content);
        InputsForFuzzyMatching inputs = new InputsForFuzzyMatching(
                inventoryService.getCachedInventory(), parsedList);
        PrunedInventory pruned = fuzzyFilterService.pruneInventory(inputs);
        LlmFinalSuggestions finalSuggestions = recommenderService.recommendProducts(filename, pruned);
        return inventoryService.generateFinalRecommendations(finalSuggestions);
    }
}
