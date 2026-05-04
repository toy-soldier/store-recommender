package com.storerecommender.webapp.schemas;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/** Structured output from the recommender LLM — all queries and their suggestions. */
@Data
public class LLMFinalSuggestions {
    private List<LLMSuggestionsPerGroceryListLineItem> list = new ArrayList<>();
}
