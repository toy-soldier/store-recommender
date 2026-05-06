package com.storerecommender.webapp.schemas;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/** The agent's final recommendations for the user.*/
@AllArgsConstructor
@Getter
public class FinalRecommendations {
    private List<EnrichedSuggestionsPerGroceryListLineItem> list;
}
