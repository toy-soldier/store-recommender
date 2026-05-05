package com.storerecommender.webapp.clients;

import com.storerecommender.webapp.schemas.CatalogPage;
import com.storerecommender.webapp.schemas.EnrichedSuggestion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Component
public class ApiServerClient {

    private final RestClient restClient;

    public ApiServerClient(RestClient.Builder builder,
                           @Value("${app.api-server.base-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    @Retryable(
            maxAttemptsExpression = "${retry.api-server.max-attempts}",
            backoff = @Backoff(
                    delayExpression = "${retry.api-server.backoff.delay-ms}",
                    multiplierExpression = "${retry.api-server.backoff.multiplier}"
            )
    )
    public CatalogPage getCatalogPage(int page) {
        log.debug("Retrieving catalog page {}", page);
        return restClient.get()
                .uri("/api/v1/products?page={page}&size=100", page)
                .retrieve()
                .body(CatalogPage.class);
    }

    @Retryable(
            maxAttemptsExpression = "${retry.api-server.max-attempts}",
            backoff = @Backoff(
                    delayExpression = "${retry.api-server.backoff.delay-ms}",
                    multiplierExpression = "${retry.api-server.backoff.multiplier}"
            )
    )
    public EnrichedSuggestion getProduct(Integer sku) {
        log.debug("Retrieving product {}", sku);
        ParameterizedTypeReference<Map<String, EnrichedSuggestion>> type =
                new ParameterizedTypeReference<>() {};
        return restClient.get()
                .uri("/api/v1/products/{sku}", sku)
                .retrieve()
                .body(type)
                .get("content");
    }
}
