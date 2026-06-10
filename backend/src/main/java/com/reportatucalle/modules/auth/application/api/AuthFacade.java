package com.reportatucalle.modules.auth.application.api;

import java.util.Optional;

/**
 * Façade Pública del módulo Auth.
 * 
 * Contrato que otros módulos pueden usar para interactuar con Auth.
 * NO expone repositorios o detalles internos.
 * Mantiene límite arquitectónico entre módulos.
 */
public interface AuthFacade {
    
    /**
     * Obtiene el email de una cuenta autenticada.
     */
    Optional<String> getEmailForAccount(Long accountId);
    
    /**
     * Verifica si una cuenta existe.
     */
    boolean accountExists(Long accountId);
    
    /**
     * Obtiene el rol de una cuenta.
     */
    Optional<String> getRoleForAccount(Long accountId);
}
