package com.storerecommender.integration;

import com.storerecommender.apiserver.ApiServerApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ApiServerApplication.class)
@AutoConfigureMockMvc
class ProductControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getProductGivenExistingSkuReturns200AndProductDetails() throws Exception {
        mockMvc.perform(get("/api/v1/products/1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.sku").value(1001));
    }

    @Test
    void getProductGivenNonExistingSkuReturns404() throws Exception {
        mockMvc.perform(get("/api/v1/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCatalogReturns200AndPageOfResults() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(321));
    }

    @Test
    void getCatalogReturns200AndHasSevenPages() throws Exception {
        mockMvc.perform(get("/api/v1/products?page=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(7));
    }

    @Test
    void getCatalogReturns200AndPageSevenHas21Products() throws Exception {

        mockMvc.perform(get("/api/v1/products?page=7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfElements").value(21));
    }
}
