package com.reportatucalle.modules.category.domain.repository;

import com.reportatucalle.modules.category.domain.entity.Category;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de Salida: Contrato para la persistencia de Categorías.
 * 
 * IMPORTANTE - Arquitectura Hexagonal:
 * - Esta interfaz define el contrato que el DOMINIO espera
 * - El dominio NO conoce cómo se implementa (BD, archivo, cache, etc.)
 * - La implementación está en infrastructure/persistence/adapter
 * 
 * Responsabilidades:
 * - Definir operaciones CRUD necesarias para el negocio
 * - Operar SIEMPRE con Category (entidad de dominio), NUNCA con entidades JPA
 * - Mantener una barrera de inversión de dependencias
 * 
 * Nota: Esta interfaz es "pura" - no contiene anotaciones de Spring.
 * El @Repository se aplica en CategoryJpaRepository (infraestructura).
 */
public interface CategoryRepository {
    
    /**
     * Persiste una nueva categoría.
     * 
     * @param category la entidad de dominio a guardar
     * @return la categoría persistida (con ID asignado)
     */
    Category save(Category category);
    
    /**
     * Obtiene una categoría por su identificador único.
     * 
     * @param id el ID de la categoría
     * @return Optional con la categoría, o vacío si no existe
     */
    Optional<Category> findById(Long id);
    
    /**
     * Obtiene todas las categorías activas, ordenadas alfabéticamente.
     * 
     * Caso de uso: Llenar el desplegable (Select) en la UI.
     * 
     * @return lista de categorías activas ordenadas por nombre
     */
    List<Category> findAllActive();
    
    /**
     * Busca una categoría por su nombre exacto.
     * 
     * Caso de uso: Validar que no existan duplicados.
     * 
     * @param name el nombre a buscar
     * @return Optional con la categoría, o vacío si no existe
     */
    Optional<Category> findByName(String name);
    
    /**
     * Obtiene todas las categorías (incluso las inactivas).
     * 
     * @return lista con todas las categorías
     */
    List<Category> findAll();
    
    /**
     * Valida si existe una categoría con un nombre específico.
     * 
     * @param name el nombre a validar
     * @return true si existe, false si no
     */
    boolean existsByName(String name);
    
    /**
     * Elimina una categoría por su ID.
     * 
     * @param id el ID de la categoría a eliminar
     */
    void deleteById(Long id);
}