package com.reportatucalle.modules.category.infrastructure.persistence.mapper;

import com.reportatucalle.modules.category.domain.entity.AlgorithmType;
import com.reportatucalle.modules.category.domain.entity.Category;
import com.reportatucalle.modules.category.infrastructure.persistence.entity.CategoryJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper Manual para convertir entre la capa de Dominio y la capa de Persistencia.
 * 
 * Responsabilidades:
 * - Convertir CategoryJpaEntity (persistencia) → Category (dominio)
 * - Convertir Category (dominio) → CategoryJpaEntity (persistencia)
 * 
 * Esta clase actúa como barrera de traducción, asegurando que la capa de Dominio
 * nunca tenga contacto directo con las anotaciones JPA o detalles de persistencia.
 */
@Component
public class CategoryMapper {

    /**
     * Convierte una entidad JPA a una entidad de Dominio pura.
     * 
     * @param jpaEntity la entidad JPA de la base de datos
     * @return Category (Dominio puro, sin contaminación de JPA)
     */
    public Category toDomain(CategoryJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        return Category.builder()
                .id(jpaEntity.getId())
                .name(jpaEntity.getName())
                .description(jpaEntity.getDescription())
                .markerColor(jpaEntity.getMarkerColor())
                .algorithmType(AlgorithmType.valueOf(jpaEntity.getAlgorithmType()))
                .isActive(jpaEntity.getIsActive())
                .createdAt(jpaEntity.getCreatedAt())
                .updatedAt(jpaEntity.getUpdatedAt())
                .build();
    }

    /**
     * Convierte una entidad de Dominio a una entidad JPA para persistencia.
     * 
     * @param category la entidad de dominio
     * @return CategoryJpaEntity lista para ser persisted en la BD
     */
    public CategoryJpaEntity toJpaEntity(Category category) {
        if (category == null) {
            return null;
        }

        return CategoryJpaEntity.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .markerColor(category.getMarkerColor())
                .algorithmType(category.getAlgorithmType().name()) // Convierte Enum a String
                .isActive(category.getIsActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
