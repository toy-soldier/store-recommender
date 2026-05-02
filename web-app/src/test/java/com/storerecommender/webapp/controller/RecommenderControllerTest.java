package com.storerecommender.webapp.controller;

import com.storerecommender.webapp.schemas.FinalRecommendations;
import com.storerecommender.webapp.services.AgentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

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
        var content = new String(bytes, StandardCharsets.UTF_8).trim();

        var recommendations = FinalRecommendations.builder()
                .filename(filename)
                .content(content)
                .build();
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
}
