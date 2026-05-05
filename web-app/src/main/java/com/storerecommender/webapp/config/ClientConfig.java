package com.storerecommender.webapp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ClientConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        log.debug("Creating OpenAI chat client bean");
        return builder.build();
    }
}
