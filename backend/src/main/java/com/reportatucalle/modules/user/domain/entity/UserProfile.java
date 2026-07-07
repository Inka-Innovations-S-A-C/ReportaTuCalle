package com.reportatucalle.modules.user.domain.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Entidad de Dominio pura: Perfil del ciudadano, datos personales.
 * 
 * PURO: Sin @Entity, @Table, @Column, sin JPA, sin Spring
 * Separado: UserProfileJpaEntity (infrastructure) maneja persistencia
 * Referencia blanda: accountId vincula sin importar del módulo Auth
 * SOLID: Una sola responsabilidad - datos del usuario
 */
@Getter
@Builder
public class UserProfile {
    
    private final Long id;
    private final Long accountId; // Referencia blanda (Soft Reference)
    private final String firstName;
    private final String lastName;
    private final String phone; // Opcional
    private final Integer civicScore;
    private final LocalDateTime createdAt;
    
    /**
     * Nombre completo del ciudadano.
     * Lógica de dominio: formateo de nombre.
     */
    public String getFullName() {
        return (firstName != null ? firstName : "")
                .concat(" ")
                .concat(lastName != null ? lastName : "")
                .trim();
    }
    
    /**
     * Valida si el perfil tiene datos completos requeridos.
     */
    public boolean isComplete() {
        return firstName != null && !firstName.isBlank()
                && lastName != null && !lastName.isBlank()
                && accountId != null;
    }
    
    /**
     * Valida si el teléfono está en formato válido (lógica de negocio).
     */
    public boolean isPhoneValid() {
        if (phone == null) return true; // Opcional
        return phone.matches("^\\+?\\d{7,15}$");
    }
}
