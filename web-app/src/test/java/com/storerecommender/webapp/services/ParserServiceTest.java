package com.storerecommender.webapp.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.storerecommender.webapp.clients.LlmClient;
import com.storerecommender.webapp.schemas.ParsedGroceryList;
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
class ParserServiceTest {
    @Mock
    private LlmClient llmClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ClassPathResource resource = new ClassPathResource("prompts/parser_prompt.txt");
    private final ResourceLoader resourceLoader = new DefaultResourceLoader();
    private final String model = "parser-model";
    private final String dummyResponsesPath = "dummy/parser-responses/";

    private ParserService createWithApiKey(String apiKey) throws IOException {
        var parserService = new ParserService(llmClient, apiKey, model,
                resource, dummyResponsesPath, objectMapper);
        parserService.setResourceLoader(resourceLoader);
        return parserService;
    }

    @Test
    void parseListGivenServiceWithRealKeyReturnsLlmResponse() throws IOException {
        var parsedGroceryList = new ParsedGroceryList();
        ParserService parserService = createWithApiKey("this-is-a-real-api-key");

        when(llmClient.call(anyString(), anyString(), any())).thenReturn(parsedGroceryList);

        ParsedGroceryList result = parserService.parseList("list01.txt", "test");

        assertEquals(parsedGroceryList, result);
    }

    @Test
    void parseListGivenServiceWithDummyKeyReturnsDummyResponse() throws IOException {
        ParserService parserService = createWithApiKey("dummy");

        ParsedGroceryList result = parserService.parseList("list01.txt", "test");

        assertEquals("3 packs of milk", result.getList().get(0).query());
        assertEquals("1 bag of sugar", result.getList().get(1).query());
    }

    @Test
    void parseListGivenServiceWithDummyKeyAndNonExistentResponseThrowsException() throws IOException {
        ParserService parserService = createWithApiKey("dummy");

        assertThrows(IllegalStateException.class, () -> parserService.parseList(
                "non-existent.txt", "test"));
    }
}