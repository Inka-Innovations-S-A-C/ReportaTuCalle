package com.reportatucalle.config;

import com.reportatucalle.shared.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración central de Spring Security 6.
 * Arquitectura Stateless orientada a APIs REST puras.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    private static final String[] PUBLIC_ENDPOINTS = {
            // Endpoints de registro y login (deben ser públicos para poder obtener el token)
            "/api/v1/auth/**",
            "/auth/**", // Sin api/v1 por si acaso context-path altera algo
            
            // Categorías: el frontend las necesita para el formulario de reportes
            "/api/v1/categories",
            "/api/v1/categories/**",

            // Reportes cercanos: el mapa público los consume sin login
            "/api/v1/reports/nearby",

            // Documentación de la API (Swagger)
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            
            ////IMPORTANTE: Carpeta de imágenes.
            // Sube la foto quien tiene Token, pero cualquiera puede verla en el navegador.
            "/uploads/**" 
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
