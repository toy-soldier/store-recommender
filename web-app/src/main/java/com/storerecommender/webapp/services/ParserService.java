package com.storerecommender.webapp.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.storerecommender.webapp.clients.LlmClient;
import com.storerecommender.webapp.schemas.ParsedGroceryList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class ParserService extends LlmService {

    public ParserService(LlmClient llmClient,
                         @Value("${spring.ai.openai.api-key}") String apiKey,
                         @Value("${app.models.parser}") String modelName,
                         @Value("${app.prompts.parser}") Resource promptResource,
                         @Value("${app.dummy-mode.parser-responses}") String dummyResponsesPath,
                         ObjectMapper objectMapper) throws IOException {
        super(llmClient, apiKey, modelName, promptResource, dummyResponsesPath, objectMapper);
        log.info("ParserService created");
    }

    public ParsedGroceryList parseList(String filename, String content) {
        log.info("Parsing {}...", filename);
        ParsedGroceryList parsedGroceryList = processRequest(filename, content, ParsedGroceryList.class);
        log.info("Parsed {} grocery list lines", parsedGroceryList.getList().size());
        return parsedGroceryList;
    }
}
