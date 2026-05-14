package com.storerecommender.webapp.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.storerecommender.webapp.clients.LlmClient;
import com.storerecommender.webapp.schemas.LlmFinalSuggestions;
import com.storerecommender.webapp.schemas.PrunedInventory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommenderServiceTest {
    @Mock
    private LlmClient llmClient;
    @Mock
    private ObjectMapper mockMapper;
    private final ObjectMapper realObjectMapper = new ObjectMapper();
    private final ClassPathResource resource = new ClassPathResource(
            "prompts/recommender_prompt.txt");
    private final ResourceLoader resourceLoader = new DefaultResourceLoader();
    private final String model = "recommender-model";
    private final String dummyResponsesPath = "dummy/recommender-responses/";

    private RecommenderService createWithApiKey(String apiKey, ObjectMapper objectMapper) throws IOException {
        var recommenderService = new RecommenderService(llmClient, apiKey, model,
                resource, dummyResponsesPath, objectMapper);
        recommenderService.setResourceLoader(resourceLoader);
        return recommenderService;
    }

    @Test
    void recommendProductsGivenServiceWithRealApiKeyReturnsLlmResponse() throws IOException {
        var prunedInventory = new PrunedInventory();
        var finalSuggestions = new LlmFinalSuggestions();
        RecommenderService recommenderService = createWithApiKey("this-is-a-real-api-key", realObjectMapper);

        when(llmClient.call(anyString(), anyString(), any())).thenReturn(finalSuggestions);

        LlmFinalSuggestions result = recommenderService.recommendProducts(
                "list01.txt", prunedInventory);

        assertEquals(finalSuggestions, result);
    }

    @Test
    void recommendProductsGivenServiceWithDummyApiKeyReturnsDummyResponse() throws IOException {
        var prunedInventory = new PrunedInventory();
        RecommenderService recommenderService = createWithApiKey("dummy", realObjectMapper);

        LlmFinalSuggestions result = recommenderService.recommendProducts(
                "list01.txt", prunedInventory);

        assertEquals("3 packs of milk", result.getList().get(0).query());
        assertEquals("1 bag of sugar", result.getList().get(1).query());
    }

    @Test
    void recommendProductsGivenServiceWithDummyKeyAndNonExistentResponseThrowsException() throws IOException {
        var prunedInventory = new PrunedInventory();
        RecommenderService recommenderService = createWithApiKey("dummy", realObjectMapper);

        assertThrows(IllegalStateException.class, () -> recommenderService.recommendProducts(
                "non-existent.txt", prunedInventory));
    }

    @Test
    void recommendProductsGivenUnserializablePrunedInventoryThrowsException() throws IOException {
        var invalid = new PrunedInventory();
        RecommenderService recommenderService = createWithApiKey("this-is-a-real-api-key", mockMapper);

        when(mockMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);

        assertThrows(IllegalStateException.class, () -> recommenderService.recommendProducts(
                "list01.txt", invalid));
    }
}