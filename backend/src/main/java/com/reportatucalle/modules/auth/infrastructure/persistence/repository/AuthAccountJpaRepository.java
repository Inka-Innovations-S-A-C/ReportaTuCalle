package com.reportatucalle.modules.auth.infrastructure.persistence.repository;

import com.reportatucalle.modules.auth.infrastructure.persistence.entity.AuthAccountJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA Repository: Operaciones específicas de persistencia JPA.
 * 
 * ✅ SOLO infraestructura: Extiende JpaRepository, usa @Repository
 * ✅ Trabaja con AuthAccountJpaEntity (no domain)
 * ✅ Implementado por adapter que convierte a domain
 */
@Repository
public interface AuthAccountJpaRepository extends JpaRepository<AuthAccountJpaEntity, Long> {
    
    /**
     * Busca una cuenta por email.
     */
    Optional<AuthAccountJpaEntity> findByEmail(String email);
    
    /**
     * Verifica si existe una cuenta con ese email.
     */
    boolean existsByEmail(String email);
}
