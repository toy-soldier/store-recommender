package com.storerecommender.webapp.controller;

import com.storerecommender.webapp.services.AgentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Controller
@AllArgsConstructor
public class RecommenderController {

    private final AgentService agentService;

    @GetMapping("/")
    public String uploadPage() {
        return "upload";
    }

    @PostMapping("/recommender")
    public String recommender(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        String content = new String(file.getBytes(), StandardCharsets.UTF_8).trim();
        if (content.isEmpty()) {
            model.addAttribute("message", "Please select a non-empty file.");
            return uploadPage();
        }

        model.addAttribute("content",
                agentService.getFinalRecommendations(file.getOriginalFilename(), content));
        return "recommendations";
    }
}
