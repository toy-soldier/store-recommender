package com.storerecommender.webapp.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.storerecommender.webapp.helpers.RetriesConfig;
import com.storerecommender.webapp.schemas.CatalogPage;
import com.storerecommender.webapp.schemas.EnrichedSuggestion;
import com.storerecommender.webapp.schemas.ProductInventoryLineItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.ExpectedCount.times;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(ApiServerClient.class)
@Import(RetriesConfig.class)
class ApiServerClientRetryTest {

    private static final String BASE_URL = "http://localhost:8081";
    private static final String CATALOG_URL = BASE_URL + "/api/v1/products?page=1&size=100";
    private static final String PRODUCT_URL = BASE_URL + "/api/v1/products/1001";

    @Autowired private ApiServerClient apiServerClient;
    @Autowired private MockRestServiceServer server;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void getCatalogPageSucceedsAfterRetry() throws Exception {
        var body = new CatalogPage(List.of(new ProductInventoryLineItem(1001, "Brand Name")), 1);

        server.expect(requestTo(CATALOG_URL)).andRespond(withServerError());
        server.expect(requestTo(CATALOG_URL)).andRespond(
                withSuccess(objectMapper.writeValueAsString(body), MediaType.APPLICATION_JSON));

        assertEquals(body, apiServerClient.getCatalogPage(1));
        server.verify();
    }

    @Test
    void getCatalogPageThrowsAfterMaxAttempts() {
        server.expect(times(3), requestTo(CATALOG_URL)).andRespond(withServerError());

        assertThrows(RestClientException.class, () -> apiServerClient.getCatalogPage(1));
        server.verify();
    }

    @Test
    void getProductSucceedsAfterRetry() throws Exception {
        var suggestion = EnrichedSuggestion.builder()
                .sku(1001).name("Name").brand("Brand").category("Category")
                .price(BigDecimal.valueOf(2.99)).stock(50).build();

        server.expect(requestTo(PRODUCT_URL)).andRespond(withServerError());
        server.expect(requestTo(PRODUCT_URL)).andRespond(
                withSuccess(objectMapper.writeValueAsString(Map.of("content", suggestion)),
                        MediaType.APPLICATION_JSON));

        assertEquals(suggestion, apiServerClient.getProduct(1001));
        server.verify();
    }

    @Test
    void getProductThrowsAfterMaxAttempts() {
        server.expect(times(3), requestTo(PRODUCT_URL)).andRespond(withServerError());

        assertThrows(RestClientException.class, () -> apiServerClient.getProduct(1001));
        server.verify();
    }
}
