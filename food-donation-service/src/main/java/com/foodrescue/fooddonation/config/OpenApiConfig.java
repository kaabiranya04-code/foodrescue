package com.foodrescue.fooddonation.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI foodRescueOpenApi() {
        return new OpenAPI().info(new Info()
                .title("FoodRescue - food-donation-service")
                .version("v1")
                .description("API REST de la plateforme de reduction du gaspillage alimentaire."));
    }
}
