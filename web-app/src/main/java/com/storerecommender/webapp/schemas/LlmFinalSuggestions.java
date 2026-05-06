package com.storerecommender.webapp.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/** Structured output from the recommender LLM — all queries and their suggestions. */
@Data
public class LlmFinalSuggestions {
    @JsonProperty private List<LlmSuggestionsPerGroceryListLineItem> list = new ArrayList<>();
}
