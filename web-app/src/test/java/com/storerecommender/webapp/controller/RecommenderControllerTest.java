package com.storerecommender.webapp.controller;

import com.storerecommender.webapp.schemas.*;
import com.storerecommender.webapp.services.AgentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RecommenderController.class)
class RecommenderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AgentService agentService;

    @Test
    void getUploadPageReturnsUploadView() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("upload"));
    }

    @Test
    void postRecommenderWithValidFileReturnsRecommendationsView() throws Exception {
        var filename = "list.txt";
        var bytes = new ClassPathResource("test_files/" + filename).getInputStream().readAllBytes();

        var recommendations = new FinalRecommendations(null);
        when(agentService.getFinalRecommendations(eq(filename), anyString()))
                .thenReturn(recommendations);

        var file = new MockMultipartFile("file", filename, "text/plain", bytes);

        mockMvc.perform(multipart("/recommender").file(file))
                .andExpect(status().isOk())
                .andExpect(view().name("recommendations"))
                .andExpect(model().attributeExists("content"));
    }

    @Test
    void postRecommenderWithEmptyFileReturnsUploadViewWithMessage() throws Exception {
        var resource = new ClassPathResource("test_files/empty.txt");
        var file = new MockMultipartFile("file", "empty.txt", "text/plain", resource.getInputStream());

        mockMvc.perform(multipart("/recommender").file(file))
                .andExpect(status().isOk())
                .andExpect(view().name("upload"))
                .andExpect(model().attribute("message", "Please select a non-empty file."));
    }

    @Test
    void postRecommenderWithWhitespaceOnlyFileReturnsUploadViewWithMessage() throws Exception {
        var resource = new ClassPathResource("test_files/file_with_spaces_and_newlines_only.txt");
        var file = new MockMultipartFile("file", "file_with_spaces_and_newlines_only.txt", "text/plain", resource.getInputStream());

        mockMvc.perform(multipart("/recommender").file(file))
                .andExpect(status().isOk())
                .andExpect(view().name("upload"))
                .andExpect(model().attribute("message", "Please select a non-empty file."));
    }

    @Test
    void postRecommenderRendersRecommendationsCorrectly() throws Exception {
        var filename = "list.txt";
        var bytes = new ClassPathResource("test_files/" + filename).getInputStream().readAllBytes();

        var highConf = EnrichedSuggestion.builder().sku(1001).brand("Brand A").name("Whole Milk")
                .category("Dairy").price(new BigDecimal("3.99")).stock(5).confidence(90).build();
        var midConf = EnrichedSuggestion.builder().sku(1002).brand("Brand B").name("Semi-Skimmed Milk")
                .category("Dairy").price(new BigDecimal("2.99")).stock(3).confidence(70).build();
        var lowConf = EnrichedSuggestion.builder().sku(1003).brand("Brand C").name("Oat Milk")
                .category("Dairy").price(new BigDecimal("4.49")).stock(2).confidence(50).build();

        var lineItem = new EnrichedSuggestionsPerGroceryListLineItem("milk", List.of(highConf, midConf, lowConf));
        var recommendations = new FinalRecommendations(List.of(lineItem));

        when(agentService.getFinalRecommendations(eq(filename), anyString()))
                .thenReturn(recommendations);

        var file = new MockMultipartFile("file", filename, "text/plain", bytes);

        mockMvc.perform(multipart("/recommender").file(file))
                .andExpect(status().isOk())
                .andExpect(view().name("recommendations"))
                .andExpect(content().string(containsString("For your requirement `milk`...")))
                .andExpect(content().string(containsString("Highly recommended!")))
                .andExpect(content().string(containsString("Recommended")))
                .andExpect(content().string(containsString("You may also like...")))
                .andExpect(content().string(containsString("Brand A Whole Milk (at $3.99 per unit)")))
                .andExpect(content().string(containsString("Brand B Semi-Skimmed Milk (at $2.99 per unit)")))
                .andExpect(content().string(containsString("Brand C Oat Milk (at $4.49 per unit)")));
    }
}
