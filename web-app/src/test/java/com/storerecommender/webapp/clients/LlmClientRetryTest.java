package com.storerecommender.webapp.clients;

import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class LlmClientRetryTest {
    @MockitoBean(answers = Answers.RETURNS_DEEP_STUBS)
    private ChatClient chatClient;

    @Autowired
    private LlmClient llmClient;

    @Value("${retry.openai.max-attempts}")
    private int maxAttempts;

    @Test
    void callSucceedsAfterRetry() {
        when(chatClient.prompt()
                .options(any())
                .user(anyString())
                .call()
                .entity(String.class))
                .thenThrow(RuntimeException.class)
                .thenReturn("ok");

        String result = llmClient.call(
                "health check", "ping-model", String.class
        );

        assertEquals("ok", result);
    }

    @Test
    void callThrowsAfterMaxAttempts() {
        when(chatClient.prompt()
                .options(any())
                .user(anyString())
                .call()
                .entity(String.class))
                .thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> llmClient.call(
                "health check", "ping-model", String.class
        ));
        verify(chatClient.prompt()
                .options(any())
                .user(anyString())
                .call(), times(maxAttempts)).entity(String.class);
    }
}
