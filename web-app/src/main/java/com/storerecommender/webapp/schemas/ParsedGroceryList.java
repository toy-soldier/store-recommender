package com.storerecommender.webapp.schemas;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/** Structured output from the parser LLM — the full parsed grocery list. */
@Data
public class ParsedGroceryList {
    private List<ParsedGroceryListLineItem> list = new ArrayList<>();
}
