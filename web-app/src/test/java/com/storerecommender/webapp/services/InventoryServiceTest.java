package com.storerecommender.webapp.services;

import com.storerecommender.webapp.clients.ApiServerClient;
import com.storerecommender.webapp.schemas.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {
    @Mock
    private ApiServerClient apiServerClient;
    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void generateFinalRecommendationsReturnsAllSuggestions() {
        var s1 = new LlmSuggestion(1001, "description-unused",94);
        var s2 = new LlmSuggestion(1002, "description-unused", 98);
        var s3 = new LlmSuggestion(1250, "description-unused",85);
        var lineItem1 = new LlmSuggestionsPerGroceryListLineItem("milk", List.of(s1, s2));
        var lineItem2 = new LlmSuggestionsPerGroceryListLineItem("sugar", List.of(s3));
        var finalSuggestions = new LlmFinalSuggestions();
        finalSuggestions.setList(List.of(lineItem1, lineItem2));

        var es1 = EnrichedSuggestion.builder().sku(1001).name("yyy").confidence(94).build();
        var es2 = EnrichedSuggestion.builder().sku(1002).name("zzz").confidence(98).build();
        var es3 = EnrichedSuggestion.builder().sku(1250).name("aaa").confidence(85).build();

        when(apiServerClient.getProduct(1001)).thenReturn(es1);
        when(apiServerClient.getProduct(1002)).thenReturn(es2);
        when(apiServerClient.getProduct(1250)).thenReturn(es3);

        FinalRecommendations recommendations = inventoryService.generateFinalRecommendations(finalSuggestions);

        assertEquals("milk", recommendations.getList().get(0).query());
        assertEquals(List.of(es1, es2), recommendations.getList().get(0).list());
        assertEquals("sugar", recommendations.getList().get(1).query());
        assertEquals(List.of(es3), recommendations.getList().get(1).list());
    }

    @Test
    void generateFinalRecommendationsGivenEnrichmentFailuresReturnsOnlyCompleteSuggestions() {
        var s1 = new LlmSuggestion(1001, "description-unused",94);
        var s2 = new LlmSuggestion(1002, "description-unused", 98);
        var s3 = new LlmSuggestion(1250, "description-unused",85);
        var lineItem1 = new LlmSuggestionsPerGroceryListLineItem("milk", List.of(s1, s2));
        var lineItem2 = new LlmSuggestionsPerGroceryListLineItem("sugar", List.of(s3));
        var finalSuggestions = new LlmFinalSuggestions();
        finalSuggestions.setList(List.of(lineItem1, lineItem2));

        var es2 = EnrichedSuggestion.builder().sku(1002).name("zzz").confidence(98).build();

        when(apiServerClient.getProduct(1001)).thenThrow(new RuntimeException());
        when(apiServerClient.getProduct(1002)).thenReturn(es2);
        when(apiServerClient.getProduct(1250)).thenThrow(new RuntimeException());

        FinalRecommendations recommendations = inventoryService.generateFinalRecommendations(finalSuggestions);

        assertEquals("milk", recommendations.getList().get(0).query());
        assertEquals(List.of(es2), recommendations.getList().get(0).list());
        assertEquals("sugar", recommendations.getList().get(1).query());
        assertEquals(List.of(), recommendations.getList().get(1).list());
    }

    @Test
    void getInventoryFromApiServerRetrievesFullInventory() {
        var p1 = new ProductInventoryLineItem(10, "Brand product 10");
        var p2 = new ProductInventoryLineItem(20, "Brand product 20");
        var p3 = new ProductInventoryLineItem(30, "Brand product 30");
        var p4 = new ProductInventoryLineItem(40, "Brand product 40");
        var p5 = new ProductInventoryLineItem(50, "Brand product 50");
        var page1 = new CatalogPage(List.of(p1, p2), 3);
        var page2 = new CatalogPage(List.of(p3, p4), 3);
        var page3 = new CatalogPage(List.of(p5), 3);

        when(apiServerClient.getCatalogPage(1)).thenReturn(page1);
        when(apiServerClient.getCatalogPage(2)).thenReturn(page2);
        when(apiServerClient.getCatalogPage(3)).thenReturn(page3);

        inventoryService.getInventoryFromApiServer();

        assertEquals(List.of(p1, p2, p3, p4, p5),
                inventoryService.getCachedInventory().getInventory());
    }

    @Test
    void getInventoryFromApiServerClearsInventoryWhenAnyPageFails() {
        var p1 = new ProductInventoryLineItem(10, "Brand product 10");
        var p2 = new ProductInventoryLineItem(20, "Brand product 20");
        var p3 = new ProductInventoryLineItem(30, "Brand product 30");
        var page1 = new CatalogPage(List.of(p1), 10);
        var page2 = new CatalogPage(List.of(p2), 10);
        var page3 = new CatalogPage(List.of(p3), 10);

        when(apiServerClient.getCatalogPage(1)).thenReturn(page1);
        when(apiServerClient.getCatalogPage(2)).thenReturn(page2);
        when(apiServerClient.getCatalogPage(3)).thenReturn(page3);
        when(apiServerClient.getCatalogPage(4)).thenThrow(new RuntimeException());

        inventoryService.getInventoryFromApiServer();

        assertEquals(List.of(), inventoryService.getCachedInventory().getInventory());
    }
}