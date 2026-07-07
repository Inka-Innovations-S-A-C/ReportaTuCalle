package com.reportatucalle.modules.user.infrastructure.persistence.repository;

import com.reportatucalle.modules.user.infrastructure.persistence.entity.UserProfileJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

/**
 * JPA Repository: Operaciones específicas de persistencia JPA.
 * 
 * ✅ SOLO infraestructura: Extiende JpaRepository, usa @Repository
 * ✅ Trabaja con UserProfileJpaEntity (no domain)
 * ✅ Implementado por adapter que convierte a domain
 */
@Repository
public interface UserProfileJpaRepository extends JpaRepository<UserProfileJpaEntity, Long> {
    
    /**
     * Busca un perfil por ID de cuenta (referencia blanda).
     */
    Optional<UserProfileJpaEntity> findByAccountId(Long accountId);
    
    /**
     * Verifica si existe un perfil con ese ID de cuenta.
     */
    boolean existsByAccountId(Long accountId);
    
    /**
     * Encuentra los top ciudadanos ordenados por puntaje (desc).
     */
    List<UserProfileJpaEntity> findTop10ByOrderByCivicScoreDesc();
}
