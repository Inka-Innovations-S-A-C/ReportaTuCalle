package com.reportatucalle.modules.category.infrastructure.persistence.repository;

import com.reportatucalle.modules.category.infrastructure.persistence.entity.CategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para acceso a datos de Categorías.
 * 
 * IMPORTANTE: Este repositorio SOLO debe usarse en la capa de Infraestructura.
 * Trabaja exclusivamente con CategoryJpaEntity (la entidad de persistencia).
 * 
 * El Dominio NO tiene dependencia a este repositorio.
 * El Dominio solo conoce la interfaz de puerto CategoryRepository (en domain/repository).
 * Los adaptadores en la infraestructura implementan esos puertos.
 */
@Repository
public interface CategoryJpaRepository extends JpaRepository<CategoryJpaEntity, Long> {
    
    /**
     * Encuentra todas las categorías activas, ordenadas alfabéticamente por nombre.
     * Usado para llenar el desplegable (Select) del frontend.
     */
    List<CategoryJpaEntity> findAllByIsActiveTrueOrderByNameAsc();
    
    /**
     * Encuentra una categoría por su nombre exacto.
     * Usado para validaciones al crear o actualizar categorías.
     */
    Optional<CategoryJpaEntity> findByName(String name);
    
    /**
     * Valida si existe una categoría con un nombre específico.
     */
    boolean existsByName(String name);
}
