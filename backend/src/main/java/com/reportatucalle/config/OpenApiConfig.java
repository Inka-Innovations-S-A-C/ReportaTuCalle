package com.reportatucalle.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Swagger/OpenAPI.
 * Genera documentación viva de la API para probar los endpoints
 * sin necesidad de Postman y facilitar la integración frontend-backend.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI reportaTuCalleOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ReportaTuCalle API")
                        .description("API para la plataforma ciudadana y clustering espacial de reportes.")
                        .version("1.0.0"));
    }
}