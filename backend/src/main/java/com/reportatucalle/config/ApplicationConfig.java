package com.reportatucalle.config;

import com.reportatucalle.modules.auth.infrastructure.persistence.repository.AuthAccountJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuración central de los Beans de Spring Security.
 * 
 * NOTA ARQUITECTÓNICA:
 * Spring Security requiere UserDetails para el filtro de autenticación.
 * AuthAccountJpaEntity implementa UserDetails (bridge con infrastructure).
 * AuthAccountDomain permanece puro (no UserDetails).
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final AuthAccountJpaRepository authAccountJpaRepository;

    /**
     * Define cómo Spring Security buscará a los usuarios.
     * Usa el JpaRepository directamente para retornar UserDetails (AuthAccountJpaEntity).
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> authAccountJpaRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado en la base de datos"));
    }

    /**
     * Proveedor de autenticación que usa nuestra base de datos y nuestro encriptador.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;

    }

    /**
     * El gestor principal de autenticación que usamos en el AuthService para el login.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Algoritmo de encriptación estándar de la industria (BCrypt).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}