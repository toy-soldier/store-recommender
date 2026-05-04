package com.storerecommender.webapp.schemas;

import lombok.Data;

import java.util.List;

/** The agent's final recommendations for the user.*/
@Data
public class FinalRecommendations {
    private String filename;
    private String content;
    private List<EnrichedSuggestionsPerGroceryListLineItem> list;
}
