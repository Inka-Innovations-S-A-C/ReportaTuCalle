package com.reportatucalle.modules.user.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad JPA: Persistencia del perfil del usuario.
 * 
 * ✅ SOLO para infraestructura: Contiene @Entity, @Table, @Column
 * ✅ Convertida DESDE UserProfileDomain mediante UserMapper
 * ✅ Referencia suave (Soft Reference) a AuthAccount mediante accountId
 * ✅ Desacoplada del módulo Auth a nivel de código Java
 */
@Entity
@Table(name = "user_profiles")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "account_id", nullable = false, unique = true)
    private Long accountId; // Referencia blanda (no Foreign Key en Java)
    
    @Column(nullable = false, length = 100)
    private String firstName;
    
    @Column(nullable = false, length = 100)
    private String lastName;
    
    @Column(length = 20)
    private String phone;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
