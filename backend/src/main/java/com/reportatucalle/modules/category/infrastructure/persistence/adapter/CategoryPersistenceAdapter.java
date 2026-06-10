package com.reportatucalle.modules.category.infrastructure.persistence.adapter;

import com.reportatucalle.modules.category.domain.entity.Category;
import com.reportatucalle.modules.category.domain.repository.CategoryRepository;
import com.reportatucalle.modules.category.infrastructure.persistence.mapper.CategoryMapper;
import com.reportatucalle.modules.category.infrastructure.persistence.repository.CategoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador de Persistencia para Category.
 * 
 * Implementa el puerto de salida CategoryRepository (del dominio),
 * proporcionando la implementación usando JPA.
 * 
 * Responsabilidades:
 * - Traducir llamadas del dominio a operaciones JPA
 * - Usar el mapper para convertir entre entidades
 * - Mantener el dominio completamente agnóstico a la persistencia
 * 
 * Patrón: Adaptador de Hexagonal Architecture
 */
@Component
@RequiredArgsConstructor
public class CategoryPersistenceAdapter implements CategoryRepository {
    
    private final CategoryJpaRepository jpaRepository;
    private final CategoryMapper categoryMapper;
    
    /**
     * Persiste una nueva categoría en la base de datos.
     */
    @Override
    public Category save(Category category) {
        var jpaEntity = categoryMapper.toJpaEntity(category);
        var savedJpaEntity = jpaRepository.save(jpaEntity);
        return categoryMapper.toDomain(savedJpaEntity);
    }
    
    /**
     * Obtiene una categoría por su ID.
     */
    @Override
    public Optional<Category> findById(Long id) {
        return jpaRepository.findById(id)
                .map(categoryMapper::toDomain);
    }
    
    /**
     * Obtiene todas las categorías activas, ordenadas por nombre.
     */
    @Override
    public List<Category> findAllActive() {
        return jpaRepository.findAllByIsActiveTrueOrderByNameAsc()
                .stream()
                .map(categoryMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * Busca una categoría por su nombre exacto.
     */
    @Override
    public Optional<Category> findByName(String name) {
        return jpaRepository.findByName(name)
                .map(categoryMapper::toDomain);
    }
    
    /**
     * Obtiene todas las categorías (incluso las inactivas).
     */
    @Override
    public List<Category> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(categoryMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * Valida si existe una categoría con un nombre específico.
     */
    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }
    
    /**
     * Elimina una categoría por su ID.
     */
    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
