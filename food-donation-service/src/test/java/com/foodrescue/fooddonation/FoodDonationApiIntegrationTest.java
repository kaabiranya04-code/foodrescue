package com.foodrescue.fooddonation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** Tests d'integration REST : vrais controllers, vraie validation, base H2 en memoire. */
@SpringBootTest
@AutoConfigureMockMvc
class FoodDonationApiIntegrationTest {

    @Autowired
    private MockMvc mvc;

    private String post(String url, String json, int expectedStatus) throws Exception {
        return mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().is(expectedStatus))
                .andReturn().getResponse().getContentAsString();
    }

    private String createRestaurant() throws Exception {
        String body = post("/api/v1/restaurants",
                "{\"name\":\"Boulangerie El Baraka\",\"city\":\"Sfax\",\"contactEmail\":\"contact@elbaraka.tn\"}", 201);
        String id = JsonPath.read(body, "$.id");
        return id;
    }

    private String createBatch(String restaurantId, double kg) throws Exception {
        String body = post("/api/v1/food-batches",
                "{\"restaurantId\":\"" + restaurantId + "\",\"foodType\":\"khobz\",\"quantityKg\":" + kg
                        + ",\"expiresAt\":\"2099-01-01T00:00:00Z\"}", 201);
        String id = JsonPath.read(body, "$.id");
        return id;
    }

    private String createDonation(String batchId) throws Exception {
        String body = post("/api/v1/donations", "{\"batchId\":\"" + batchId + "\"}", 201);
        String id = JsonPath.read(body, "$.id");
        return id;
    }

    private double impactKg() throws Exception {
        String body = mvc.perform(get("/api/v1/impact")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Number kg = JsonPath.read(body, "$.kgSaved");
        return kg.doubleValue();
    }

    @Test
    void createRestaurant_withInvalidEmail_returns400() throws Exception {
        mvc.perform(post("/api/v1/restaurants").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"X\",\"city\":\"Sfax\",\"contactEmail\":\"pas-un-email\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.contactEmail").exists());
    }

    @Test
    void createBatch_forUnknownRestaurant_returns404() throws Exception {
        post("/api/v1/food-batches",
                "{\"restaurantId\":\"inconnu\",\"foodType\":\"khobz\",\"quantityKg\":10,"
                        + "\"expiresAt\":\"2099-01-01T00:00:00Z\"}", 404);
    }

    @Test
    void createBatch_withNegativeQuantity_returns400() throws Exception {
        String restaurantId = createRestaurant();
        post("/api/v1/food-batches",
                "{\"restaurantId\":\"" + restaurantId + "\",\"foodType\":\"khobz\",\"quantityKg\":-5,"
                        + "\"expiresAt\":\"2099-01-01T00:00:00Z\"}", 400);
    }

    @Test
    void createDonation_forUnknownBatch_returns404() throws Exception {
        post("/api/v1/donations", "{\"batchId\":\"inconnu\"}", 404);
    }

    @Test
    void secondDonationOnSameBatch_returns409() throws Exception {
        String batchId = createBatch(createRestaurant(), 15);
        createDonation(batchId);
        post("/api/v1/donations", "{\"batchId\":\"" + batchId + "\"}", 409);
    }

    @Test
    void completeDonation_withInvalidCharityId_returns400() throws Exception {
        String donationId = createDonation(createBatch(createRestaurant(), 10));
        post("/api/v1/donations/" + donationId + "/complete", "{\"charityId\":\"ABC\"}", 400);
    }

    @Test
    void fullDonationFlow_updatesImpact() throws Exception {
        double before = impactKg();
        String batchId = createBatch(createRestaurant(), 20);
        String donationId = createDonation(batchId);

        mvc.perform(get("/api/v1/food-batches/" + batchId))
                .andExpect(jsonPath("$.status").value("RESERVED"));

        post("/api/v1/donations/" + donationId + "/complete",
                "{\"charityId\":\"CH-0001\",\"receiptNumber\":\"REC-2026-TEST\"}", 200);

        mvc.perform(get("/api/v1/donations/" + donationId))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.charityId").value("CH-0001"));
        mvc.perform(get("/api/v1/food-batches/" + batchId))
                .andExpect(jsonPath("$.status").value("DONATED"));

        String impact = mvc.perform(get("/api/v1/impact")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Number kg = JsonPath.read(impact, "$.kgSaved");
        Number meals = JsonPath.read(impact, "$.mealsServed");
        Number co2 = JsonPath.read(impact, "$.co2AvoidedKg");
        assertThat(kg.doubleValue() - before).isCloseTo(20.0, within(0.01));
        assertThat(meals.doubleValue()).isCloseTo(kg.doubleValue() * 2.0, within(0.02));
        assertThat(co2.doubleValue()).isCloseTo(kg.doubleValue() * 2.5, within(0.02));

        post("/api/v1/donations/" + donationId + "/complete", "{\"charityId\":\"CH-0001\"}", 409);
    }

    @Test
    void openApiDocumentation_listsTheEndpoints() throws Exception {
        String docs = mvc.perform(get("/v3/api-docs")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(docs).contains("/api/v1/donations", "/api/v1/food-batches", "/api/v1/impact");
    }
}
