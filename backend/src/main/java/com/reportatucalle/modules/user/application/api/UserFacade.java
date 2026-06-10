package com.reportatucalle.modules.user.application.api;

import java.util.Optional;

/**
 * Façade Pública del módulo User.
 * 
 * Contrato que otros módulos pueden usar para interactuar con User.
 * NO expone repositorios o detalles internos.
 * Mantiene límite arquitectónico entre módulos.
 */
public interface UserFacade {
    
    /**
     * Obtiene el nombre completo del usuario.
     */
    Optional<String> getFullNameForUser(Long userId);
    
    /**
     * Verifica si un perfil de usuario existe.
     */
    boolean userExists(Long userId);
    
    /**
     * Verifica si existe un usuario vinculado a una cuenta.
     */
    boolean userExistsByAccountId(Long accountId);
    
    /**
     * Obtiene el ID del perfil de usuario por su accountId.
     */
    Optional<Long> getUserIdByAccountId(Long accountId);
}
