package com.storerecommender.webapp.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.storerecommender.webapp.schemas.CatalogPage;
import com.storerecommender.webapp.schemas.EnrichedSuggestion;
import com.storerecommender.webapp.schemas.ProductInventoryLineItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(ApiServerClient.class)
class ApiServerClientTest {

    private static final String BASE_URL = "http://localhost:8081";

    @Autowired private ApiServerClient apiServerClient;
    @Autowired private MockRestServiceServer server;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void getCatalogPageReturnsCatalogPage() throws Exception {
        var body = new CatalogPage(
                List.of(new ProductInventoryLineItem(1001, "Brand Name")), 4);

        server.expect(requestTo(BASE_URL + "/api/v1/products?page=1&size=100"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(body),
                        MediaType.APPLICATION_JSON));

        assertEquals(body, apiServerClient.getCatalogPage(1));
    }

    @Test
    void getProductReturnsEnrichedSuggestionWithNullConfidence() throws Exception {
        var suggestion = new EnrichedSuggestion(
                1001, "Name", "Brand", "Category",
                new BigDecimal("2.99"), 50, null);

        server.expect(requestTo(BASE_URL + "/api/v1/products/1001"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(Map.of("content", suggestion)),
                        MediaType.APPLICATION_JSON));

        assertEquals(suggestion, apiServerClient.getProduct(1001));
    }
}
