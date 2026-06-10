package com.reportatucalle.modules.user.domain.repository;

import com.reportatucalle.modules.user.domain.entity.UserProfile;
import java.util.Optional;

/**
 * Puerto de salida (Outbound Port): Contrato de persistencia para perfiles de usuario.
 * 
 * PURO: Sin @Repository, sin extends JpaRepository, sin Spring
 * Definido en domain: Independiente de infraestructura
 * Implementado por: UserProfilePersistenceAdapter en infrastructure
 *Referencia blanda: accountId vincula sin acoplar a módulo auth
 */
public interface UserProfileRepository {
    
    /**
     * Guarda un nuevo perfil de usuario.
     */
    UserProfile save(UserProfile userProfile);
    
    /**
     * Busca un perfil por ID.
     */
    Optional<UserProfile> findById(Long id);
    
    /**
     * Busca un perfil por su ID de cuenta (referencia blanda).
     * Puente entre módulos usando soft reference, no acoplamiento de código.
     */
    Optional<UserProfile> findByAccountId(Long accountId);
    
    /**
     * Verifica si existe un perfil con ese ID.
     */
    boolean existsById(Long id);
    
    /**
     * Verifica si existe un perfil vinculado a esa cuenta.
     */
    boolean existsByAccountId(Long accountId);
}