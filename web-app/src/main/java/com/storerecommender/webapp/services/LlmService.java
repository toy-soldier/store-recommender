package com.storerecommender.webapp.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.storerecommender.webapp.clients.LlmClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public abstract class LlmService implements ResourceLoaderAware {
    private final LlmClient llmClient;
    private final String apiKey;
    private final String modelName;
    private final String systemPrompt;
    private final String dummyResponsesPath;
    private final ObjectMapper objectMapper;
    private ResourceLoader resourceLoader;

    protected LlmService(LlmClient llmClient, String apiKey, String modelName,
                         Resource promptResource, String dummyResponsesPath, ObjectMapper objectMapper) throws IOException {
        this.llmClient = llmClient;
        this.apiKey = apiKey;
        this.modelName = modelName;
        this.systemPrompt = promptResource.getContentAsString(StandardCharsets.UTF_8);
        this.dummyResponsesPath = dummyResponsesPath;
        this.objectMapper = objectMapper;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    protected <T> T processRequest(String filename, String content, Class<T> responseType) {
        if (apiKey.equals("dummy")) {
            return readDummyResponse(filename, responseType);
        }
        log.debug("Calling LLM {}...", modelName);
        String finalPrompt = systemPrompt + "\n\n" + content;
        T response = llmClient.call(finalPrompt, modelName, responseType);
        log.debug("Processing done");
        return response;
    }

    private <T> T readDummyResponse(String filename, Class<T> responseType) {
        String stem = filename.contains(".") ? filename.substring(0, filename.lastIndexOf(".")) : filename;
        String path = dummyResponsesPath + stem + ".json";
        log.debug("Responding with dummy response: {}", path);
        try {
            Resource resource = resourceLoader.getResource(path);
            return objectMapper.readValue(resource.getInputStream(), responseType);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load dummy response: " + path, e);
        }
    }
}
