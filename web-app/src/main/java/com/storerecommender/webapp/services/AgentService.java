package com.storerecommender.webapp.services;

import com.storerecommender.webapp.schemas.FinalRecommendations;
import org.springframework.stereotype.Service;

@Service
public class AgentService {
    public FinalRecommendations getFinalRecommendations(String filename, String content) {
        var recommendations = new FinalRecommendations();
        recommendations.setFilename(filename);
        recommendations.setContent(content);
        return recommendations;
    }
}
