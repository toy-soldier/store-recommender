package com.storerecommender.webapp.services;

import com.storerecommender.webapp.clients.ApiServerClient;
import com.storerecommender.webapp.schemas.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class InventoryService {
    private final ApiServerClient apiServerClient;
    private final ProductInventory productInventory;

    public InventoryService(ApiServerClient apiServerClient) {
        this.apiServerClient = apiServerClient;
        this.productInventory = new ProductInventory();
        log.info("Inventory service initialized");
    }

    public ProductInventory getCachedInventory() {
        return productInventory;
    }

    public FinalRecommendations generateFinalRecommendations(LlmFinalSuggestions finalSuggestions) {
        log.info("Generating final recommendations...");
        List<EnrichedSuggestionsPerGroceryListLineItem> finalList = new ArrayList<>();
        for (LlmSuggestionsPerGroceryListLineItem lineItem : finalSuggestions.getList()) {
            String query = lineItem.query();
            List<EnrichedSuggestion> enrichedSuggestions = new ArrayList<>();
            for (LlmSuggestion suggestion : lineItem.list())
                try {
                    enrichedSuggestions.add(enrich(suggestion));
                } catch (Exception e) {
                    log.error("Error enriching suggestion for SKU {}; skipping", suggestion.sku(),e);
                }
            finalList.add(new EnrichedSuggestionsPerGroceryListLineItem(query, enrichedSuggestions));
        }
        log.info("Generating final recommendations done");
        return new FinalRecommendations(finalList);
    }

    public void getInventoryFromApiServer() {
        log.info("Getting inventory from API server");
        try {
            productInventory.getInventory().clear();
            getCatalogPages();
        } catch (Exception e) {
            log.error("Exception while getting inventory from API server", e);
            log.error("Clearing product inventory");
            productInventory.getInventory().clear();
        }
        log.info("Getting inventory from API server done");
    }

    private void getCatalogPages() {
        CatalogPage catalogPage;
        int currentPage = 1;
        int totalPages;

        do {
            catalogPage = apiServerClient.getCatalogPage(currentPage);
            totalPages = catalogPage.totalPages();
            productInventory.getInventory()
                    .addAll(catalogPage.content());
            currentPage++;
        } while (currentPage <= totalPages);
    }

    private EnrichedSuggestion enrich(LlmSuggestion suggestion) {
        EnrichedSuggestion enrichedSuggestion = apiServerClient.getProduct(suggestion.sku());
        enrichedSuggestion.setConfidence(suggestion.confidence());
        return enrichedSuggestion;
    }
}
