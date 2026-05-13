package com.storerecommender.webapp.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.storerecommender.webapp.clients.LlmClient;
import com.storerecommender.webapp.schemas.LlmFinalSuggestions;
import com.storerecommender.webapp.schemas.PrunedInventory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class RecommenderService extends LlmService {

    private final ObjectMapper objectMapper;

    public RecommenderService(LlmClient llmClient,
                              @Value("${spring.ai.openai.api-key}") String apiKey,
                              @Value("${app.models.recommender}") String modelName,
                              @Value("${app.prompts.recommender}") Resource promptResource,
                              @Value("${app.dummy-mode.recommender-responses}") String dummyResponsesPath,
                              ObjectMapper objectMapper) throws IOException {
        super(llmClient, apiKey, modelName, promptResource, dummyResponsesPath, objectMapper);
        this.objectMapper = objectMapper;
        log.info("RecommenderService created");
    }

    public LlmFinalSuggestions recommendProducts(String filename, PrunedInventory pruned) {
        log.info("Recommending products...");
        try {
            String prunedJson = objectMapper.writeValueAsString(pruned);
            LlmFinalSuggestions suggestions = processRequest(filename, prunedJson, LlmFinalSuggestions.class);
            log.info("Recommending products done");
            return suggestions;
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize PrunedInventory", e);
        }
    }
}
