package com.storerecommender.webapp.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;

/** A single product suggestion returned by the recommender LLM, before api-server enrichment. */
public record LLMSuggestion(
        @JsonProperty(required = true) Integer sku,
        @JsonProperty(required = true) String description,
        @JsonProperty(required = true) Integer confidence
) {}
