package com.reportatucalle.modules.auth.domain.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Entidad de Dominio pura: Solo responsable de lógica de autenticación y validación.
 * 
 * PURO: Sin @Entity, @Table, @Column, sin JPA, sin Spring
 * Separado: AuthAccountJpaEntity (infrastructure) maneja persistencia
 * Enfoque: Dominio de negocio exclusivamente
 */
@Getter
@Builder
public class AuthAccount {
    
    private final Long id;
    private final String email;
    private final String passwordHash; // Ya encriptada, nunca exponer plaintext
    private final Role role;
    private final LocalDateTime createdAt;
    
    /**
     * Valida si el email es válido según reglas de negocio.
     * En hexagonal, la lógica de validación vive aquí.
     */
    public boolean isEmailValid() {
        return email != null && email.contains("@") && email.length() > 5;
    }
    
    /**
     * Verifica si la contraseña coincide (en práctica, el comparador haría esto).
     * Este método es un placeholder para operaciones de dominio.
     */
    public boolean isPasswordValid(String plainPassword, String encoder) {
        // En realidad, PasswordEncoder lo haría
        // Esto es solo para ilustrar lógica de dominio
        return plainPassword != null && !plainPassword.isBlank();
    }
    
    /**
     * Valida si la cuenta puede autenticar (reglas de negocio).
     */
    public boolean canAuthenticate() {
        return isEmailValid() && passwordHash != null && !passwordHash.isBlank();
    }
    
    /**
     * Retorna el nombre del rol para autorización.
     */
    public String getAuthorityName() {
        return "ROLE_" + role.name();
    }
}
