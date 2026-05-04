package com.storerecommender.webapp.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;

/** A single line item parsed from the user's grocery list by the parser LLM. */
public record ParsedGroceryListLineItem(
        @JsonProperty(required = true) String query,
        @JsonProperty(required = true) String product,
        @JsonProperty(required = true) Double quantity,
        @JsonProperty(required = true) String unit
) {}
