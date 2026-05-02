package com.storerecommender.webapp.schemas;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FinalRecommendations {
    private String filename;
    private String content;
}
