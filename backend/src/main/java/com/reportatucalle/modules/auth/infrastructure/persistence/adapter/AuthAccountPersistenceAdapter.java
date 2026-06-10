package com.reportatucalle.modules.auth.infrastructure.persistence.adapter;

import com.reportatucalle.modules.auth.domain.entity.AuthAccount;
import com.reportatucalle.modules.auth.domain.repository.AuthAccountRepository;
import com.reportatucalle.modules.auth.infrastructure.persistence.entity.AuthAccountJpaEntity;
import com.reportatucalle.modules.auth.infrastructure.persistence.mapper.AuthPersistenceMapper;
import com.reportatucalle.modules.auth.infrastructure.persistence.repository.AuthAccountJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adaptador de persistencia: Implementa el puerto AuthAccountRepository.
 * 
 * Responsabilidades:
 * - Traducir llamadas del puerto a operaciones JPA (via mapper)
 * - Delegar a AuthAccountJpaRepository para DB
 * - Retornar entidades de dominio (AuthAccountDomain), no JPA
 */
@Component
@RequiredArgsConstructor
public class AuthAccountPersistenceAdapter implements AuthAccountRepository {
    
    private final AuthAccountJpaRepository jpaRepository;
    private final AuthPersistenceMapper mapper;
    
    @Override
    public AuthAccount save(AuthAccount authAccount) {
        AuthAccountJpaEntity jpaEntity = mapper.toJpaEntityForCreation(authAccount);
        AuthAccountJpaEntity saved = jpaRepository.save(jpaEntity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<AuthAccount> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(mapper::toDomain);
    }
    
    @Override
    public Optional<AuthAccount> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
    
    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
}
