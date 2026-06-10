package com.reportatucalle.modules.auth.domain.repository;

import com.reportatucalle.modules.auth.domain.entity.AuthAccount;
import java.util.Optional;

/**
 * Puerto de salida (Outbound Port): Contrato de persistencia para autenticación.
 * 
 * ✅ PURO: Sin @Repository, sin extends JpaRepository, sin Spring
 * ✅ Definido en domain: Independiente de infraestructura
 * ✅ Implementado por: AuthAccountPersistenceAdapter en infrastructure
 * 
 * Inversión de dependencias: Infrastructure depende del domain
 */
public interface AuthAccountRepository {
    
    /**
     * Guarda una nueva cuenta de autenticación.
     */
    AuthAccount save(AuthAccount authAccount);
    
    /**
     * Busca una cuenta por email.
     */
    Optional<AuthAccount> findByEmail(String email);
    
    /**
     * Busca una cuenta por ID.
     */
    Optional<AuthAccount> findById(Long id);
    
    /**
     * Verifica si existe una cuenta con ese email.
     */
    boolean existsByEmail(String email);
    
    /**
     * Verifica si existe una cuenta con ese ID.
     */
    boolean existsById(Long id);
}