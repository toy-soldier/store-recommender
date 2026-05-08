package com.storerecommender.webapp.clients;

import com.storerecommender.webapp.schemas.LlmFinalSuggestions;
import com.storerecommender.webapp.schemas.ParsedGroceryList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LlmClientTest {
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChatClient chatClient;
    @InjectMocks
    private LlmClient llmClient;

    @Test
    void callGivenParsingRequestReturnsParsedGroceryList() {
        var parsedGroceryList = new ParsedGroceryList();

        when(chatClient.prompt()
                .options(any())
                .user(anyString())
                .call()
                .entity(ParsedGroceryList.class)
        ).thenReturn(parsedGroceryList);

        ParsedGroceryList result = llmClient.call("parse the grocery list",
                "parser", ParsedGroceryList.class);

        assertEquals(parsedGroceryList, result);
    }

    @Test
    void callGivenRecommendationRequestReturnsLlmSuggestions() {
        var llmFinalSuggestions = new LlmFinalSuggestions();

        when(chatClient.prompt()
                .options(any())
                .user(anyString())
                .call()
                .entity(LlmFinalSuggestions.class)
        ).thenReturn(llmFinalSuggestions);

        LlmFinalSuggestions result = llmClient.call("recommend products",
                "recommender", LlmFinalSuggestions.class);

        assertEquals(llmFinalSuggestions, result);
    }
}