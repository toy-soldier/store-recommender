package com.storerecommender.webapp.clients;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LlmClient {
    private final ChatClient chatClient;

    public LlmClient(ChatClient chatClient) {
        this.chatClient = chatClient;
        log.info("LLM client created");
    }

    @Retryable(
            maxAttemptsExpression = "${retry.openai.max-attempts}",
            backoff = @Backoff(
                    delayExpression = "${retry.openai.backoff.delay-ms}",
                    multiplierExpression = "${retry.openai.backoff.multiplier}"
            )
    )
    public <T> T call(String prompt, String model, Class<T> responseType) {
        log.debug("Calling LLM with model {}", model);
        return chatClient.prompt()
                .options(OpenAiChatOptions.builder().model(model).temperature(0.0).build())
                .user(prompt)
                .call()
                .entity(responseType);
    }
}
