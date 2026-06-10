package com.reportatucalle.modules.category.application.api;

import com.reportatucalle.modules.category.domain.entity.AlgorithmType;

import java.util.Optional;

/**
 * Facade Público para el Módulo Category.
 * 
 * Contrato de API que otros módulos pueden usar para interactuar
 * con la lógica de negocio de categorías.
 * 
 * Esta interfaz DEFINE las operaciones que el módulo category
 * expone públicamente a otros módulos (optimization, report, etc.)
 * 
 * Ventajas:
 * - Desacoplamiento entre módulos
 * - Control de qué se expone públicamente
 * - Punto único de entrada al módulo
 * - Fácil cambio de implementación sin afectar consumidores
 */
public interface CategoryFacade {
    
    /**
     * Obtiene el tipo de algoritmo requerido para una categoría específica.
     * 
     * Caso de uso: El módulo 'optimization' necesita saber qué tipo de
     * algoritmo ejecutar para los reportes de una categoría.
     * 
     * @param categoryId el ID de la categoría
     * @return Optional con el tipo de algoritmo, o vacío si la categoría no existe
     */
    Optional<AlgorithmType> getAlgorithmTypeForCategory(Long categoryId);
    
    /**
     * Obtiene el nombre de una categoría.
     * 
     * @param categoryId el ID de la categoría
     * @return Optional con el nombre, o vacío si no existe
     */
    Optional<String> getCategoryName(Long categoryId);
    
    /**
     * Valida si una categoría existe y está activa.
     * 
     * @param categoryId el ID a validar
     * @return true si existe y está activa, false en caso contrario
     */
    boolean isCategoryActive(Long categoryId);
}
