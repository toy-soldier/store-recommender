package com.storerecommender.webapp.schemas;

import java.util.List;

/** One grocery query and its enriched suggestions. */
public record EnrichedSuggestionsPerGroceryListLineItem(
        String query,
        List<EnrichedSuggestion> list
) {}
