package com.storerecommender.webapp.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/** One grocery query and its LLM-suggested products, before api-server enrichment. */
public record LlmSuggestionsPerGroceryListLineItem(
        @JsonProperty(required = true) String query,
        @JsonProperty(required = true) List<LlmSuggestion> list
) {}
