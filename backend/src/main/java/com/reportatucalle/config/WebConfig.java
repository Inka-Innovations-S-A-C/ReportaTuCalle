package com.reportatucalle.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CONFIGURACIÓN DE RECURSOS ESTÁTICOS:
 * Le indica a Spring Boot que debe permitir el acceso HTTP directo a la carpeta 'uploads'
 * para que el frontend pueda renderizar las imágenes almacenadas localmente.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}