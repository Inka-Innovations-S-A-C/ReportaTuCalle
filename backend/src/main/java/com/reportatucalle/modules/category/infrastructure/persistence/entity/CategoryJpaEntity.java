package com.reportatucalle.modules.category.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * Entidad de Persistencia JPA para Categoría.
 * 
 * Esta clase SOLO debe usarse en la capa de Infraestructura.
 * Contiene todas las anotaciones de JPA/Hibernate.
 * NO debe filtrarse hacia la capa de Dominio.
 * 
 * Mapeo de tipos de algoritmo:
 * - ROUTING: Problemas de enrutamiento (TSP/VRP) - ej: baches, basura
 * - FLOW: Problemas de flujo máximo - ej: fugas de agua
 * - CONNECTIVITY: Árbol de expansión mínima - ej: semáforos, postes
 * - NONE: Sin algoritmo específico, solo visualización en mapa
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "marker_color", length = 7)
    private String markerColor;

    @Column(name = "algorithm_type", nullable = false, length = 50)
    private String algorithmType; // Se almacena como String en BD, se convierte a Enum en el dominio

    @Column(name = "is_active", nullable = false)
    @Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
