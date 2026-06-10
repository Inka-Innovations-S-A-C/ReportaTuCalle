package com.reportatucalle.modules.category.application.api;

import com.reportatucalle.modules.category.domain.entity.AlgorithmType;
import com.reportatucalle.modules.category.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementación del Facade Público para el Módulo Category.
 * 
 * Responsabilidades:
 * - Implementar las operaciones públicas expuestas por CategoryFacade
 * - Orquestar la lógica de negocio necesaria
 * - Traducir entre DTOs externos y entidades de dominio (si es necesario)
 * - Proporcionar un punto de entrada único al módulo
 * 
 * Notas sobre Arquitectura:
 * - Depende del puerto de salida CategoryRepository (inyectado)
 * - NO depende de detalles de implementación (JPA, etc.)
 * - Puede usarse directamente por otros módulos mediante inyección
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryFacadeImpl implements CategoryFacade {
    
    private final CategoryRepository categoryRepository;
    
    /**
     * Obtiene el tipo de algoritmo requerido para una categoría.
     * 
     * Implementación:
     * 1. Busca la categoría en el repositorio (puerto)
     * 2. Extrae el AlgorithmType del dominio
     * 3. Retorna el resultado envuelto en Optional
     */
    @Override
    public Optional<AlgorithmType> getAlgorithmTypeForCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .map(category -> category.getAlgorithmType());
    }
    
    /**
     * Obtiene el nombre de una categoría.
     * 
     * Implementación simple que accede al nombre de la categoría encontrada.
     */
    @Override
    public Optional<String> getCategoryName(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .map(category -> category.getName());
    }
    
    /**
     * Valida si una categoría existe y está activa.
     * 
     * Implementación:
     * 1. Busca la categoría por ID
     * 2. Verifica que exista y esté activa usando el método de dominio
     * 3. Retorna boolean
     */
    @Override
    public boolean isCategoryActive(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .filter(category -> category.isAvailable()) // Usa método de dominio
                .isPresent();
    }
}
