package com.bistro.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bistroOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bistro Reservations API")
                        .description("API para la gestión del ciclo de vida de reservas de un restaurante.")
                        .version("1.0.0"));
    }
}
