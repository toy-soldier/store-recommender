package com.storerecommender.webapp.services;

import com.storerecommender.webapp.schemas.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgentServiceTest {
    @Mock
    private InventoryService inventoryService;
    @Mock
    private ParserService parserService;
    @Mock
    private FuzzyFilterService fuzzyFilterService;
    @Mock
    private RecommenderService recommenderService;
    @InjectMocks
    private AgentService agentService;

    @Test
    void getFinalRecommendationsReturnsResultOfChain() {
        String f = "filename";
        String c = "content";
        var inventory = new ProductInventory();
        var pgl = new ParsedGroceryList();
        var inputs = new InputsForFuzzyMatching(inventory, pgl);
        var pi = new PrunedInventory();
        var llm = new LlmFinalSuggestions();
        var fr = new FinalRecommendations(null);

        when(inventoryService.getCachedInventory()).thenReturn(inventory);
        when(parserService.parseList(f, c)).thenReturn(pgl);
        when(fuzzyFilterService.pruneInventory(inputs)).thenReturn(pi);
        when(recommenderService.recommendProducts(f, pi)).thenReturn(llm);
        when(inventoryService.generateFinalRecommendations(llm)).thenReturn(fr);

        assertEquals(fr, agentService.getFinalRecommendations(f, c));
    }
}